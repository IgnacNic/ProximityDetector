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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.escaner.services.ScannerService;
import com.escaner.utils.FileUtils;
import com.example.escaner.R;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.log.LogModule;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.BIND_AUTO_CREATE;

public class SearchFragment extends Fragment implements MokoScanDeviceCallback {



    private boolean isFiltered;
    private String macFilter;
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
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        ButterKnife.bind(this, view);
        FileUtils.storage = Objects.requireNonNull(getActivity()).getExternalFilesDir(null);
        isFiltered = true;
        macFilter = "";
        Intent intent = new Intent(getActivity(), ScannerService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        return view;
    }


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    @OnClick({R.id.iv_refresh})
    public void onViewClicked(View view) {
    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onScanDevice(DeviceInfo device) {
        LogModule.i(device.toString());
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
