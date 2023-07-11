package com.example.cpaptestapp28.device.fragAddDevice;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.databinding.FragmentConfigureDeviceBinding;

public class ConfigureDevice extends Fragment {
    private FragmentConfigureDeviceBinding binding;
    private ConfigListener listener;
    private boolean isSuccess = false;

    public interface ConfigListener{
        void onRetry();

        void onClose();
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ConfigureDevice() {
        // Required empty public constructor
    }

    public static ConfigureDevice newInstance(String param1, String param2) {
        ConfigureDevice fragment = new ConfigureDevice();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ConfigListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + "must implement fragment to activity"
            );
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConfigureDeviceBinding.inflate(inflater);
        showProcessing();
        onClickListeners();
        return binding.getRoot();
    }

    public void showProcessing(){
        binding.processingLayout.setVisibility(View.VISIBLE);
        binding.resultLayout.setVisibility(View.GONE);
    }

    public void showResult(boolean result,String text){
        binding.processingLayout.setVisibility(View.GONE);
        binding.resultLayout.setVisibility(View.VISIBLE);
        binding.resultText.setText(text);
        isSuccess = result;
        if(result){
            binding.resultButton.setText("OK");
            binding.resultImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.baseline_check_circle_64));
        }else{
            binding.resultButton.setText("Retry");
            binding.resultImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.baseline_warning_64));
        }
    }

    private void onClickListeners(){
        binding.resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSuccess){
                    listener.onClose();
                }else{
                    listener.onRetry();
                }
            }
        });
    }
}