package com.example.cpaptestapp28.therapy.frags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.body.Home;
import com.example.cpaptestapp28.databinding.FragmentSelecModeBinding;
import com.example.cpaptestapp28.device.DeviceDetails;
import com.example.cpaptestapp28.utils.TherapyUtils;
import com.google.android.material.appbar.MaterialToolbar;

public class SelectMode extends Fragment {
    private FragmentSelecModeBinding binding;
    private ModeSelected listener;

    public interface ModeSelected {
        void onModeSelected(int mode);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ModeSelected) context;
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

    public SelectMode() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelecMode.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectMode newInstance(String param1, String param2) {
        SelectMode fragment = new SelectMode();
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
        binding = FragmentSelecModeBinding.inflate(inflater);
        initToolBar();
        onClick();
        return binding.getRoot();
    }

    private void initToolBar() {
        MaterialToolbar materialToolbar = binding.toolBar.toolBarAppBar;
        materialToolbar.setTitle("Select Mode");
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
        binding.cardAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onModeSelected(TherapyUtils.MODE_AUTO);
            }
        });

        binding.cardManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onModeSelected(TherapyUtils.MODE_MANUAL);
            }
        });
    }
}