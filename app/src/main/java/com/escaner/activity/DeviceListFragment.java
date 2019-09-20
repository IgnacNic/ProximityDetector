package com.escaner.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.escaner.adapter.BeaconListAdapter;
import com.escaner.entity.ScannerDevice;
import com.escaner.services.ScannerService;
import com.escaner.utils.FileUtils;
import com.escaner.utils.Utils;
import com.example.escaner.R;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.log.LogModule;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.BIND_AUTO_CREATE;

public class DeviceListFragment extends Fragment implements MokoScanDeviceCallback {


    @Bind(R.id.lv_devices)
    ListView lvDevices;
    @Bind(R.id.iv_refresh)
    ImageView ivRefresh;

    private HashMap<String, ScannerDevice> devicesDiscovered;
    private ArrayList<ScannerDevice> devices;
    private boolean isFiltered;
    private String macFilter;
    private ScannerService scannerService;
    private BeaconListAdapter adapter;
    private Animation animation = null;
    private HashMap<String, ScannerDevice> storedDevices;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        ButterKnife.bind(this, view);
        FileUtils.storage = Objects.requireNonNull(getActivity()).getExternalFilesDir(null);
        storedDevices = new HashMap<>();
        devices = new ArrayList<>();
        devicesDiscovered = new HashMap<>();
        isFiltered = false;
        macFilter = "A1:30";
        adapter = new BeaconListAdapter(getActivity());
        adapter.setListener((MainActivity) getActivity());
        adapter.setItems(devices);
        lvDevices.setAdapter(adapter);
        Intent intent = new Intent(getActivity(), ScannerService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        try {
            FileUtils.loadProximityList(storedDevices, Utils.STORED_DEVICES_FILE_NAME);
        } catch (FileNotFoundException ignored) {
        }
        return view;
    }


    public static DeviceListFragment newInstance() {
        return new DeviceListFragment();
    }


    @OnClick({R.id.iv_refresh})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_refresh) {
            if (animation == null) {
                startScan(50);
            } else {
                scannerService.stopScanDevices();
                onStopScan();
            }
        }
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
        ScannerDevice discovered = devicesDiscovered.get(device.mac);
        ScannerDevice saved = storedDevices.get(device.mac);
        if (saved != null) {
            device.name = saved.getName();
        }
        if (discovered != null) {
            if (discovered.getBattery() == 0)
                discovered.setBattery(device.battery);
            if (discovered.getName() == null || discovered.getName().equalsIgnoreCase("null") || discovered.getName().isEmpty())
                discovered.setName(device.name);
            discovered.setRssi(device.rssi);
            LogModule.i(discovered.toString());
        } else {
            ScannerDevice d = new ScannerDevice(device.mac, device.battery, device.name, device.rssi);
            devicesDiscovered.put(d.getMac(), d);
            LogModule.i(d.toString());
        }
        updateDevices();
    }

    @Override
    public void onStopScan() {
        ivRefresh.clearAnimation();
        animation = null;
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

    private void startScan(int seconds) {
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_refresh);
        ivRefresh.startAnimation(animation);
        scannerService.startScanDevices(this);
        devicesDiscovered.clear();
        scannerService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerService.stopScanDevices();
            }
        }, 1000 * seconds);
    }


    public void setListener(BeaconListAdapter.OnPlayListener onPlayListener){
        adapter.setListener(onPlayListener);
    }

}
