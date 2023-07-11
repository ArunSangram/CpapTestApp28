package com.example.cpaptestapp28.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.location.LocationManagerCompat;

import com.example.cpaptestapp28.prefs.DevicePrefs;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import timber.log.Timber;

public class ScanManager {
    private static final String TAG = "cpap_ScanManager";
    private boolean isScanTimeOut = false;
    private boolean isCountDownRunning = false;
    private Context context;
    private Handler handler;
    private boolean deviceFound = false;

    public ScanManager(Context context) {
        this.context = context;
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());
    }

    private Runnable runnableScanTimeOut = new Runnable() {
        @Override
        public void run() {
            isScanTimeOut = false;
            stopScan();
        }
    };


    /**
     * ************************ SCAN FOR DEVICE *********************************
     */
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String deviceAddress = DevicePrefs.getDeviceAddress();
    private final int SCAN_TIMEOUT = 10000;
    private boolean isScanning;
    private BluetoothLeScannerCompat bluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner();
    private List<ScanFilter> scanFilterList = new ArrayList<>();
    private ScanSettings scanSettings;

    private void startScan() {
        if (deviceAddress.isEmpty()) {
            Timber.tag(TAG).d("startScan: device is not present");
            return;
        }
        if (!isScanning) {
            Timber.tag(TAG).d("startScan: deviceAddress : %s", deviceAddress);
            if (!checkBluetooth()) {
                Timber.tag(TAG).d("startScan: bluetooth not enabled");
                // TODO inform user
                return;
            }
            if (!checkLocation(context)) {
                Timber.tag(TAG).d("startScan: location not enabled");
                // TODO inform user
                return;
            }
            setScanFilters();
            setScanSettings();
            isScanTimeOut = true;
            handler.postDelayed(runnableScanTimeOut, SCAN_TIMEOUT);
            isScanning = true;
            bluetoothLeScannerCompat.startScan(scanFilterList, scanSettings, scanCallback);
        }
    }

    private void stopScan() {
        Timber.tag(TAG).d("stopScan: ");
        if (isScanTimeOut) {
            isScanTimeOut = false;
            handler.removeCallbacks(runnableScanTimeOut);
        }
        if (isScanning) {
            bluetoothLeScannerCompat.stopScan(scanCallback);
            isScanning = false;
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
            if (!deviceFound) {
                stopScan();
                deviceFound = true;
                Timber.tag(TAG).d("onScanResult: device found");
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Timber.tag(TAG).d("onScanFailed: %s", errorCode);
        }
    };

    /**
     * ****************** CHECK BLUETOOTH , LOCATION *****************
     */
    private boolean checkBluetooth() {
        return bluetoothAdapter.isEnabled();
    }

    private boolean checkLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }


    /**
     * ***************** SCAN FILTERS , SETTINGS ************************
     */
    private void setScanSettings() {
        scanSettings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
//                .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
//                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
//                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
//                .setReportDelay(0)
                .build();
    }

    private void setScanFilters() {
        scanFilterList.clear();
        scanFilterList.add(new ScanFilter.Builder().setDeviceAddress(deviceAddress).build());
    }

}
