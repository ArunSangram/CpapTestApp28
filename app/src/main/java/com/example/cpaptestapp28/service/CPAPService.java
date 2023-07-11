package com.example.cpaptestapp28.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.location.LocationManagerCompat;

import com.example.cpaptestapp28.ApplicationControl;
import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.bluetooth.BleActions;
import com.example.cpaptestapp28.bluetooth.BleUUIDS;
import com.example.cpaptestapp28.bluetooth.JobStatus;
import com.example.cpaptestapp28.prefs.DevicePrefs;
import com.example.cpaptestapp28.room.model.Therapy;
import com.example.cpaptestapp28.utils.KEYS;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import timber.log.Timber;

public class CPAPService extends Service {
    private static final String TAG = "cpap_service";
    private final Binder binder = new CPAPService.LocalBinder();
    private Handler handler = new Handler();
    private BluetoothAdapter bluetoothAdapter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public CPAPService getService() {
            return CPAPService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag(TAG).d("onCreate: Service Started");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.tag(TAG).d("onDestroy: Service Ended");
        if (isScanning) {
            stopScan();
        }
        if (connection_state == 1) {
            disconnect();
        }
    }

    private void initNotification() {
        Notification notification = new Notification.Builder(this, ApplicationControl.CPAP_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Yantram")
                .setContentText("Temperature Monitoring Service")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);
    }

    /**
     * ********************* CPAP FUNCTIONS ****************************
     */
    public void sendInputSettings(Therapy therapy) {

    }

    public void resendInputs(Therapy therapy) {
        sendInputSettings(therapy);
    }

    public void readStage() {
        if (connection_state == 1) {

        } else {
            Timber.tag(TAG).d("readStage: DEVICE NOT CONNECTED : %s", connection_state);
        }
    }

    public void readAcknowledgement() {

    }


    /**
     * ********************* SCAN FOR DEVICE *****************************
     */
    private boolean isScanTimeOut = false;
    private boolean deviceFound = false;
    private String deviceAddress;
    private final int SCAN_TIMEOUT = 10000;
    private boolean isScanning;
    private BluetoothLeScannerCompat bluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner();
    private List<ScanFilter> scanFilterList = new ArrayList<>();
    private ScanSettings scanSettings;

    private Runnable runnableScanTimeOut = new Runnable() {
        @Override
        public void run() {
            isScanTimeOut = false;
            stopScan();
        }
    };

