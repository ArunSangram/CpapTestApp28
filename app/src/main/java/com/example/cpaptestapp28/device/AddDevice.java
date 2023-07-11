package com.example.cpaptestapp28.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.bluetooth.BleActions;
import com.example.cpaptestapp28.device.fragAddDevice.ConfigureDevice;
import com.example.cpaptestapp28.device.fragAddDevice.ScanDevice;
import com.example.cpaptestapp28.prefs.DevicePrefs;
import com.example.cpaptestapp28.service.CPAPService;
import com.example.cpaptestapp28.utils.KEYS;
import com.example.cpaptestapp28.utils.SnackBarUtils;

import java.util.Objects;

import timber.log.Timber;

public class AddDevice extends AppCompatActivity
        implements ScanDevice.DeviceListener,
        ConfigureDevice.ConfigListener,
        ServiceConnection {
    private ScanDevice scanDevice = new ScanDevice();
    private ConfigureDevice configureDevice = new ConfigureDevice();
    private static final String TAG = "vTrack_AddDevice";
    //vm
//    private AddDeviceVM addDeviceVM;
    //
    private Handler handler = new Handler();
    private BluetoothDevice device;
    //
    private boolean hasCheckPassed = false;
    private boolean hasConfiguredDevice = false;
    //
    private IntentFilter intentFilter;
    private ConstraintLayout constraintLayout;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Objects.equals(action, BleActions.ACTION_CONNECTION_STATUS)) ;
            {
                int jobStatus = intent.getIntExtra(KEYS.JOB_STATUS, 0);
                connectionResult(jobStatus);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        constraintLayout = findViewById(R.id.addDeviceLayout);
        //
        intentFilter = new IntentFilter();
        intentFilter.addAction(BleActions.ACTION_CONNECTION_STATUS);
        startService();
        showScanFragment();
    }

    /**
     * ******************* SHOW SCAN DEVICE FRAGMENT *****************************
     */
    private void showScanFragment() {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.addDevice_frameLayout, scanDevice, "scan");
        transaction.commit();
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) {
        Timber.tag(TAG).d("onDeviceSelected: %s", device.toString());
        this.device = device;
        showConfigureFragment();
    }

    /**
     * ***************** SHOW CONFIGURE DEVICE FRAGMENT *****************************
     */
    private void showConfigureFragment() {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.addDevice_frameLayout, configureDevice, "config");
        transaction.commit();
        if (cpapService != null) {
            connectToDevice();
        } else {
            SnackBarUtils.showBottomSnackBar(constraintLayout, "Service Not Connected");
        }
    }

    @Override
    public void onRetry() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectToDevice();
                configureDevice.showProcessing();
            }
        }, 1000);
    }

    @Override
    public void onClose() {
        finish();
    }

    /**
     * **************** CONNECT TO DEVICE ******************
     */
    private void connectToDevice() {
        cpapService.connectToDevice(device.getAddress());
    }


    private void connectionResult(int status) {
        if (status != 1) {
            configureDevice.showResult(false, "Failed");
        } else {
            storeInPrefs();
            configureDevice.showResult(true, "success adding device");
        }
    }

    @SuppressLint("MissingPermission")
    private void storeInPrefs() {
        DevicePrefs.storeDeviceInfo(device.getName(), device.getAddress());
    }

    /**
     * *************************** LIFECYCLE ****************************
     */
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeService();
    }


    /*
     * ******************************** SERVICE *********************************************
     */
    private CPAPService cpapService;
    private boolean isService = false;

    private void startService() {
        if (!isService) {
            bindService(new Intent(this, CPAPService.class), this, BIND_AUTO_CREATE);
            isService = true;
        } else {
            Timber.tag(TAG).d("startService: Service Already Running");
        }
    }

    private void closeService() {
        if (isService) {
            unbindService(this);
            isService = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Timber.tag(TAG).d("onServiceConnected: ");
        cpapService = ((CPAPService.LocalBinder) iBinder).getService();

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Timber.tag(TAG).d("onServiceDisconnected: ");
    }

}