package com.moko.support.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.utils.MokoUtils
 */
@SuppressWarnings({"JavaDoc", "SpellCheckingInspection"})
public class MokoUtils {

    /**
     * A - Intensidad de la señal cuando el transmisor y el receptor están separados por 1 metro
     */
    private static final double n_Value = 2.7;/* n - propagation constant (empty space = 2) */

    /**
     * @Date 2017/12/11 0011
     * @Author wenzheng.liu
     * @Description 根据Rssi获得返回的距离, 返回数据单位为m
     */
    public static double getDistance(int rssi, int acc) {
        int iRssi = Math.abs(rssi);
        double power = (-iRssi - acc) / (10 * n_Value);
        return Math.pow(10, power);
    }

    public static String int2HexString(int b) {
        return String.format("%02X", b);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    // Convert a utf-8 charSequence to hexadecimal String
    public static String string2Hex(String s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str.append(s4);
        }
        return str.toString();
    }

    // Convert hex string to utf-8 charSequence
    public static String hex2String(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                s = new String(baKeyword, StandardCharsets.UTF_8);//UTF-16le:Not
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static byte[] hex2bytes(String hex) {
        if (hex.length() % 2 == 1) {
            hex = "0" + hex;
        }
        byte[] data = new byte[hex.length() / 2];
        for (int i = 0; i < data.length; i++) {
            try {
                data[i] = (byte) (0xff & Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private static SparseArray<String> charProperties = new SparseArray<>();

    static {
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_BROADCAST, "BROADCAST");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS, "EXTENDED_PROPS");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_INDICATE, "INDICATE");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_NOTIFY, "NOTIFY");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_READ, "READ");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE, "SIGNED_WRITE");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE, "WRITE");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, "WRITE_NO_RESPONSE");
    }

    public static String getCharPropertie(int property) {
        return getSparseArrayValue(charProperties, property);
    }

    private static String getSparseArrayValue(SparseArray<String> hashMap, int number) {
        String result = hashMap.get(number);
        if (TextUtils.isEmpty(result)) {
            List<Integer> numbers = getElement(number);
            StringBuilder resultStr = new StringBuilder();
            for (int i = 0; i < numbers.size(); i++) {
                resultStr.append(hashMap.get(numbers.get(i)));
                if (i < numbers.size() - 1) {
                    resultStr.append("|");
                }
            }
            return resultStr.toString();
        }
        return result;
    }

    /**
     * Inverse function of bit operation result -> 2 | 8;
     */
    static private List<Integer> getElement(int number) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            int b = 1 << i;
            if ((number & b) > 0)
                result.add(b);
        }

        return result;
    }
}
