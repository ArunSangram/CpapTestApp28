package com.example.cpaptestapp28.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.databinding.ActivityDeviceDetailsBinding;
import com.example.cpaptestapp28.prefs.DevicePrefs;
import com.google.android.material.appbar.MaterialToolbar;

public class DeviceDetails extends AppCompatActivity {
    private ActivityDeviceDetailsBinding binding;
    //    private DeviceDetailsVM deviceDetailsVM;

    private static final String TAG = "vTrack_DevDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        initToolBar();
//        initVM();
        onClickListeners();
    }

//    private void initVM() {
//        deviceDetailsVM = new ViewModelProvider(this).get(DeviceDetailsVM.class);
//    }

    private void initToolBar() {
        MaterialToolbar materialToolbar = findViewById(R.id.toolBar_appBar);
        materialToolbar.setTitle("Device");
        materialToolbar.setTitleTextColor(getResources().getColor(R.color.white, null));
        materialToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24_white));
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void defaults() {
        String deviceName = DevicePrefs.getDeviceName();
        if (deviceName.isEmpty()) {
            binding.dDetailsNoDeviceLayout.setVisibility(View.VISIBLE);
            binding.dDetailsDevicePresentLayout.setVisibility(View.GONE);
        } else {
            binding.dDetailsNoDeviceLayout.setVisibility(View.GONE);
            binding.dDetailsDevicePresentLayout.setVisibility(View.VISIBLE);
            //
            binding.dDetailsNameText.setText(DevicePrefs.getDeviceName());
            binding.dDetailsAddressText.setText(DevicePrefs.getDeviceAddress());
        }
    }

    private void onClickListeners() {
        binding.dDetailsNoDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceDetails.this, AddDevice.class);
                startActivity(intent);
            }
        });

        binding.dDetailsUnPairDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDevice();
            }
        });
    }

    /**
     * ************************* LIFECYCLE METHODS ***********************
     */
    @Override
    protected void onResume() {
        super.onResume();
        defaults();
    }

    /**
     * ************************ REMOVE DEVICE ****************************
     */
    private void removeDevice() {
        DevicePrefs.removeDeviceInfo();
    }

}