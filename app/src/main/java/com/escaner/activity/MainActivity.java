package com.escaner.activity;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.escaner.adapter.BeaconListAdapter;
import com.escaner.entity.ScannerDevice;
import com.escaner.utils.FileUtils;
import com.example.escaner.R;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements BeaconListAdapter.OnPlayListener {


    private FragmentManager fragmentManager;
    private DeviceListFragment deviceFragment;
    private SearchFragment searchFragment;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FileUtils.storage = getExternalFilesDir(null);
        fragmentManager = getSupportFragmentManager();
        showSearchFragment();
        showListFragment();
    }

    private void showSearchFragment() {
        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.frame_container, searchFragment).commit();
        } else {
            fragmentManager.beginTransaction().show(searchFragment).hide(deviceFragment).commit();
        }
    }

    private void showListFragment() {
        if (deviceFragment == null) {
            deviceFragment = DeviceListFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.frame_container, deviceFragment).commit();
        } else {
            fragmentManager.beginTransaction().show(deviceFragment).hide(searchFragment).commit();
        }
    }


    @Override
    public void onPlayClicked(ScannerDevice beaconXInfo) {

    }
}
