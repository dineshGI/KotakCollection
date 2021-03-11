package com.kotakcollection.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.kotakcollection.Http.CallApi;
import com.kotakcollection.Printer.MiddleWare;
import com.kotakcollection.Printer.PlayTicket;
import com.kotakcollection.R;
import com.kotakcollection.interfaces.VolleyResponseListener;
import com.kotakcollection.utils.CommonAlertDialog;
import com.kotakcollection.utils.Util;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LoginActivity extends Activity implements View.OnClickListener {

    EditText EdUsername, EdPassword;
    Button BtnLogin;
    private CheckBox chkbox;
    String device;
    private GpsTracker gpsTracker;
    String latitude, longitude;
    String osName;
    CommonAlertDialog alert;
    ProgressDialog loading;
    TextView TxtVersion, TxtFAQ, TxtForgetPass;
    ProgressDialog pd;
    PlayTicket ticket = new PlayTicket();
    BluetoothDevice[] btDeviceList = null;
    ArrayAdapter<String> adtDvcs;
    List<String> lstDvcsStr = new ArrayList<>();
    private List<BluetoothDevice> lstDvcsStr_d = new ArrayList<BluetoothDevice>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH},
                        0);
            }
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        TxtVersion = findViewById(R.id.app_vers);
        TxtFAQ = findViewById(R.id.faq);
        TxtFAQ.setOnClickListener(this);
        TxtForgetPass = findViewById(R.id.forget_password);
        TxtForgetPass.setOnClickListener(this);
        TxtVersion.setText(Util.app_version_name);
        pd = new ProgressDialog(this);
        EdUsername = findViewById(R.id.username);
        EdPassword = findViewById(R.id.password);
        loading = new ProgressDialog(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager autofillManager = getSystemService(AutofillManager.class);
            autofillManager.disableAutofillServices();
            EdUsername.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
            EdPassword.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        BtnLogin = findViewById(R.id.login);
        BtnLogin.setOnClickListener(this);
        chkbox = findViewById(R.id.rememberme);
        alert = new CommonAlertDialog(this);
        gpsTracker = new GpsTracker(LoginActivity.this);

        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());
        }
        devicedetails();
        chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    if (!Util.getData("loginuser", getApplicationContext()).isEmpty()) {
                        EdUsername.setText(Util.getData("loginuser", getApplicationContext()));
                        EdPassword.setText(Util.getData("loginpass", getApplicationContext()));
                    }

                } else {
                    EdUsername.setText("");
                    EdPassword.setText("");
                }

            }
        });

        InitPrinter();
    }

    private void InitPrinter() {

        try {
            MiddleWare.PrnBdevice = null;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                return;
            }

            Set<BluetoothDevice> pairedDevices = null;
            //List<String>mpairedDeviceList=new ArrayList<String>();
            if (mBluetoothAdapter.isEnabled()) {
                String getName = mBluetoothAdapter.getName();
                pairedDevices = mBluetoothAdapter.getBondedDevices();
                while (lstDvcsStr.size() > 1) {
                    lstDvcsStr.remove(1);
                }
                for (BluetoothDevice device : pairedDevices) {
                    getName = device.getName() + "#" + device.getAddress();
                    //mpairedDeviceList.add(getName);
                    lstDvcsStr.add(getName);
                    lstDvcsStr_d.add(device);
                    //mPairedDevicesArrayAdapter.add(getName);
                }
            }

            String[] strDevices = new String[lstDvcsStr.size()];
            btDeviceList = new BluetoothDevice[lstDvcsStr.size()];
            for (int i = 0; i < lstDvcsStr.size(); i++) {
                strDevices[i] = (i + 1) + ". BT Device : " + lstDvcsStr.get(i);
                btDeviceList[i] = lstDvcsStr_d.get(i);
                Log.e("NAME", btDeviceList[i].getName() + "\n" + btDeviceList[i]);

            }

            //  mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
            MiddleWare JMI = new MiddleWare();
            //  String strConfigPrnName = JMI.getPrinterDtls(MainActivity.this);
            String strConfigPrnName = "MY3_1000E8C4DD3D";
            //if(strConfigPrnName.startsWith("MY")||strConfigPrnName.startsWith("X33"))//need to add new printer name
            {
                //String[] strDevices = new String[btDeviceList.length];
                for (int i = 0; i < btDeviceList.length; i++) {
                    //strDevices[i] = (i+1)+". BT Device : " + btDeviceList[i].getName();
                    if (strConfigPrnName.equals(btDeviceList[i].getName())) {
                        MiddleWare.PrnBdevice = btDeviceList[i];
                        //device.setText("Paired : " + btDeviceList[i].getName());
                        break;
                    }
                }
            }
            JMI.ESCPrinter_init(LoginActivity.this, 0);
        } catch (Exception ex) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (EdUsername.getEditableText().toString().isEmpty()) {
                    alert.build(getResources().getString(R.string.enter_username));
                } else if (EdPassword.getEditableText().toString().isEmpty()) {

                    alert.build(getResources().getString(R.string.enter_password));
                } else {

                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    } else {
                        login();
                    }

                }
                break;
            case R.id.faq:
                Intent home = new Intent(LoginActivity.this, FAQActivity.class);
                startActivity(home);
                break;
            case R.id.forget_password:
                if (EdUsername.getEditableText().toString().isEmpty()) {
                    alert.build(getResources().getString(R.string.enter_username));
                } else {
                    ForgetPassword();
                }
                break;
            default:
                break;
        }
    }

    private void login() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("LoginId", EdUsername.getEditableText().toString());
            Util.saveData("LoginId", EdUsername.getEditableText().toString(), getApplicationContext());
            obj.put("Password", Util.EncryptURL(EdPassword.getEditableText().toString()));
            obj.put("DeviceType", "2");
            obj.put("ClientId", "1013");
            obj.put("DeviceInfo", device);
            Util.saveData("DeviceInfo", device, getApplicationContext());
            obj.put("Version", Util.app_version);
            obj.put("Lat", latitude);
            obj.put("Long", longitude);
            obj.put("FCMToken", Util.getData("FCMToken", getApplicationContext()));
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(LoginActivity.this, params.toString(), Util.LOGIN, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(getString(R.string.timeout_error));
                    } else {
                        alert.build(getString(R.string.server_error));
                    }
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        final JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));
                        Util.Logcat.e("set UserId:::" + resobject.getString("UserId"));
                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            Util.saveData("UserId", resobject.getString("UserId"), getApplicationContext());
                            Util.saveData("WorkStatus", resobject.getString("WorkStatus"), getApplicationContext());
                            Util.saveData("UserName", resobject.getString("UserName"), getApplicationContext());
                            Util.saveData("LoginId", resobject.getString("LoginId"), getApplicationContext());
                            Util.saveData("SAId", resobject.getString("SAId"), getApplicationContext());
                            Util.saveData("SABranchId", resobject.getString("SABranchId"), getApplicationContext());
                            Util.saveData("RoleName", resobject.getString("RoleName"), getApplicationContext());
                            Util.saveData("SABranchName", resobject.getString("SABranchName"), getApplicationContext());
                            Util.saveData("EmpCode", resobject.getString("EmpCode"), getApplicationContext());
                            if (chkbox.isChecked() == true) {
                                Util.saveData("loginuser", EdUsername.getEditableText().toString(), getApplicationContext());
                                Util.saveData("loginpass", EdPassword.getEditableText().toString(), getApplicationContext());
                            }

                            if (!resobject.getString("VersionChk").equalsIgnoreCase("1")) {
                                Intent home = new Intent(LoginActivity.this, MainActivity.class);
                                home.putExtra("ClientMapping", resobject.getString("ClientMapping"));
                                startActivity(home);
                                finish();

                            } else {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(LoginActivity.this, R.style.alertDialog);
                                // dlg.setTitle("App Update");
                                dlg.setMessage("Kindly Update New version");
                                dlg.setCancelable(false);
                                dlg.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            update(resobject.getString("Downloadlink"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                dlg.show();
                            }
                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                            alert.build(resobject.getString("StatusDesc"));
                        } else {
                            alert.build(resobject.getString("StatusDesc"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void devicedetails() {

        String device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        try {
            Field[] fields = Build.VERSION_CODES.class.getFields();
            osName = "Android " + fields[Build.VERSION.SDK_INT + 1].getName();
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

       /* TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();*/

       /* String deviceId = android.provider.Settings.Secure.getString(
                this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);*/

        device = device_id + "," + "null" + "," + osName + "," + Build.VERSION.RELEASE + "," + Build.SERIAL + "," + Build.MANUFACTURER + "," + Build.MODEL + "," + "null" + "," + "null" + "," + "null" + "," + latitude + "," + longitude + "," + "null" + ",";
        Util.Logcat.e("device>" + device);
    }

    private void update(String url) {
        //https://github.com/amitshekhariitbhu/Fast-Android-Networking
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        // String url = "http://14.141.212.203/IndostarMobileAPP/indostar.apk";
        String dirPath = "/mnt/sdcard/Download";
        String fileName = "indostar.apk";
        AndroidNetworking.download(url, dirPath, fileName)
                .setTag("downloadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        pd.show();
                        pd.setCancelable(false);
                        pd.setMessage("Updating New Version App. Please Wait...");
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        pd.dismiss();

                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "indostar.apk");

                        if (file.exists()) {

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Uri uri = FileProvider.getUriForFile(LoginActivity.this, "com.indostart", file);
                                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } else {
                                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "ّFile not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle errora
                        Util.Logcat.e(String.valueOf(error));
                        pd.dismiss();
                        alert.build("App Update Failed ! Contact Admin");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // GetUrl();
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            loading.show();
            loading.setMessage("Please wait...");
            loading.setCancelable(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loading.dismiss();
                }
            }, 3000);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage(R.string.enable_gps)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void ForgetPassword() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("LoginId", Util.getData("LoginId", getApplicationContext()));
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(this, params.toString(), Util.FORGETPASSWORD, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(getString(R.string.timeout_error));
                    } else {
                        alert.build(getString(R.string.server_error));
                    }
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse:" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));
                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            alert.build(resobject.getString("StatusDesc"));
                        } else {
                            alert.build(resobject.getString("StatusDesc"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



     /* private void update() {
        //https://github.com/amitshekhariitbhu/Fast-Android-Networking
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        String url = "http://14.141.212.203/isamanapk/sampleapk.apk";
        String dirPath = "/mnt/sdcard/Download";
        String fileName = "sampleapk.apk";
        AndroidNetworking.download(url, dirPath, fileName)
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        pd.show();
                        pd.setCancelable(false);
                        pd.setMessage("Updating App. Please Wait...");
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        pd.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file = new File("/mnt/sdcard/Download/sampleapk.apk");
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle errora
                        alert.build("App Update Failed");

                    }
                });
    }*/

}
