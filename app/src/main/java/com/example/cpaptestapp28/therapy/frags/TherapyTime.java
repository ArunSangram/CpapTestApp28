package com.example.cpaptestapp28.therapy.frags;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.databinding.FragmentTherapyTimeBinding;
import com.google.android.material.appbar.MaterialToolbar;

public class TherapyTime extends Fragment {
    private FragmentTherapyTimeBinding binding;

    private TherapyTimeListener listener;

    public interface TherapyTimeListener {
        void onTherapyTimeSelected(int time);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (TherapyTimeListener) context;
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

    public TherapyTime() {
        // Required empty public constructor
    }

    public static TherapyTime newInstance(String param1, String param2) {
        TherapyTime fragment = new TherapyTime();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTherapyTimeBinding.inflate(inflater);
        initToolBar();
        binding.numberPicker.setMaxValue(10);
        binding.numberPicker.setMinValue(1);
        onClick();
        return binding.getRoot();
    }

    private void initToolBar() {
        MaterialToolbar materialToolbar = binding.toolbar.toolBarAppBar;
        materialToolbar.setTitle("Select Therapy Time");
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
        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTherapyTimeSelected(binding.numberPicker.getValue());
            }
        });
    }
}