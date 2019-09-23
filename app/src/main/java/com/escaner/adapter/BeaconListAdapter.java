package com.escaner.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.escaner.entity.ScannerDevice;
import com.example.escaner.R;
import com.moko.support.utils.MokoUtils;

import butterknife.Bind;
import butterknife.ButterKnife;


@SuppressWarnings("ALL")
public class BeaconListAdapter extends BaseAdapter<ScannerDevice> {

    private static String selected;
    DeviceViewHolder hold;
    private String[] selAux;
    private View view;
    private boolean wasInvisible = true;
    private OnPlayListener oPListener;
    private OnOptionsListener onOptionsListener;

    public BeaconListAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindViewHolder(int position, BaseAdapter.ViewHolder viewHolder, View convertView, ViewGroup parent) {
        final DeviceViewHolder holder = (DeviceViewHolder) viewHolder;
        final ScannerDevice device = getItem(position);
        setView(holder, device);
    }

    private void setView(DeviceViewHolder holder, final ScannerDevice device) {
        hold = holder;
        double distance = MokoUtils.getDistance(-75, device.getRssi());
        holder.tvName.setText(TextUtils.isEmpty(device.getName()) ? "N/A" : device.getName());
        holder.tvRSSI.setText("" + device.getRssi());
        if (device.isSaved()) holder.ivOptions.setBackgroundResource(R.drawable.delete_icon);
        else holder.ivOptions.setBackgroundResource(R.drawable.save_icon);
        holder.rlStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oPListener.onPlayClicked(device);
            }
        });
        holder.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsListener.onOptionsClicked(device);
            }
        });
        int pulsera;
        try {
            holder.tvMac.setText(device.getMac());
        } catch (NullPointerException npe) {
            holder.tvMac.setText("NÚMERO:");
        }
        int battery = device.getBattery();
        holder.tvBattery.setText("" + (battery > 0 ? battery + "%":"0%"));
        if (battery >= 0 && battery <= 20) {
            holder.ivBattery.setImageResource(R.drawable.battery_1);
        }
        if (battery > 20 && battery <= 40) {
            holder.ivBattery.setImageResource(R.drawable.battery_4);
        }
        if (battery > 40 && battery <= 60) {
            holder.ivBattery.setImageResource(R.drawable.battery_3);
        }
        if (battery > 60 && battery <= 80) {
            holder.ivBattery.setImageResource(R.drawable.battery_2);
        }
        if (battery > 80 && battery <= 100) {
            holder.ivBattery.setImageResource(R.drawable.battery_1);
        }
    }

    @Override
    protected ViewHolder createViewHolder(int position, LayoutInflater inflater, ViewGroup parent) {
        view = inflater.inflate(R.layout.list_item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    public void setListeners(OnPlayListener onPlayListener, OnOptionsListener onOptionsListener) {
        oPListener = onPlayListener;
        this.onOptionsListener = onOptionsListener;
    }

    public interface OnPlayListener {

        void onPlayClicked(ScannerDevice scannerDevice);
    }

    public interface OnOptionsListener {

        void onOptionsClicked(ScannerDevice scannerDevice);
    }

    class DeviceViewHolder extends ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.iv_battery)
        ImageView ivBattery;
        @Bind(R.id.battery_percent)
        TextView tvBattery;
        @Bind(R.id.tv_mac)
        TextView tvMac;
        @Bind(R.id.tv_rssi)
        TextView tvRSSI;
        @Bind(R.id.rl_start)
        RelativeLayout rlStart;
        @Bind(R.id.iv_options)
        ImageView ivOptions;

        public DeviceViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);
        }

    }
}