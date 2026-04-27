java
package com.karrel.bluetoothsample.presenter;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.karrel.bluetoothsample.model.PairedItem;
import com.karrel.bluetoothsample.model.ScanedItem;
import com.karrel.mylibrary.RLog;

import java.util.ArrayList;

import rx.subjects.PublishSubject;

public class DeviceListPresenterImpl implements DeviceListPresenter {
    private Activity mActivity;
    private PublishSubject<PairedItem> mPairedDeviceSubject;
    private PublishSubject<ScanedItem> mScanedDeviceSubject;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private final int REQUEST_ACCESS_COARSE_LOCATION = 1;

    public DeviceListPresenterImpl(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void startBluetooth() {
        RLog.e();
        // create subject
        createSubject();

        createScanCallback();

        createBluetoothLeAdapter();
        // checkpermission
        checkPermission();

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
            }
        }
    }

    private void createBluetoothLeAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
    }

    private void createScanCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    if (device == null) return;

                    ScanedItem item = new ScanedItem(device.getName(), device.getAddress());
                    RLog.d("onScanResult : " + item.toString());
                    mScanedDeviceSubject.onNext(item);
                }
            };
        }
    }

    private void createSubject() {
        mPairedDeviceSubject = PublishSubject.create();
        mScanedDeviceSubject = PublishSubject.create();
    }

    @Override
    public void stopBluetooth() {
        RLog.e();
        stopScan();
    }

    private void stopScan() {
        if (mBluetoothLeScanner != null && mScanCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    @Override
    public rx.Observable<PairedItem> getPaiedDeviceObservable() {
        return mPairedDeviceSubject.asObservable();
    }

    @Override
    public rx.Observable<ScanedItem> getScanedDeviceObservable() {
        return mScanedDeviceSubject.asObservable();
    }

    @Override
    public void getPairedDevices() {
        RLog.e();
        if (mBluetoothAdapter == null) return;
        java.util.Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                PairedItem item = new PairedItem(device.getName(), device.getAddress());
                RLog.d("PairedItem : " + item.toString());
                mPairedDeviceSubject.onNext(item);
            }
        }
    }

    @Override
    public void scanDevice() {
        RLog.e();
        if (mBluetoothLeScanner != null && mScanCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner.startScan(mScanCallback);
        }
    }

    @Override
    public boolean isEnableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            return false;
        } else {
            if (bluetoothAdapter.isEnabled()) {
                return true;
            } else {
                return false;
            }
        }
    }
}