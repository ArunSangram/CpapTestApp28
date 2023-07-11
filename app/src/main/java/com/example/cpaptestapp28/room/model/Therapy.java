package com.example.cpaptestapp28.room.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "therapy")
public class Therapy {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String deviceAddress;
    private int mode; // 0 -auto mode ,1 - manual mode
    private float startPressure; // 4-20 cmH20
    private float maxPressure; // 5-20 cmH20
    private float minPressure; // 4-19cmH20
    private int rampTime; // 0-60 mins
    private int maskType; // 0-10
    private int therapyTime; // 60 - 480 mins
    private long addedOn;
    private long startTime;
    private int status; // 1 - pass ,-1 failed
    private int therapyNumber;

    public Therapy() {
    }

    public Therapy(String deviceAddress, int mode,
                   float startPressure, float maxPressure,
                   float minPressure, int rampTime,
                   int maskType, int therapyTime,
                   long addedOn) {
        this.deviceAddress = deviceAddress;
        this.mode = mode;
        this.startPressure = startPressure;
        this.maxPressure = maxPressure;
        this.minPressure = minPressure;
        this.rampTime = rampTime;
        this.maskType = maskType;
        this.therapyTime = therapyTime;
        this.addedOn = addedOn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public float getStartPressure() {
        return startPressure;
    }

    public void setStartPressure(float startPressure) {
        this.startPressure = startPressure;
    }

    public float getMaxPressure() {
        return maxPressure;
    }

    public void setMaxPressure(float maxPressure) {
        this.maxPressure = maxPressure;
    }

    public float getMinPressure() {
        return minPressure;
    }

    public void setMinPressure(float minPressure) {
        this.minPressure = minPressure;
    }

    public int getRampTime() {
        return rampTime;
    }

    public void setRampTime(int rampTime) {
        this.rampTime = rampTime;
    }

    public int getMaskType() {
        return maskType;
    }

    public void setMaskType(int maskType) {
        this.maskType = maskType;
    }

    public int getTherapyTime() {
        return therapyTime;
    }

    public void setTherapyTime(int therapyTime) {
        this.therapyTime = therapyTime;
    }

    public long getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(long addedOn) {
        this.addedOn = addedOn;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTherapyNumber() {
        return therapyNumber;
    }

    public void setTherapyNumber(int therapyNumber) {
        this.therapyNumber = therapyNumber;
    }

    @Override
    public String toString() {
        return "Therapy{" +
                "id=" + id +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", mode=" + mode +
                ", startPressure=" + startPressure +
                ", maxPressure=" + maxPressure +
                ", minPressure=" + minPressure +
                ", rampTime=" + rampTime +
                ", maskType=" + maskType +
                ", therapyTime=" + therapyTime +
                ", addedOn=" + addedOn +
                ", startTime=" + startTime +
                ", status=" + status +
                '}';
    }
}
