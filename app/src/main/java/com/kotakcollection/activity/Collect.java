package com.kotakcollection.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.kotakcollection.Http.CallApi;
import com.kotakcollection.R;
import com.kotakcollection.interfaces.VolleyResponseListener;
import com.kotakcollection.utils.CommonAlertDialog;
import com.kotakcollection.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.kotakcollection.utils.Util.COLLECT;


public class Collect extends AppCompatActivity implements View.OnClickListener {
    RadioGroup radiogroup;
    EditText EditRemarks;
    TextView cancel, submit, title, TxAmount;
    private GpsTracker gpsTracker;
    AlertDialog.Builder alertDialogBuilder;
    String appid, ResponseData;
    static String waybillno;
    LinearLayout hidelayout;
    CommonAlertDialog alert;
    LinearLayout LayoutAmount;
    String amount, selection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radiogroup = findViewById(R.id.radiobtn);
        LayoutAmount = findViewById(R.id.lyAmount);
        EditRemarks = findViewById(R.id.remarks);
        TxAmount = findViewById(R.id.showamount);
        cancel = findViewById(R.id.btn_cancel);
        hidelayout = findViewById(R.id.dialog_layout);
        cancel.setOnClickListener(this);
        title = findViewById(R.id.radiotitle);
        title.setText(getString(R.string.choose_payment));
        submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);
        alert = new CommonAlertDialog(this);
        gpsTracker = new GpsTracker(this);
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        alertDialogBuilder.setCancelable(false);
        Intent intent = getIntent();
        appid = intent.getStringExtra("AppointmentId");
        waybillno = intent.getStringExtra("WaybillNumber");
        String from = intent.getStringExtra("from");
        amount = intent.getStringExtra("amount");

        TxAmount.setText("Amount to be Collected " + "(" + amount + ")");
        Util.Logcat.e("from:::" + from);
        Util.saveData("radioselection", "nodata", getApplicationContext());
        Collect();
    }

    private void Collect() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            Util.Logcat.e("GET_PAYMENT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(this, params.toString(), Util.GET_PAYMENT, new VolleyResponseListener() {
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
                        Util.Logcat.e("GET_PAYMENT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONArray jsonArray = new JSONArray(Util.Decrypt(response.getString("Postresponse")));
                        ResponseData = jsonArray.toString();
                        Util.Logcat.e("jsonArray lenght:::" + jsonArray.length());
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RadioButton radioButton = new RadioButton(Collect.this);
                                radioButton.setTextSize(15);
                                radioButton.setText(jsonObject.getString("PaymentType"));
                                radioButton.setTextColor(getResources().getColor(R.color.text_primary));
                                radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                                radiogroup.addView(radioButton);
                                Util.Logcat.e("PaymentType" + jsonObject.getString("PaymentType"));
                            }

                            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {

                                    int checkedRadioButtonId = group.getCheckedRadioButtonId();
                                    View radioButton = radiogroup.findViewById(checkedRadioButtonId);
                                    int radioId = radiogroup.indexOfChild(radioButton);
                                    RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                                    selection = (String) btn.getText();
                                    Log.e("SELECTION", selection);
                                    Util.saveData("radioselection", selection, getApplicationContext());

                                    try {
                                        JSONArray array = new JSONArray(ResponseData);
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            if (selection.equalsIgnoreCase(jsonObject.getString("PaymentType"))) {
                                                Util.saveData("PaymentTypeId", jsonObject.getString("PaymentTypeId"), getApplicationContext());
                                                break;
                                            }
                                        }
                                        if ("5".equalsIgnoreCase(Util.getData("PaymentTypeId", getApplicationContext()))) {
                                            EditRemarks.setVisibility(View.VISIBLE);
                                        } else {
                                            EditRemarks.setVisibility(View.GONE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            alertDialogBuilder.setMessage(getString(R.string.server_empty));
                            alertDialogBuilder.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialogBuilder.setCancelable(false);
                            alertDialog.show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:

                if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("nodata")) {
                    alert.build(title.getText().toString());
                } else if (EditRemarks.getVisibility() == View.VISIBLE && EditRemarks.getEditableText().toString().isEmpty()) {
                    alert.build(getString(R.string.enter_remarks));
                } else {
                    hidelayout.setVisibility(View.INVISIBLE);
                    Util.Logcat.e("data::::" + Util.getData("radioselection", getApplicationContext()));

                    if (selection.contains("Cash")) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
                            obj.put("SAUserId", Util.getData("UserId", getApplicationContext()));
                            obj.put("WaybillNo", waybillno);
                            obj.put("DeliveryDate", "");
                            obj.put("CheqNo", "");
                            obj.put("BankName", "");
                            obj.put("CheqDate", "");
                            obj.put("PaymentModeId", Util.getData("PaymentTypeId", getApplicationContext()));
                            obj.put("SMASelection", "");
                            obj.put("Remarks", EditRemarks.getEditableText().toString());
                            obj.put("StatusId", "1");
                            obj.put("MobileNo", "");
                            obj.put("LandLineNo", "");

                            if (gpsTracker.canGetLocation()) {
                                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
                            }
                            Util.Logcat.e("INPUT:::" + obj.toString());
                            String data = Util.EncryptURL(obj.toString());
                            JSONObject params = new JSONObject();
                            params.put("Getrequestresponse", data);

                            Intent amtactivity = new Intent(Collect.this, Denomination.class);
                            amtactivity.putExtra("apidata", params.toString());
                            // amtactivity.putExtra("amt",params.toString());
                            startActivity(amtactivity);
                            finish();
                        } catch (JSONException e) {

                        }
                    } else {
                        callCollect();
                    }
                }

                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    private void callCollect() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("SAUserId", Util.getData("UserId", getApplicationContext()));
            obj.put("WaybillNo", waybillno);
            obj.put("DeliveryDate", "");
            obj.put("CheqNo", "");
            obj.put("BankName", "");
            obj.put("CheqDate", "");
            obj.put("PaymentModeId", Util.getData("PaymentTypeId", getApplicationContext()));
            obj.put("SMASelection", "");
            obj.put("Remarks", EditRemarks.getEditableText().toString());
            obj.put("StatusId", "1");
            obj.put("MobileNo", "");
            obj.put("LandLineNo", "");

            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(Collect.this, params.toString(), COLLECT, new VolleyResponseListener() {
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
                        Util.Logcat.e("COLLECT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            Intent alert = new Intent(Collect.this, AlertActivity.class);
                            alert.putExtra("status", resobject.getString("Status"));
                            alert.putExtra("StatusDesc", resobject.getString("StatusDesc"));
                            alert.putExtra("ReceiptNo", resobject.getString("ReceiptNo"));
                            alert.putExtra("ReceiptDate", resobject.getString("ReceiptDate"));
                            alert.putExtra("PaymentType", resobject.getString("PaymentType"));
                            alert.putExtra("CollectedAmount", resobject.getString("CollectedAmount"));
                            alert.putExtra("CustomerName", resobject.getString("CustomerName"));
                            alert.putExtra("AccountNo", resobject.getString("AccountNo"));
                            alert.putExtra("CustomerMobileNo", resobject.getString("CustomerMobileNo"));
                            alert.putExtra("FEName", resobject.getString("FEName"));
                            startActivity(alert);
                            finish();

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
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

}