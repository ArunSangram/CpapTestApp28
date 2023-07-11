package com.example.cpaptestapp28.prefs;

import android.content.SharedPreferences;

import com.example.cpaptestapp28.ApplicationControl;
import com.example.cpaptestapp28.utils.KEYS;

public class DevicePrefs {
    public static void storeDeviceInfo(String deviceName,String deviceAddress){
        SharedPreferences.Editor editor = ApplicationControl.getPreferences().edit();
        editor.putString(KEYS.DEVICE_NAME,deviceName);
        editor.putString(KEYS.DEVICE_ADDRESS,deviceAddress);
        editor.apply();
    }


    public static void removeDeviceInfo(){
        SharedPreferences.Editor editor = ApplicationControl.getPreferences().edit();
        editor.putString(KEYS.DEVICE_NAME,"");
        editor.putString(KEYS.DEVICE_ADDRESS,"");
        editor.apply();
    }

    public static String getDeviceName(){
        SharedPreferences preferences = ApplicationControl.getPreferences();
        return preferences.getString(KEYS.DEVICE_NAME,"");
    }

    public static String getDeviceAddress(){
        SharedPreferences preferences = ApplicationControl.getPreferences();
        return preferences.getString(KEYS.DEVICE_ADDRESS,"");
    }
}
