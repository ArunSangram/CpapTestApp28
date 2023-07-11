package com.example.cpaptestapp28.body;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;

import com.example.cpaptestapp28.ApplicationControl;
import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.bluetooth.BleActions;
import com.example.cpaptestapp28.databinding.ActivityHomeBinding;
import com.example.cpaptestapp28.device.DeviceDetails;
import com.example.cpaptestapp28.prefs.DevicePrefs;
import com.example.cpaptestapp28.reports.ReportsMain;
import com.example.cpaptestapp28.service.CPAPService;
import com.example.cpaptestapp28.therapy.StartTherapy;
import com.example.cpaptestapp28.utils.DeviceStatus;
import com.example.cpaptestapp28.utils.KEYS;
import com.example.cpaptestapp28.utils.SnackBarUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import timber.log.Timber;

public class Home extends AppCompatActivity implements ServiceConnection {
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BleActions.ACTION_CONNECTION_STATUS.equals(action)) {
                int status = intent.getIntExtra(KEYS.JOB_STATUS, 0);
                connectStatus(status);
            }
        }
    };
    private static final String TAG = "cpap_Home";
    private ActivityHomeBinding binding;
    private FragDeviceStatus fragDeviceStatus = new FragDeviceStatus();
    private CpapEvents cpapEvents = new CpapEvents();
    private CPAP_Control control = new CPAP_Control();
    private MaterialCardView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startService();
        initToolBar();
        initHeader();
        checkDeviceStatus();
    }

    private void initToolBar() {
        MaterialToolbar materialToolbar = findViewById(R.id.toolBar_appBar);
        materialToolbar.setTitle("Main");
        materialToolbar.setTitleTextColor(getResources().getColor(R.color.white, null));
        materialToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.bars));
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        materialToolbar.inflateMenu(R.menu.menu_main);
        materialToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ellipsis_vertical));
        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_device) {
                    Intent intent = new Intent(Home.this, DeviceDetails.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void initHeader() {
        View view = binding.navigationView.getHeaderView(0);
        backButton = view.findViewById(R.id.nav_header_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_startTherapy) {
                    Intent intent = new Intent(Home.this, StartTherapy.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.menu_reports) {
                    Intent intent = new Intent(Home.this, ReportsMain.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * **************** CHECK DEVICE STATUS ********************
     */
    private void checkDeviceStatus() {
        String deviceName = DevicePrefs.getDeviceName();
        if (deviceName.isEmpty()) {
            uiChange(DeviceStatus.DEVICE_STATUS_NO_DEVICE);
        } else {
            ApplicationControl.setDeviceAvailableStatus(DeviceStatus.DEVICE_STATUS_CONNECTING);
            uiChange(DeviceStatus.DEVICE_STATUS_CONNECTING);
            // start connection
        }
    }

    private void bleConnect() {
        if (cpapService == null) {
            SnackBarUtils.showBottomSnackBar(binding.getRoot(), "Service is not present");
        } else {
            cpapService.connectToDevice(DevicePrefs.getDeviceAddress());
        }
    }

    private void connectStatus(int status) {
        if (status == 1) {

        } else {
            ApplicationControl.setDeviceAvailableStatus(DeviceStatus.DEVICE_STATUS_SEARCH_FAILED);
            uiChange(DeviceStatus.DEVICE_STATUS_SEARCH_FAILED);
        }
    }

    // check if therapy present or not
    private void checkTherapyPresent() {
        if (cpapService != null) {
            cpapService.readStage();
        } else {
            SnackBarUtils.showBottomSnackBar(binding.getRoot(), "Service is not present");
        }
    }

    private void checkConnectedStatus() {
        int deviceStatus = ApplicationControl.getDeviceAvailableStatus();
        if (deviceStatus == DeviceStatus.DEVICE_STATUS_CONNECTING) {
            uiChange(DeviceStatus.DEVICE_STATUS_CONNECTING);
        } else if (deviceStatus == DeviceStatus.DEVICE_STATUS_SEARCH_FAILED) {
            uiChange(DeviceStatus.DEVICE_STATUS_SEARCH_FAILED);
        } else if (deviceStatus == DeviceStatus.DEVICE_STATUS_THERAPY_ABSENT) {
            uiChange(DeviceStatus.DEVICE_STATUS_THERAPY_ABSENT);
        } else if (deviceStatus == DeviceStatus.DEVICE_STATUS_THERAPY_PRESENT) {
            uiChange(DeviceStatus.DEVICE_STATUS_THERAPY_PRESENT);
        }
    }

    private void connectToDevice() {

    }

    /**
     * ******* LIFECYCLE METHODS ******
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkDeviceStatus();
        Timber.tag(TAG).d("onResume: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.tag(TAG).d("onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.tag(TAG).d("onStop: ");
        closeDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeService();
        Timber.tag(TAG).d("onDestroy: ");
    }

    /**
     * ************** UI CHANGE ON DEVICE STATUS CHANGE ****************
     */
    private void uiChange(int value) {
        switch (value) {
            case DeviceStatus.DEVICE_STATUS_NO_DEVICE:
                binding.layoutDeviceAbsent.setVisibility(View.VISIBLE);
                binding.layoutDevicePresent.setVisibility(View.GONE);
                binding.layoutDeviceCheck.setVisibility(View.GONE);
                binding.layoutTherapyActive.setVisibility(View.GONE);
                binding.layoutTherapyInactive.setVisibility(View.GONE);
                binding.layoutLookingForDevice.setVisibility(View.GONE);
                binding.layoutDeviceConnectionFailed.setVisibility(View.GONE);
                binding.layoutAddDevice.setVisibility(View.VISIBLE);
                break;
            case DeviceStatus.DEVICE_STATUS_THERAPY_PRESENT:
                binding.layoutDeviceAbsent.setVisibility(View.GONE);
                binding.layoutDeviceCheck.setVisibility(View.GONE);
                binding.layoutDevicePresent.setVisibility(View.VISIBLE);
                binding.layoutTherapyActive.setVisibility(View.VISIBLE);
                binding.layoutTherapyInactive.setVisibility(View.GONE);
                binding.layoutLookingForDevice.setVisibility(View.GONE);
                binding.layoutDeviceConnectionFailed.setVisibility(View.GONE);
                binding.layoutAddDevice.setVisibility(View.GONE);
                break;
            case DeviceStatus.DEVICE_STATUS_THERAPY_ABSENT:
                binding.layoutDeviceAbsent.setVisibility(View.GONE);
                binding.layoutDevicePresent.setVisibility(View.VISIBLE);
                binding.layoutDeviceCheck.setVisibility(View.GONE);
                binding.layoutTherapyActive.setVisibility(View.GONE);
                binding.layoutTherapyInactive.setVisibility(View.VISIBLE);
                binding.layoutLookingForDevice.setVisibility(View.GONE);
                binding.layoutDeviceConnectionFailed.setVisibility(View.GONE);
                binding.layoutAddDevice.setVisibility(View.GONE);
                break;
            case DeviceStatus.DEVICE_STATUS_CONNECTING:
                binding.layoutDeviceAbsent.setVisibility(View.GONE);
                binding.layoutDevicePresent.setVisibility(View.GONE);
                binding.layoutDeviceCheck.setVisibility(View.VISIBLE);
                binding.layoutTherapyActive.setVisibility(View.GONE);
                binding.layoutTherapyInactive.setVisibility(View.GONE);
                binding.layoutLookingForDevice.setVisibility(View.VISIBLE);
                binding.layoutDeviceConnectionFailed.setVisibility(View.GONE);
                binding.layoutAddDevice.setVisibility(View.GONE);
                break;
            case DeviceStatus.DEVICE_STATUS_SEARCH_FAILED:
                binding.layoutDeviceAbsent.setVisibility(View.GONE);
                binding.layoutDevicePresent.setVisibility(View.GONE);
                binding.layoutDeviceCheck.setVisibility(View.VISIBLE);
                binding.layoutTherapyActive.setVisibility(View.GONE);
                binding.layoutTherapyInactive.setVisibility(View.GONE);
                binding.layoutLookingForDevice.setVisibility(View.GONE);
                binding.layoutDeviceConnectionFailed.setVisibility(View.VISIBLE);
                binding.layoutAddDevice.setVisibility(View.GONE);
                break;
            default:
                break;
        }
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
        // start connection
        if (ApplicationControl.getDeviceAvailableStatus() == DeviceStatus.DEVICE_STATUS_CONNECTING) {
            bleConnect();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Timber.tag(TAG).d("onServiceDisconnected: ");
    }

    /**
     * ************ DRAWER OPEN *************
     */
    private void openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);

    }

    /**
     * ******************** NAVIGATION VIEW ************************************
     */
    private void drawerNavClicks() {
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_startTherapy) {
                    Intent intent = new Intent(Home.this, StartTherapy.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.menu_reports) {
                    Intent intent = new Intent(Home.this, ReportsMain.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}