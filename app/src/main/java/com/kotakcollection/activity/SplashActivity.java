package com.kotakcollection.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kotakcollection.R;
import com.kotakcollection.utils.Util;


public class SplashActivity extends AppCompatActivity {
    private static final long DELAY_TIME = 3000;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        getSupportActionBar().hide();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Util.saveData("FCMToken", token, getApplicationContext());
                        // Log and toast
                        Util.Logcat.e("MSG - TOKEN" + token);
                        //  Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    private void init() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(home);
                finish();
            }
        }, DELAY_TIME);
    }
}
