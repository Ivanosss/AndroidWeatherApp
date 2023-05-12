package com.example.climateapplication.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.climateapplication.R;
import com.example.climateapplication.databinding.ActivityMainBinding;
import com.example.climateapplication.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    String currentLocation;

    String latitude;
    String longitude;

    ImageView mWeatherImageView;

    private static class WeatherAsyncTask extends AsyncTask<Void, Void, String> {
        private WeakReference<MainActivity> activityRef;
        private String currentLocation;

        public WeatherAsyncTask(MainActivity activity, String currentLocation) {
            activityRef = new WeakReference<>(activity);
            this.currentLocation = currentLocation;
        }

        @Override
        protected String doInBackground(Void... params) {
            MainActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            try {
                OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(Network.openWeahterAPI + "forecast.json?key=" + Network.getOpenWeahterAPIKEY
                                + "&q=" + currentLocation + "&days=1&aqi=no&alerts=no")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    return null;
                }
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            MainActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            if (s != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    JSONObject locationObject = jsonResponse.getJSONObject("location");
                    String locationName = locationObject.getString("name");
                    JSONObject currentObject = jsonResponse.getJSONObject("current");
                    String temp_c = currentObject.getString("temp_c");
                    JSONObject forcastingObject = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day");
                    String minTemp = forcastingObject.getString("mintemp_c");
                    String maxTemp = forcastingObject.getString("maxtemp_c");
                    String imageUrl = currentObject.getJSONObject("condition").getString("icon");
                    imageUrl = "https:" + imageUrl.replace("64", "128");
                    Log.d("IVAN DEBUGGING444", locationName);
                    activity.updateLocationText(locationName, temp_c,minTemp,maxTemp, imageUrl);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mWeatherImageView = binding.weatherImage;

        Intent intent = getIntent();

        latitude = intent.getStringExtra("LATITUDE");
        longitude = intent.getStringExtra("LONGITUDE");
        currentLocation = latitude + "," + longitude;
        WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask(this, currentLocation);
        weatherAsyncTask.execute();

        Log.d("IVAN DEBUGGING", currentLocation);
    }

    private void updateLocationText(String location, String temp_c, String mintemp, String maxtemp, String imageUrl) {
        String formatTempc = temp_c + "\u2103";
        String formatMin = mintemp +  "\u2103";
        String formatMax = maxtemp +  "\u2103";
        binding.locationText.setText(location);
        binding.currentText.setText(formatTempc);
        binding.maximumText.setText(formatMax);
        binding.minimumText.setText(formatMin);
        Glide.with(MainActivity.this).load(imageUrl).into(mWeatherImageView);
    }
}
