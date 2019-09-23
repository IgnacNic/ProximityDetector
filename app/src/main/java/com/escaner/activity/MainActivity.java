package com.escaner.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.escaner.adapter.BeaconListAdapter;
import com.escaner.entity.ScannerDevice;
import com.escaner.utils.FileUtils;
import com.example.escaner.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements BeaconListAdapter.OnPlayListener {
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Bind(R.id.iv_back)
    LinearLayout ivBack;


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
        ivBack.setVisibility(View.INVISIBLE);
        ivBack.setClickable(false);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , PERMISSION_REQUEST_CODE);
            }
        }
        showSearchFragment();
        showListFragment();
    }

    private void showSearchFragment() {
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setClickable(true);
        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.frame_container, searchFragment).hide(searchFragment).commit();
        } else {
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_left_exit).show(searchFragment).hide(deviceFragment).commit();
        }
    }

    private void showListFragment() {
        ivBack.setVisibility(View.INVISIBLE);
        ivBack.setClickable(false);
        if (deviceFragment == null) {
            deviceFragment = DeviceListFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.frame_container, deviceFragment).commit();
        } else {
            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {

                }
            });
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_right_enter,R.anim.fragment_right_exit).show(deviceFragment).hide(searchFragment).commit();
        }
    }


    @Override
    public void onPlayClicked(ScannerDevice device) {
        searchFragment.setDevice(device);
        showSearchFragment();
    }

    @Override
    public void onBackPressed(){
        showListFragment();
    }
}
