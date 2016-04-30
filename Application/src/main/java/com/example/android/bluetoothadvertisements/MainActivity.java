/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothadvertisements;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Setup display fragments and ensure the device supports Bluetooth.
 */
public class MainActivity extends FragmentActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.activity_main_title);

        if (savedInstanceState == null) {

            mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                    .getAdapter();

            // Is Bluetooth supported on this device?
            if (mBluetoothAdapter != null) {

                // Is Bluetooth turned on?
                if (mBluetoothAdapter.isEnabled()) {

                    // Are Bluetooth Advertisements supported on this device?
                    if (mBluetoothAdapter.isMultipleAdvertisementSupported() ) {
                        final String permissionCoarce = Manifest.permission.ACCESS_COARSE_LOCATION;
                        final String permissionFine = Manifest.permission.ACCESS_COARSE_LOCATION;
                        if (checkSelfPermission(permissionCoarce) == PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(permissionFine) == PackageManager.PERMISSION_GRANTED) {
                            setupFragments();
                        } else {
                            requestLocationPermission();
                        }
                       /* if (checkSelfPermission(
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

                        } else {
                            Log.d("Test", "mBluetoothAdapter.isOffloadedFilteringSupported()" + mBluetoothAdapter.isOffloadedFilteringSupported());
                            Log.d("Test", "mBluetoothAdapter.isOffloadedScanBatchingSupported()" + mBluetoothAdapter.isOffloadedScanBatchingSupported());
                            // Everything is supported and enabled, load the fragments.

                        }*/

                    } else {

                        // Bluetooth Advertisements are not supported.
                        showErrorText(R.string.bt_ads_not_supported);
                    }
                } else {

                    // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
                }
            } else {

                // Bluetooth is not supported.
                showErrorText(R.string.bt_not_supported);
            }
        }
    }

    private void requestLocationPermission() {
        //String permission = Manifest.permission.ACCESS_COARSE_LOCATION;
        /*Intent locationPermission = new Intent(permission);
        startActivityForResult(locationPermission, Constants.REQUEST_ENABLE_Location);*/
       /* if (shouldShowRequestPermissionRationale(permission))
        {
            //Explain to the user why we need to read the contacts
            Snackbar.Make(layout, "Location access is required to show coffee shops nearby.", Snackbar.LengthIndefinite)
                    .SetAction("OK", v => RequestPermissions(PermissionsLocation, RequestLocationId))
            .Show();
            return;
        }*/
        requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },Constants.REQUEST_ENABLE_Location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String permission = Manifest.permission_group.LOCATION;
                    if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                        setupFragments();
                    } else {
                        requestLocationPermission();
                    }
                    /*if (checkSelfPermission(
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

                    } else {
                        Log.d("Test", "mBluetoothAdapter.isOffloadedFilteringSupported()" + mBluetoothAdapter.isOffloadedFilteringSupported());
                        Log.d("Test", "mBluetoothAdapter.isOffloadedScanBatchingSupported()" + mBluetoothAdapter.isOffloadedScanBatchingSupported());
                        // Everything is supported and enabled, load the fragments.
                        setupFragments();
                    }*/

                    // permission was granted, yay!
                    setupFragments();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // Bluetooth Advertisements are not supported.
                    showErrorText(R.string.bt_ads_not_supported);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {

                    // Bluetooth is now Enabled, are Bluetooth Advertisements supported on
                    // this device?
                    if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

                        // Everything is supported and enabled, load the fragments.
                        //setupFragments();
                        requestLocationPermission();;

                    } else {

                        // Bluetooth Advertisements are not supported.
                        showErrorText(R.string.bt_ads_not_supported);
                    }
                } else {

                    // User declined to enable Bluetooth, exit the app.
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            case Constants.REQUEST_ENABLE_Location:
                    if (resultCode == RESULT_OK) {
                        setupFragments();
                    }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        ScannerFragment scannerFragment = new ScannerFragment();
        // Fragments can't access system services directly, so pass it the BluetoothAdapter
        scannerFragment.setBluetoothAdapter(mBluetoothAdapter);
        transaction.replace(R.id.scanner_fragment_container, scannerFragment);

        AdvertiserFragment advertiserFragment = new AdvertiserFragment();
        transaction.replace(R.id.advertiser_fragment_container, advertiserFragment);

        transaction.commit();

        Intent intent = new Intent(this, ScannerService.class);
        PendingIntent recurringLl24 = PendingIntent.getService(this, 1, intent,0);
        //PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5 * 1000, recurringLl24); // Log repetition
    }

    public class StartServiceReceiver extends BroadcastReceiver {

        private static final String TAG = "LL24";
        Context context;

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(TAG, "Alarm for LifeLog...");

            Intent ll24Service = new Intent(context,ScannerService.class);
            context.getApplicationContext().startService(ll24Service);
        }
    }

    private void showErrorText(int messageId) {

        TextView view = (TextView) findViewById(R.id.error_textview);
        view.setText(getString(messageId));
    }
}