    public void startScan() {
        deviceAddress = DevicePrefs.getDeviceAddress();
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
            if (!checkLocation(this)) {
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

    /**
     * ************************ CONNECT TO DEVICE **********************************
     */
    private BluetoothGatt bluetoothGatt;
    private boolean isAttemptConnectionTimeout, isConnectionTimeout;
    private final int ATTEMPT_CONNECTION_TIMEOUT = 20000;
    private int connectionAttempts = 0;
    private int connection_state = 0; // 0 -started, 1 - success, -1 failed
    private int jobStatus = 0; // 0- started ,1 -success , -1 failed
    private BluetoothGattCharacteristic char_startDevice, char_inputPressure, char_inputRampTime,
            char_inputMode, char_inputMaskType, char_inputMaxPressure, char_inputMinPressure,
            char_therapyTime, char_timeStamp, char_deviceAck, char_deviceData1, char_deviceData2,
            char_deviceData3, char_deviceOnTime, char_deviceOffTime, char_deviceBattery, char_deviceTemp,
            char_versionControl, char_eepromData, char_eepromStatus, char_shutDownReason;
    private Runnable attemptConnectionTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAttemptConnectionTimeout) {
                isAttemptConnectionTimeout = false;
                closeConnection();
            }
        }
    };

    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            connectToDevice(deviceAddress);
        }
    };

    @SuppressLint("MissingPermission")
    public void connectToDevice(String deviceAddress) {
        isAttemptConnectionTimeout = true;
        handler.postDelayed(attemptConnectionTimeoutRunnable, ATTEMPT_CONNECTION_TIMEOUT);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connection_state = 1;
                bluetoothGatt = gatt;
                Timber.tag(TAG).d("onConnectionStateChange: Connected Successfully");
                handler.removeCallbacks(attemptConnectionTimeoutRunnable); // remove attempt connection timeout
                handler.postDelayed(new Runnable() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        bluetoothGatt.discoverServices();
                    }
                }, 300);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (status == 133 && connectionAttempts < 3) {
                    handler.removeCallbacks(attemptConnectionTimeoutRunnable);
                    connectionAttempts++;
                    handler.postDelayed(reconnectRunnable, 5000);
                } else {
                    connection_state = -1;
                    jobStatus = -1;
                    sendConnBroadCast(BleActions.ACTION_CONNECTION_STATUS, -1);
                    closeConnection();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> serviceList = gatt.getServices();
                BluetoothGattService service = null;
                boolean isServiceFound = false;
                for (BluetoothGattService s : serviceList) {
                    if (s.getUuid().toString().equals(BleUUIDS.SERVICE)) {
                        service = s;
                        isServiceFound = true;
                        Timber.tag(TAG).d("onServicesDiscovered: ");
                        break;
                    }
                }
                if (isServiceFound) {
                    Timber.tag(TAG).d("onServicesDiscovered: discover services");
                    discoverChars(service);
                } else {
                    jobStatus = -1;
                    Timber.tag(TAG).d("onServicesDiscovered: FAILED TO FIND SERVICE");
                    disconnect();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status, @NonNull byte[] value) {
            super.onDescriptorRead(gatt, descriptor, status, value);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }
    };

    private void discoverChars(BluetoothGattService service) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            switch (characteristic.getUuid().toString()) {
                case BleUUIDS.START_DEVICE:
                    char_startDevice = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND START DEVICE");
                    break;
                case BleUUIDS.INPUT_PRESSURE:
                    char_inputPressure = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND INPUT PRESSURE");
                    break;
                case BleUUIDS.INPUT_RAMP_TIME:
                    char_inputRampTime = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND INPUT RAMP TIME");
                    break;
                case BleUUIDS.INPUT_MODE:
                    char_inputMode = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND INPUT MODE");
                    break;
                case BleUUIDS.INPUT_MASK_TYPE:
                    char_inputMaskType = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND INPUT MASK_TYPE");
                    break;
                case BleUUIDS.INPUT_MAX_PRESSURE:
                    char_inputMaxPressure = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND INPUT MAX PRESSURE");
                    break;
                case BleUUIDS.INPUT_MIN_PRESSURE:
                    char_inputMinPressure = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND INPUT MIN PRESSURE");
                    break;
                case BleUUIDS.THERAPY_TIME:
                    char_therapyTime = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND THERAPY TIME");
                    break;
                case BleUUIDS.TIMESTAMP:
                    char_timeStamp = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND TIMESTAMP");
                    break;
                case BleUUIDS.DEVICE_ACKNOWLEDGEMENT:
                    char_deviceAck = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND DEVICE ACKNOWLEDGEMENT");
                    break;
                case BleUUIDS.DEVICE_DATA_1:
                    char_deviceData1 = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND DEVICE DATA 1 ");
                    break;
                case BleUUIDS.DEVICE_DATA_2:
                    char_deviceData2 = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND DEVICE DATA 2");
                    break;
                case BleUUIDS.DEVICE_DATA_3:
                    char_deviceData3 = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND DEVICE DATA 3");
                    break;
                case BleUUIDS.DEVICE_ON_TIME:
                    char_deviceOnTime = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND ON TIME");
                    break;
                case BleUUIDS.DEVICE_OFF_TIME:
                    char_deviceOffTime = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND OFF TIME");
                    break;
                case BleUUIDS.DEVICE_BATTERY:
                    char_deviceBattery = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND DEVICE BATTERY");
                    break;
                case BleUUIDS.DEVICE_TEMPERATURE:
                    char_deviceTemp = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND DEVICE TEMPERATURE");
                    break;
                case BleUUIDS.DEVICE_VERSION_CONTROL:
                    char_versionControl = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND DEVICE VERSION CONTROL");
                    break;
                case BleUUIDS.DEVICE_EEPROM_DATA:
                    char_eepromData = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND EEPROM DATA");
                    break;
                case BleUUIDS.DEVICE_EEPROM_STATUS:
                    char_eepromStatus = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND CUSTOM EEPROM STATUS");
                    break;
                case BleUUIDS.DEVICE_SHUTDOWN_REASON:
                    char_shutDownReason = characteristic;
                    Timber.tag(TAG).d("discoverChars: FOUND SHUTDOWN REASON");
                    break;
                default:
                    Timber.tag(TAG).d("discoverChars: UNKNOWN BLUETOOTH CHAR FOUND");
                    break;
            }
        }
        sendConnBroadCast(BleActions.ACTION_CONNECTION_STATUS, 1);
    }

    /**
     * ************** READ CHARACTERISTIC *****************
     */
    @SuppressLint("MissingPermission")
    private void readChar(BluetoothGattCharacteristic characteristic, String message) {
        if (bluetoothAdapter == null) {
            Timber.tag(TAG).d("readChar: bluetooth adapter is null");
            jobStatus = JobStatus.JOB_FAILED;
            disconnect();
            return;
        }
        if (bluetoothGatt == null) {
            Timber.tag(TAG).d("readChar: bluetooth gatt is null");
            jobStatus = JobStatus.JOB_FAILED;
            disconnect();
            return;
        }
        if (characteristic == null) {
            Timber.tag(TAG).d("readChar: bluetooth characteristic is null");
            jobStatus = JobStatus.JOB_FAILED;
            disconnect();
            return;
        }
        Timber.tag(TAG).d("readChar: " + message + " START READING");
        bluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * ************** WRITE CHARACTERISTIC *****************
     */
    @SuppressLint("MissingPermission")
    private void writeChar(BluetoothGattCharacteristic characteristic, String message) {
        if (bluetoothAdapter == null) {
            Timber.tag(TAG).d("writeChar: bluetooth adapter is null");
            jobStatus = JobStatus.JOB_FAILED;
            return;
        }
        if (bluetoothGatt == null) {
            Timber.tag(TAG).d("writeChar: bluetooth gatt is null");
            jobStatus = JobStatus.JOB_FAILED;
            return;
        }
        if (characteristic == null) {
            Timber.tag(TAG).d("writeChar: bluetooth characteristic is null");
            jobStatus = JobStatus.JOB_FAILED;
            return;
        }
        Timber.tag(TAG).d("writeChar: " + message + " START WRITING");
        bluetoothGatt.writeCharacteristic(characteristic);
    }
    @SuppressLint("MissingPermission")
    private void disconnect() {
        Timber.tag(TAG).d("disconnect: ");
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
    }

    @SuppressLint("MissingPermission")
    private void closeConnection() {
        Timber.tag(TAG).d("closeConnection: ");
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
    }

    private void sendConnBroadCast(String action, int status) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(KEYS.JOB_STATUS, status);
        sendBroadcast(intent);
    }

}
