package com.example.cpaptestapp28.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import timber.log.Timber;

public class BluetoothUtils {
    private static final String TAG = "cpap_BleUTILS";

    @SuppressLint("MissingPermission")
    public static boolean isConnectedToBluetoothDevice(Context context, String deviceAddress) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if Bluetooth is supported on the device
        if (bluetoothAdapter == null) {
            Timber.tag(TAG).d("isConnectedToBluetoothDevice: bluetooth adapter is null");
            return false;
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            Timber.tag(TAG).d("isConnectedToBluetoothDevice: bluetooth not enabled ");
            return false;
        }

        // Get the list of currently connected devices
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if (device.getAddress().equals(deviceAddress)) {
                return true; // Found the device in the list of connected devices
            }
        }

        return false; // Device not found in the list of connected devices
    }
}
