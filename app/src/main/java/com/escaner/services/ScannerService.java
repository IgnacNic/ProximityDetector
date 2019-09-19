package com.escaner.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.handler.BaseMessageHandler;

public class ScannerService extends Service {
    public ServiceHandler mHandler;
    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new ServiceHandler(this);
    }

    public void startScanDevices(MokoScanDeviceCallback callback) {
        MokoSupport.getInstance().startScanDevice(callback);
    }

    public void stopScanDevices() {
        MokoSupport.getInstance().stopScanDevice();
    }

    public static class ServiceHandler extends BaseMessageHandler<ScannerService> {

        ServiceHandler(ScannerService service) {
            super(service);
        }

        @Override
        protected void handleMessage(ScannerService service, Message msg) {
        }
    }

    public class LocalBinder extends Binder {
        public ScannerService getService() {
            return ScannerService.this;
        }
    }
}
