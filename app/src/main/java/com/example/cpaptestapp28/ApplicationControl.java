package com.example.cpaptestapp28;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;

import com.example.cpaptestapp28.utils.DeviceStatus;
import com.example.cpaptestapp28.utils.KEYS;

import timber.log.Timber;

public class ApplicationControl extends Application {
    private static ApplicationControl instance;
    private static SharedPreferences preferences;
    private static int deviceStatus = DeviceStatus.DEVICE_STATUS_NO_DEVICE;
    public static final String CPAP_NOTIFICATION_CHANNEL_ID = "cpap_notification";

    public static int CPAP_STAGE = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferences = getSharedPreferences(KEYS.PREFS_ID, MODE_PRIVATE);
        Timber.plant(new Timber.DebugTree());
        createNotificationChannel();
    }

    public static ApplicationControl getInstance() {
        return instance;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static void setDeviceAvailableStatus(int value) {
        deviceStatus = value;
    }

    public static int getDeviceAvailableStatus() {
        return deviceStatus;
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CPAP_NOTIFICATION_CHANNEL_ID, "cpap_channel_id",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }
}
