package com.example.cpaptestapp28.therapy.frags;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cpaptestapp28.R;
import com.example.cpaptestapp28.databinding.FragmentTherapyConfigureBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class TherapyConfigure extends Fragment {
    private FragmentTherapyConfigureBinding binding;

    private TherapyConfigListener listener;

    public interface TherapyConfigListener {
        void onFinish();

        void onRetry();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (TherapyConfigListener) context;
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

    public TherapyConfigure() {
        // Required empty public constructor
    }

    public static TherapyConfigure newInstance(String param1, String param2) {
        TherapyConfigure fragment = new TherapyConfigure();
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
        binding = FragmentTherapyConfigureBinding.inflate(inflater);
        initToolBar();
        return binding.getRoot();
    }

    private void initToolBar() {
        MaterialToolbar materialToolbar = binding.toolbar.toolBarAppBar;
        materialToolbar.setTitle("Configuring Device");
        materialToolbar.setTitleTextColor(getResources().getColor(R.color.white, null));
//        materialToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.baseline_arrow_back_ios_24_white));
//        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().onBackPressed();
//            }
//        });
    }

    public void addTask(String taskName, boolean status) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.configure_element, null, false);
        TextView taskText = view.findViewById(R.id.taskName);
        ImageView imageView = view.findViewById(R.id.taskResultImage);
        taskText.setText(taskName);
        if (status) {
            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.baseline_check_circle_24));
        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.baseline_check_circle_24));
        }
    }

    public void showConfigureResult(boolean result) {
        MaterialButton materialButton = new MaterialButton(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        materialButton.setLayoutParams(layoutParams);
        if (result) {
            materialButton.setText("Ok");
            binding.progressBar.setVisibility(View.GONE);
            binding.finalStatusImage.setVisibility(View.VISIBLE);
            binding.finalStatusImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.baseline_check_circle_64));
            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onFinish();
                }
            });
        } else {
            materialButton.setText("Retry");
            binding.progressBar.setVisibility(View.GONE);
            binding.finalStatusImage.setVisibility(View.VISIBLE);
            binding.finalStatusImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.baseline_warning_64));
            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retry();
                }
            });
        }
    }

    private void retry() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.parentLayout.removeAllViews();
        listener.onRetry();
    }
}