package com.example.cpaptestapp28.therapy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.room.model.Therapy;
import com.example.cpaptestapp28.service.CPAPService;
import com.example.cpaptestapp28.therapy.frags.InputSettings;
import com.example.cpaptestapp28.therapy.frags.MaskSelection;
import com.example.cpaptestapp28.therapy.frags.SelectMode;
import com.example.cpaptestapp28.therapy.frags.TherapyConfigure;
import com.example.cpaptestapp28.therapy.frags.TherapyTime;
import com.example.cpaptestapp28.utils.TherapyUtils;

import timber.log.Timber;

public class StartTherapy extends AppCompatActivity
        implements SelectMode.ModeSelected,
        InputSettings.InputSettingsSelected,
        MaskSelection.MaskSelectionListener,
        TherapyTime.TherapyTimeListener,
        TherapyConfigure.TherapyConfigListener,
        ServiceConnection {
    private static final String TAG = "cpap_StartTherapy";
    private SelectMode selectMode = new SelectMode();
    private InputSettings inputSettings = new InputSettings();
    private MaskSelection maskSelection = new MaskSelection();
    private TherapyTime therapyTime = new TherapyTime();
    private TherapyConfigure therapyConfigure = new TherapyConfigure();

    Therapy therapy = new Therapy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_therapy);
        //
        startService();
        showModeSelection();
    }

    private void showModeSelection() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.startTherapy_frameLayout, selectMode, "mode");
        fragmentTransaction.commit();
    }

    @Override
    public void onModeSelected(int mode) {
        therapy.setMode(mode);
        if (mode == TherapyUtils.MODE_MANUAL) {
            showInputSettings();
        } else if (mode == TherapyUtils.MODE_AUTO) {
            showMaskSelection();
        }
    }

    //
    private void showInputSettings() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.startTherapy_frameLayout, inputSettings, "inp");
        fragmentTransaction.commit();
    }

    @Override
    public void onInputSettingsSubmit(float maxP, float minP, float startP, int rampTime) {
        therapy.setMaxPressure(maxP);
        therapy.setMinPressure(minP);
        therapy.setStartPressure(startP);
        therapy.setRampTime(rampTime);
        //
        showMaskSelection();
    }

    private void showMaskSelection() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.startTherapy_frameLayout, maskSelection, "mask");
        fragmentTransaction.commit();
    }

    @Override
    public void onMaskSelected(int maskType) {
        therapy.setMaskType(maskType);
        showTherapyTimeSelection();
    }

    private void showTherapyTimeSelection() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.startTherapy_frameLayout, therapyTime, "mask");
        fragmentTransaction.commit();
    }

    @Override
    public void onTherapyTimeSelected(int time) {
        therapy.setTherapyTime(time);
        showTherapyConfigure();
    }

    private void showTherapyConfigure() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.startTherapy_frameLayout, therapyTime, "mask");
        fragmentTransaction.commit();
        sendInputs();
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onRetry() {

    }

    private void sendInputs() {
        Timber.tag(TAG).d("sendInputs: %s", therapy.toString());
    }

    /**
     * ********* LIFECYCLE METHODS ******
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeService();
    }

    /*
     * ****************** SERVICE ***********************
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