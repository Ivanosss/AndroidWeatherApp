package com.example.climateapplication.activities;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.climateapplication.R;
import com.example.climateapplication.databinding.ActivityLoadingBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LoadingActivity extends AppCompatActivity {
    ActivityLoadingBinding binding;
    MyCountdownTimer countdownTimer;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int FINE_LOCATION_PERMISSION_CODE = 100;
    private static final int COARSE_LOCATION_PERMISSION_CODE = 101;
    double latitude;
    double longitude;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        Glide.with(this).load(R.drawable.gif1).into(binding.stormy);

        countdownTimer = new MyCountdownTimer(5000, 1000);
        countdownTimer.start();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                FINE_LOCATION_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                COARSE_LOCATION_PERMISSION_CODE);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION_PERMISSION_CODE);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.d(TAG,"Error trying to get last GPS location");
                e.printStackTrace();
            }
        });
    }

    private void checkPermission(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(LoadingActivity.this, permission) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{permission},
                    requestCode);
        } else {
            Toast.makeText(LoadingActivity.this, "Permission already",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoadingActivity.this, "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
                getLastLocation();
            } else {
                Toast.makeText(LoadingActivity.this, "Fine Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == COARSE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoadingActivity.this, "Coarse Location Permission Granted", Toast.LENGTH_SHORT).show();
                getLastLocation();
            } else {
                Toast.makeText(LoadingActivity.this, "Coarse Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("IVAN DEBUGGING", "Error trying to get last location");
                    e.printStackTrace();
                }
            });
        }
    }


    public class MyCountdownTimer extends CountDownTimer
    {
        int progress = 0;
        public MyCountdownTimer(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            progress = progress + 20;
            binding.loadingProgressBar.setProgress(progress);
        }

        @Override
        public void onFinish()
        {
            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
            intent.putExtra("LATITUDE", String.valueOf(latitude));
            intent.putExtra("LONGITUDE", String.valueOf(longitude));
            startActivity(intent);
        }
    }
}