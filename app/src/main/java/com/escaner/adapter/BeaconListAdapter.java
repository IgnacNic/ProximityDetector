package com.escaner.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.escaner.services.ScannerService;
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
    private OnAcceptListener oAListener;
    private OnDismissListener oDListener;

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
        int pulsera;
        try {
            holder.tvMac.setText(device.getMac());
        } catch (NullPointerException npe) {
            holder.tvMac.setText("NÚMERO:");
        }
        int battery = device.getBattery();
        holder.tvBattery.setText("" + (battery > 0 ? battery + "%":""));
        holder.llData.removeAllViews();
        if (battery >= 0 && battery <= 20) {
            holder.ivBattery.setImageResource(R.drawable.battery_5);
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

    public void setListener(OnAcceptListener oal, OnDismissListener odl) {
        oAListener = oal;
        oDListener = odl;
    }

    public interface OnAcceptListener {
        /**
         * Metodo invocado al seleccionar el boton "SI" en la secion de informacion de la pulsera. Ver
         * la implementacion para mas informacion sobre el procedimiento.
         */
        void onAcceptClick(ScannerService beaconXInfo);
    }

    public interface OnDismissListener {
        /**
         * Metodo invocado al seleccionar el botón "NO" en la seccion de informacion de la pulsera. Ver
         * la implementacion para mas información sobre el procedimiento.
         */
        void onDismissClick(ScannerService beaconXInfo);
    }

    class DeviceViewHolder extends ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.iv_battery)
        ImageView ivBattery;
        @Bind(R.id.battery_percent)
        TextView tvBattery;
        @Bind(R.id.ll_data)
        LinearLayout llData;
        @Bind(R.id.accept_button)
        Button accept_button;
        @Bind(R.id.dismiss_button)
        Button dismiss_button;
        @Bind(R.id.tv_mac)
        TextView tvMac;


        public DeviceViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);
        }

    }
}