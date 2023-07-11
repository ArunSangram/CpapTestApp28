package com.example.cpaptestapp28.global;

import com.example.cpaptestapp28.utils.DeviceStatus;

public class SingDeviceStatus {
    private static SingDeviceStatus instance;
    private boolean isDevicePresent;
    private int connectionStatus;
    private boolean isTherapyOn;
    private int therapyID;

    private SingDeviceStatus() {
        this.isDevicePresent = false;
        this.connectionStatus = DeviceStatus.DEVICE_STATUS_NO_DEVICE;
        this.isTherapyOn = false;
        this.therapyID = -1;
    }

    public SingDeviceStatus getInstance() {
        if (instance == null) {
            instance = new SingDeviceStatus();
        }
        return instance;
    }

    public boolean isDevicePresent() {
        return isDevicePresent;
    }

    public void setDevicePresent(boolean devicePresent) {
        isDevicePresent = devicePresent;
    }

    public int getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(int connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public boolean isTherapyOn() {
        return isTherapyOn;
    }

    public void setTherapyOn(boolean therapyOn) {
        isTherapyOn = therapyOn;
    }

    public int getTherapyID() {
        return therapyID;
    }

    public void setTherapyID(int therapyID) {
        this.therapyID = therapyID;
    }
}
