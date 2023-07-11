package com.example.cpaptestapp28.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtils {

    public static void showBottomSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
//        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }

    public static void showTopSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }
}
