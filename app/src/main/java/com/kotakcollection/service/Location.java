package com.kotakcollection.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.kotakcollection.Http.CallApi;
import com.kotakcollection.R;
import com.kotakcollection.activity.LoginActivity;
import com.kotakcollection.interfaces.VolleyResponseListener;
import com.kotakcollection.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.location.LocationManager.GPS_PROVIDER;

public class Location extends Service {

    //https://deepshikhapuri.wordpress.com/2016/11/25/service-in-android/
    private Handler mHandler = new Handler();
    public static Timer mTimer = null;
    long notify_interval = 1000 * 60 * 3;
    GpsTracker gpsTracker;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        gpsTracker = new GpsTracker(this);
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);

    }

    private void callapi(String lat, String lng) {
        if (Util.isOnline(getApplicationContext())) {

            try {
                JSONObject obj = new JSONObject();
                obj.put("LoginId", Util.getData("LoginId", getApplicationContext()));
                obj.put("DeviceInfo", Util.getData("DeviceInfo", getApplicationContext()));
                obj.put("Lat", lat);
                obj.put("Long", lng);
                Log.e("SERVICE CALL" , obj.toString());
                String data = Util.EncryptURL(obj.toString());
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);
                CallApi.postResponseNopgrss(getApplicationContext(), params.toString(), Util.MOBILE_TRACK, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Util.Logcat.e("onError" + message);
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        Util.Logcat.e("onResponse" + response);
                        try {
                            String hscsai = response.getString("Postresponse");
                            String ff = Util.Decrypt(hscsai);
                            // Util.Logcat.e("SERVICE CALL" + ff);
                            JSONObject hai = new JSONObject(ff);
                            Util.Logcat.e("OUTPUT" + hai);
                            //connect = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.app_name) + "\nPlease check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private class TimerTaskToGetLocation extends TimerTask {

        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    gpsTracker = new GpsTracker(getApplicationContext());
                    if (gpsTracker.canGetLocation()) {
                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(GPS_PROVIDER)|| String.valueOf(gpsTracker.getLatitude()).equalsIgnoreCase("0.0")) {
                            Intent hai = new Intent(Location.this, LoginActivity.class);
                            hai.setFlags(FLAG_ACTIVITY_CLEAR_TASK);
                            hai.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(hai);
                        } else {
                            callapi(String.valueOf(gpsTracker.getLatitude()), String.valueOf(gpsTracker.getLongitude()));
                        }
                    }
                }
            });
        }
    }
}
