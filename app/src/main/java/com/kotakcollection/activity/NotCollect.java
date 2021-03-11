package com.kotakcollection.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
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


public class NotCollect extends AppCompatActivity implements View.OnClickListener {
    RadioGroup radiogroup;
    // EditText EditAmount;
    TextView cancel, submit, title, TxAmount;
    private GpsTracker gpsTracker;
    AlertDialog.Builder alertDialogBuilder;
    String appid;
    static String waybillno;
    LinearLayout hidelayout;
    CommonAlertDialog alert;
    LinearLayout LayoutAmount;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.not_collect);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radiogroup = findViewById(R.id.radiobtn);
        LayoutAmount = findViewById(R.id.lyAmount);
        TxAmount = findViewById(R.id.showamount);
        cancel = findViewById(R.id.btn_cancel);
        hidelayout = findViewById(R.id.dialog_layout);
        cancel.setOnClickListener(this);
        title = findViewById(R.id.radiotitle);
        title.setText(getString(R.string.choose_reason));
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
        NotCollect();
    }

    private void NotCollect() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(this, params.toString(), Util.RADIOBUTTON, new VolleyResponseListener() {
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
                        Util.saveData("radioresponse", resobject.toString(), getApplicationContext());
                        JSONArray jsonArray = resobject.optJSONArray("_RRFailedModel");
                        if (jsonArray != null && jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RadioButton radioButton = new RadioButton(NotCollect.this);
                                radioButton.setTextSize(15);
                                radioButton.setText(jsonObject.optString("PickupFailedReasonDesc"));
                                radioButton.setTextColor(getResources().getColor(R.color.text_primary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
                                radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                                radiogroup.addView(radioButton);
                                Util.Logcat.e("DeliveryFailed" + jsonObject.optString("DeliveryFailedReasonDesc"));
                            }

                            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {

                                    int checkedRadioButtonId = group.getCheckedRadioButtonId();
                                    View radioButton = radiogroup.findViewById(checkedRadioButtonId);
                                    int radioId = radiogroup.indexOfChild(radioButton);
                                    RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                                    String selection = (String) btn.getText();
                                    Util.saveData("radioselection", selection, getApplicationContext());

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
                } else {
                    hidelayout.setVisibility(View.INVISIBLE);
                    Util.Logcat.e("data::::" + Util.getData("radioselection", getApplicationContext()));
                    Util.Logcat.e("APPOINTMENT" + Util.getData("radioselection", getApplicationContext()));
                    Intent i = new Intent(NotCollect.this, ReasonActivity.class);
                    i.putExtra("AppointmentId", appid);
                    i.putExtra("WaybillNumber", waybillno);
                    i.putExtra("radioselection", Util.getData("radioselection", getApplicationContext()));
                    startActivity(i);
                    finish();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }
}