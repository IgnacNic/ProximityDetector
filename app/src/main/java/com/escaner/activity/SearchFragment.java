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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.escaner.entity.ScannerDevice;
import com.escaner.services.ScannerService;
import com.example.escaner.R;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.log.LogModule;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.BIND_AUTO_CREATE;

public class SearchFragment extends Fragment implements MokoScanDeviceCallback {

    @Bind(R.id.tv_device_mac)
    TextView tvMac;
    @Bind(R.id.tv_device_name)
    TextView tvName;
    @Bind(R.id.rl_scan_btn)
    RelativeLayout rlScanBtn;
    @Bind(R.id.tv_scan_btn)
    TextView tvScanBtn;
    @Bind(R.id.tv_device_saved)
    TextView tvDeviceSaved;

    private String name;
    private String mac;
    private boolean saved;
    private ScannerService scannerService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            scannerService = ((ScannerService.LocalBinder) service).getService();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.setPriority(100);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.bind(this, view);
        Intent intent = new Intent(getActivity(), ScannerService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        return view;
    }


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    public void setDevice(ScannerDevice device) {
        mac = device.getMac();
        name = device.getName() == null || device.getName().equalsIgnoreCase("null") ? mac : device.getName();
        saved = device.isSaved();
        repaint();
    }

    private void repaint() {
        tvMac.setText(mac);
        tvName.setText(name);
        MokoSupport.getInstance().stopScanDevice();
        rlScanBtn.setBackgroundResource(R.drawable.shape_button_bg);
        tvScanBtn.setText(R.string.escanear);
        tvScanBtn.setTextColor(getResources().getColor(R.color.white_fffefe));
        if(saved)
            tvDeviceSaved.setText(R.string.guardado);
    }


//    @OnClick()
//    public void onViewClicked(View view) {
//    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onScanDevice(DeviceInfo device) {
        LogModule.i("Search fragment\n" + device.toString());
    }

    @Override
    public void onStopScan() {

    }

    private void startScan(int seconds) {
        scannerService.startScanDevices(this);
        scannerService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerService.stopScanDevices();
            }
        }, 1000 * seconds);
    }


}
