package com.escaner.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ListView;

import com.escaner.adapter.BeaconListAdapter;
import com.escaner.entity.ScannerDevice;
import com.escaner.services.ScannerService;
import com.example.escaner.R;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.log.LogModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MokoScanDeviceCallback {

    @Bind(R.id.lv_devices)
    ListView lvDevices;

    private HashMap<String, ScannerDevice> devicesDiscovered;
    private ArrayList<ScannerDevice> devices;
    private boolean isFiltered;
    private boolean isScanning;
    private String macFilter;
    private ScannerService scannerService;
    private BeaconListAdapter adapter;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            scannerService = ((ScannerService.LocalBinder) service).getService();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.setPriority(100);
            startScan(60);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        devices = new ArrayList<>();
        devicesDiscovered = new HashMap<>();
        isFiltered = true;
        macFilter = "DF:EF";
        adapter = new BeaconListAdapter(this);
        adapter.setItems(devices);
        lvDevices.setAdapter(adapter);

        Intent intent = new Intent(this, ScannerService.class);
        startService(intent);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onStartScan() {
        devicesDiscovered.clear();
    }

    @Override
    public void onScanDevice(DeviceInfo device) {
        if (isFiltered) {
            if (!device.mac.contains(macFilter))
                return;
        }
        LogModule.i(device.toString());
        ScannerDevice saved = devicesDiscovered.get(device.mac);
        if(saved != null){
            if(saved.getBattery() == 0) saved.setBattery(device.battery);
            if(saved.getName() == null || saved.getName().equalsIgnoreCase("null") || saved.getName().isEmpty()) saved.setName(device.name);
        }
        else {
            ScannerDevice d = new ScannerDevice(device.mac, device.battery, device.name, device.rssi);
            devicesDiscovered.put(d.getMac(), d);
        }
        updateDevices();
    }

    private void updateDevices() {
        devices.clear();
        devices.addAll(devicesDiscovered.values());
        Collections.sort(devices, new Comparator<ScannerDevice>() {
            @Override
            public int compare(ScannerDevice o1, ScannerDevice o2) {
                return Integer.compare(o2.getRssi(), o1.getRssi());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStopScan() {

    }

    private void startScan(int seconds) {
        isScanning = true;
        scannerService.startScanDevices(this);
        scannerService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerService.stopScanDevices();
            }
        }, 1000 * seconds);
    }
}
