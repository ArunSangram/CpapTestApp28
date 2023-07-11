package com.example.cpaptestapp28.device.fragAddDevice;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cpaptestapp28.R;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class AdapterScanDevice extends RecyclerView.Adapter<AdapterScanDevice.ScanViewHolder> {
    private Context context;
    private List<ScanResult> deviceList;
    private BluetoothListener listener;

    public interface BluetoothListener {
        void onDeviceSelected(ScanResult scanResult);
    }

    public AdapterScanDevice(Context context, List<ScanResult> deviceList, BluetoothListener listener) {
        this.context = context;
        this.deviceList = deviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_d_list, parent, false);
        return new ScanViewHolder(view);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull ScanViewHolder holder, int position) {
        ScanResult scanResult = deviceList.get(position);
        BluetoothDevice bluetoothDevice = scanResult.getDevice();
        holder.deviceName.setText(bluetoothDevice.getName());
        holder.deviceAddress.setText(bluetoothDevice.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeviceSelected(scanResult);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class ScanViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceName, deviceAddress;

        public ScanViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.red_dList_name);
            deviceAddress = itemView.findViewById(R.id.red_dList_address);
        }
    }
}
