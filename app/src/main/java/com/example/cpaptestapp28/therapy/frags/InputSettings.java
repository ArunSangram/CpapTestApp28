package com.example.cpaptestapp28.therapy.frags;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.databinding.FragmentSettingsBinding;
import com.example.cpaptestapp28.utils.SnackBarUtils;
import com.google.android.material.appbar.MaterialToolbar;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InputSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputSettings extends Fragment {
    private static final String TAG = "cpap_InpSett";
    private FragmentSettingsBinding binding;
    private InputSettingsSelected listener;

    public interface InputSettingsSelected {
        void onInputSettingsSubmit(float maxP, float minP, float startP, int rampTime);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (InputSettingsSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + "must implement fragment to activity"
            );
        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static InputSettings newInstance(String param1, String param2) {
        InputSettings fragment = new InputSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InputSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater);
        initToolBar();
        onClick();
        return binding.getRoot();
    }

    private void initToolBar() {
        MaterialToolbar materialToolbar = binding.toolbar.toolBarAppBar;
        materialToolbar.setTitle("Set Input Settings");
        materialToolbar.setTitleTextColor(getResources().getColor(R.color.white, null));
        materialToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.baseline_arrow_back_ios_24_white));
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void onClick() {
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFormSubmit();
            }
        });
    }

    private void onFormSubmit() {
        String maxP = binding.maxPressure.getText().toString().trim();
        String minP = binding.minPressure.getText().toString().trim();
        String startP = binding.startPressure.getText().toString().trim();
        String rampTime = binding.rampTime.getText().toString().trim();

        if (TextUtils.isEmpty(maxP)) {
            SnackBarUtils.showTopSnackBar(binding.getRoot(), "Maximum Pressure cannot be empty");
            binding.maxPressure.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(minP)) {
            SnackBarUtils.showTopSnackBar(binding.getRoot(), "Minimum Pressure cannot be empty");
            binding.minPressure.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(startP)) {
            SnackBarUtils.showTopSnackBar(binding.getRoot(), "Start Pressure cannot be empty");
            binding.startPressure.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(rampTime)) {
            SnackBarUtils.showTopSnackBar(binding.getRoot(), "Ramp Time cannot be empty");
            binding.rampTime.requestFocus();
            return;
        }

        try {
            float maxPValue = Float.parseFloat(maxP);
            float minPValue = Float.parseFloat(minP);
            float startPValue = Float.parseFloat(startP);
            int rampTimeValue = Integer.parseInt(rampTime);

            if (maxPValue > 20 || maxPValue < 5) {
                SnackBarUtils.showTopSnackBar(binding.getRoot(), "Max Pressure lies between 5-20 cmH20");
                binding.maxPressure.requestFocus();
                return;
            }
            if (minPValue > 19 || minPValue < 4) {
                SnackBarUtils.showTopSnackBar(binding.getRoot(), "Min Pressure lies between 4-19 cmH20");
                binding.minPressure.requestFocus();
                return;
            }
            if (maxPValue <= minPValue) {
                SnackBarUtils.showTopSnackBar(binding.getRoot(), "Max Pressure must be greater than min Pressure");
                binding.maxPressure.requestFocus();
                return;
            }
            if (startPValue > maxPValue || startPValue < minPValue) {
                SnackBarUtils.showTopSnackBar(binding.getRoot(), "Start Pressure must be between max and min Pressure");
                binding.startPressure.requestFocus();
                return;
            }
            if (rampTimeValue > 60 || rampTimeValue < 0) {
                SnackBarUtils.showTopSnackBar(binding.getRoot(), "Ramp Time must be between 0-60");
                binding.rampTime.requestFocus();
                return;
            }

            listener.onInputSettingsSubmit(maxPValue, minPValue, startPValue, rampTimeValue);
        } catch (Exception e) {
            Timber.tag(TAG).d("onFormSubmit: %s", e.getMessage());
            SnackBarUtils.showTopSnackBar(binding.getRoot(), "Failed to process input settings");
        }
    }
}