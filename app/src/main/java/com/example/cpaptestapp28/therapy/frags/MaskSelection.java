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
import com.example.cpaptestapp28.databinding.FragmentMaskSelectionBinding;
import com.google.android.material.appbar.MaterialToolbar;

public class MaskSelection extends Fragment {
    private FragmentMaskSelectionBinding binding;
    private MaskSelectionListener listener;

    public interface MaskSelectionListener {
        void onMaskSelected(int maskType);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (MaskSelectionListener) context;
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

    public MaskSelection() {
        // Required empty public constructor
    }

    public static MaskSelection newInstance(String param1, String param2) {
        MaskSelection fragment = new MaskSelection();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMaskSelectionBinding.inflate(inflater);
        initToolBar();
        onClick();
        return binding.getRoot();
    }

    private void initToolBar() {
        MaterialToolbar materialToolbar = binding.toolBar.toolBarAppBar;
        materialToolbar.setTitle("Select Mask");
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
        binding.cardMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMaskSelected(1);
            }
        });
    }
}