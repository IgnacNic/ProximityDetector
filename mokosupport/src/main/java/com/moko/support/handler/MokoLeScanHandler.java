package com.moko.support.handler;

import android.bluetooth.BluetoothDevice;

import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.utils.MokoUtils;

import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * @Date 2017/12/12 0012
 * @Author wenzheng.liu
 * @Description Search device callback class
 * @ClassPath com.moko.support.handler.MokoLeScanHandler
 */
public class MokoLeScanHandler extends ScanCallback {
    private MokoScanDeviceCallback callback;

    public MokoLeScanHandler(MokoScanDeviceCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if (result != null) {
            BluetoothDevice device = result.getDevice();
            byte[] scanRecord = result.getScanRecord().getBytes();
            int rssi = result.getRssi();
            if (scanRecord.length == 0 || rssi < -127 || rssi == 127) {
                return;
            }
            DeviceInfo deviceInfo = new DeviceInfo();
            String name = parseName(scanRecord);
            if(name == null)
                deviceInfo.name = device.getName();
            else
                deviceInfo.name = name;
            deviceInfo.rssi = rssi;
            deviceInfo.mac = device.getAddress();
            String scanRecordStr = MokoUtils.bytesToHexString(scanRecord);
            deviceInfo.scanRecord = scanRecordStr;
            deviceInfo.battery = parseBattery(scanRecord);
            callback.onScanDevice(deviceInfo);
        }
    }//45 -> 2D

    private String parseName(byte[] scanRecord) {
        String aux = MokoUtils.bytesToHexString(scanRecord);
        boolean isMokoBeacon;
        int length = 0;
        if (((int) scanRecord[5] & 0xff) == 0x10 && ((int) scanRecord[6] & 0xff) == 0xFF) {
            length = (int) scanRecord[3];
            isMokoBeacon = true;
        } else if (((int) scanRecord[5] & 0xff) == 0xE1 && ((int) scanRecord[6] & 0xff) == 0xFF) {
            length = (int) scanRecord[7];
            isMokoBeacon = false;
        } else
            return null;

        if (isMokoBeacon){
            return MokoUtils.hex2String(aux.substring(36,length*2 + 8));
        }
        else {
            return MokoUtils.hex2String(aux.substring(40,length*2 + 16));
        }
    }


    private int parseBattery(byte[] scanRecord) {
        boolean isMokoBeacon;
        if (((int) scanRecord[5] & 0xff) == 0x10 && ((int) scanRecord[6] & 0xff) == 0xFF) {
            isMokoBeacon = true;
        } else if (((int) scanRecord[5] & 0xff) == 0xE1 && ((int) scanRecord[6] & 0xff) == 0xFF) {
            isMokoBeacon = false;
        } else
            return 0;
        if (isMokoBeacon)
            return (int) scanRecord[18];
        else
            return (int) scanRecord[13];
    }
}