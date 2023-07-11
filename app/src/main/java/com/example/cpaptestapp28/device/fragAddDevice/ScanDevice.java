package com.example.cpaptestapp28.device.fragAddDevice;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cpaptestapp28.databinding.FragmentScanDeviceBinding;
import com.example.cpaptestapp28.utils.SnackBarUtils;
import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import timber.log.Timber;

public class ScanDevice extends Fragment implements
        AdapterScanDevice.BluetoothListener {
    private static final String TAG = "vTrack_ScanDevice";
    private static final int SCAN_TIMEOUT = 30000;
    private FragmentScanDeviceBinding binding;
    // bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private boolean isScanning = false;
    private List<ScanResult> bluetoothDevices = new ArrayList<>();
    // recycler
    private AdapterScanDevice adapterScanDevice;
    private Handler handler = new Handler(Looper.getMainLooper());
    //
    private final String DEVICE_NAME = "CPAP";
    private IntentFilter intentFilter;
    private DeviceListener listener;

    public interface DeviceListener {
        void onDeviceSelected(BluetoothDevice device);
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanDevice() {
        // Required empty public constructor
    }

    public static ScanDevice newInstance(String param1, String param2) {
        ScanDevice fragment = new ScanDevice();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Timber.tag(TAG).d("onReceive: %s", action);
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        defaults();
                    }
                });
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeviceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + "must implement fragment to activity"
            );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentScanDeviceBinding.inflate(inflater);
        //
        initRecycler();
        onClickListeners();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return binding.getRoot();
    }

    private void initRecycler() {
        binding.scanFragRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterScanDevice = new AdapterScanDevice(getContext(), bluetoothDevices, this);
        binding.scanFragRecyclerView.setAdapter(adapterScanDevice);
    }

    @Override
    public void onDeviceSelected(ScanResult scanResult) {
        stopScan();
        checkBeaconOrConnection(scanResult);
    }


    private void onClickListeners() {
        binding.scanFragButtonEnableBluetooth.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                bluetoothAdapter.enable();
            }
        });

        binding.scanFragButtonEnableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        binding.scanFragButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaults();
            }
        });
    }

    private void defaults() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        binding.scanFragScanningLayout.setVisibility(View.GONE);
        binding.scanFragStartScanLayout.setVisibility(View.GONE);
        binding.scanFragBluetoothLayout.setVisibility(View.GONE);
        binding.scanFragLocationLayout.setVisibility(View.GONE);
        if (!checkBluetooth()) {
            binding.scanFragBluetoothLayout.setVisibility(View.VISIBLE);
            return;
        }

        if (!checkLocation(getContext())) {
            binding.scanFragLocationLayout.setVisibility(View.VISIBLE);
            return;
        }

        if (isScanning) {
            stopScan();
        }
        resetScan();
        startScan();
    }

    /**
     * ****************** BLUETOOTH SCANNING *****************************
     */
    private BluetoothLeScannerCompat scannerCompat = BluetoothLeScannerCompat.getScanner();
    private boolean isScanTimeout = false;
    private Runnable RunnableScanTimeout = new Runnable() {
        @Override
        public void run() {
            isScanTimeout = false;
            stopScan();
        }
    };

    private void resetScan() {
        Timber.tag(TAG).d("resetScan: ");
        binding.scanFragScanningLayout.setVisibility(View.VISIBLE);
        binding.scanFragStartScanLayout.setVisibility(View.GONE);
        binding.scanFragBluetoothLayout.setVisibility(View.GONE);
        binding.scanFragLocationLayout.setVisibility(View.GONE);
        // empty list
        bluetoothDevices.clear();
        adapterScanDevice.notifyDataSetChanged();
    }

    private void startScan() {
        if (!isScanning) {
            Timber.tag(TAG).d("startScan: ");
            isScanning = true;
            isScanTimeout = true;
            handler.postDelayed(RunnableScanTimeout, SCAN_TIMEOUT);
            scannerCompat.startScan(scanCallback);
        } else {
            Timber.tag(TAG).d("startScan: Scan already running");
        }
    }

    private void stopScan() {
        Timber.tag(TAG).d("stopScan: ");
        if (isScanTimeout) {
            isScanTimeout = false;
            handler.removeCallbacks(RunnableScanTimeout);
        }

        if (isScanning) {
            if (scannerCompat != null) {
                scannerCompat.stopScan(scanCallback);
                isScanning = false;
                binding.scanFragScanningLayout.setVisibility(View.GONE);
                binding.scanFragStartScanLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result.getDevice().getName() != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleScanResult(result);
                    }
                });
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            stopScan();
            Timber.tag(TAG).e("onScanFailed: %s", errorCode);
        }
    };

    @SuppressLint("MissingPermission")
    private void handleScanResult(ScanResult scanResult) {
        boolean hasDuplicate = false;
        for (ScanResult sr : bluetoothDevices) {
            if (sr.getDevice().getName().equals(scanResult.getDevice().getName())) {
                hasDuplicate = true;
                break;
            }
        }

        if (!hasDuplicate) {
            Timber.tag(TAG).d("handleScanResult: %s", scanResult.toString());
            bluetoothDevices.add(scanResult);
            adapterScanDevice.notifyItemInserted(bluetoothDevices.size() - 1);
        }
    }

    /**
     * ******************* BEACON OR CONNECTION ****************
     */
    private void checkBeaconOrConnection(ScanResult scanResult) {
        if (scanResult.getScanRecord() != null) {
            List<ParcelUuid> uuids = scanResult.getScanRecord().getServiceUuids();
            if (uuids != null && uuids.size() > 0 && scanResult.getScanRecord().getManufacturerSpecificData() == null) {
                Timber.tag(TAG).d("checkBeaconOrConnection: CONNECTION TYPE");
                listener.onDeviceSelected(scanResult.getDevice());
            } else {
                Timber.tag(TAG).d("checkBeaconOrConnection: BEACON MODE");
                SnackBarUtils.showBottomSnackBar(binding.getRoot(), "Device in Beacon Mode");
            }
        }
    }


    /**
     * ******************* LIFECYCLE METHODS *******************
     */
    @Override
    public void onStart() {
        super.onStart();
        defaults();
        requireActivity().registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onStop() {
        super.onStop();
        stopScan();
        requireActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
}