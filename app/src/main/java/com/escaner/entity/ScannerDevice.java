package com.escaner.entity;

public class ScannerDevice {

    private String mac;
    private int battery;
    private String name;
    private int rssi;
    private boolean saved = false;


    public ScannerDevice(String macAdd, int battery, String name, int rssi) {
        this.mac = macAdd;
        this.battery = battery;
        this.name = name;
        this.rssi = rssi;
        saved = false;
    }


    public String getMac() {
        return mac;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRssi() {
        return rssi;
    }

    @Override
    public String toString() {
        return (name == null ? "Unnamed: \n" : name.toUpperCase() + ": \n") +
                "\t\tMac addr.: " + mac.toUpperCase() + ",\n" +
                "\t\tBattery:   " + battery + "%,\n" +
                "\t\tAdv RSSI:  " + rssi + ".\n";
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
