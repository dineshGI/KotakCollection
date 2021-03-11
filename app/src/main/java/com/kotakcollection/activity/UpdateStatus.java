package com.kotakcollection.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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


public class UpdateStatus extends AppCompatActivity implements View.OnClickListener {
    RadioGroup radiogroup;
    EditText EditRemarks, EdMobileNo, EdLandlineNo;
    TextView cancel, submit, title, TxAmount;
    private GpsTracker gpsTracker;
    AlertDialog.Builder alertDialogBuilder;
    String appid, ResponseData;
    static String waybillno;
    LinearLayout hidelayout;
    CommonAlertDialog alert;
    LinearLayout LayoutAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_status);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radiogroup = findViewById(R.id.radiobtn);
        LayoutAmount = findViewById(R.id.lyAmount);
        EdMobileNo = findViewById(R.id.mobileno);
        EdLandlineNo = findViewById(R.id.landlineno);
        EditRemarks = findViewById(R.id.remarks);
        TxAmount = findViewById(R.id.showamount);
        cancel = findViewById(R.id.btn_cancel);
        hidelayout = findViewById(R.id.dialog_layout);
        cancel.setOnClickListener(this);
        title = findViewById(R.id.radiotitle);

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
        Util.saveData("StatusId", "", getApplicationContext());
        Util.Logcat.e("from:::" + from);
        Util.saveData("radioselection", "nodata", getApplicationContext());
        if (from.equalsIgnoreCase("DisputeManagement")) {
            DisputeManagement();
        } else {
            //LayoutAmount.setVisibility(View.GONE);
            NCvalidation();
        }
    }

    private void DisputeManagement() {
        title.setText("Select Lead Status");
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("ClientId", getString(R.string.clientid));
            obj.put("product", "Dispute_Mgmt");
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(this, params.toString(), Util.DISPUTE_MGNT_RADIO, new VolleyResponseListener() {
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

                        if (resobject.getString("StatusRepId").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("_getStatusUpdate");
                            ResponseData = jsonArray.toString();

                            if (jsonArray != null && jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    RadioButton radioButton = new RadioButton(UpdateStatus.this);
                                    radioButton.setTextSize(15);
                                    radioButton.setText(jsonObject.optString("StatusDesc"));
                                    radioButton.setTextColor(getResources().getColor(R.color.text_primary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
                                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                                    radiogroup.addView(radioButton);
                                    Util.Logcat.e("StatusDesc" + jsonObject.optString("StatusDesc"));
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
                                        if (selection.equalsIgnoreCase("Not Resolved")) {
                                            EditRemarks.setVisibility(View.VISIBLE);
                                        } else {
                                            EditRemarks.setVisibility(View.GONE);
                                        }

                                        try {
                                            JSONArray array = new JSONArray(ResponseData);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject jsonObject = array.getJSONObject(i);
                                                if (selection.equalsIgnoreCase(jsonObject.getString("StatusDesc"))) {
                                                    Util.saveData("StatusId", jsonObject.getString("StatusId"), getApplicationContext());
                                                    Util.Logcat.e("StatusId::" + jsonObject.getString("StatusId"));
                                                    break;
                                                }
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        } else {
                            alertDialogBuilder.setMessage(resobject.getString("StatusRepDesc"));
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

    private void NCvalidation() {
        title.setText("New Number Available");
        RadioButton CashradioButton = new RadioButton(UpdateStatus.this);
        CashradioButton.setTextSize(15);
        CashradioButton.setText("Yes");
        CashradioButton.setTextColor(getResources().getColor(R.color.text_primary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
        CashradioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        radiogroup.addView(CashradioButton);
        RadioButton ChequeradioButton = new RadioButton(UpdateStatus.this);
        ChequeradioButton.setTextSize(15);
        ChequeradioButton.setText("No");
        ChequeradioButton.setTextColor(getResources().getColor(R.color.text_primary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
        ChequeradioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        radiogroup.addView(ChequeradioButton);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioButtonId = group.getCheckedRadioButtonId();
                View radioButton = radiogroup.findViewById(checkedRadioButtonId);
                int radioId = radiogroup.indexOfChild(radioButton);
                RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                String selection = (String) btn.getText();
                Util.saveData("radioselection", selection, getApplicationContext());
                if (selection.equalsIgnoreCase("Yes")) {
                    EdMobileNo.setVisibility(View.VISIBLE);
                    EdLandlineNo.setVisibility(View.VISIBLE);
                } else {
                    EdMobileNo.setVisibility(View.GONE);
                    EdLandlineNo.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:

                if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("nodata")) {
                    alert.build(title.getText().toString());
                } else if (EdMobileNo.getVisibility() == View.VISIBLE && EdMobileNo.getEditableText().toString().isEmpty() && EdLandlineNo.getEditableText().toString().isEmpty()) {
                    alert.build(getString(R.string.enter_mobileno_landline));
                } else if (EditRemarks.getVisibility() == View.VISIBLE && EditRemarks.getEditableText().toString().isEmpty()) {
                    alert.build(getString(R.string.enter_remarks));
                } else {
                    hidelayout.setVisibility(View.INVISIBLE);
                    Util.Logcat.e("data::::" + Util.getData("radioselection", getApplicationContext()));
                    callResolved();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    private void callResolved() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("SAUserId", Util.getData("UserId", getApplicationContext()));
            obj.put("WaybillNo", waybillno);
            obj.put("DeliveryDate", "");
            obj.put("CheqNo", "");
            obj.put("BankName", "");
            obj.put("CheqDate", "");
            obj.put("PaymentModeId", "");
            obj.put("SMASelection", "");
            obj.put("Remarks", EditRemarks.getEditableText().toString());
            obj.put("MobileNo", EdMobileNo.getEditableText().toString());
            obj.put("LandLineNo", EdLandlineNo.getEditableText().toString());
            if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("Yes")) {
                obj.put("StatusId", "4");
            } else if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("No")) {
                obj.put("StatusId", "5");
            } else {
                obj.put("StatusId", Util.getData("StatusId", getApplicationContext()));
            }

            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }

            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(UpdateStatus.this, params.toString(), COLLECT, new VolleyResponseListener() {
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
                            Intent alert = new Intent(UpdateStatus.this, AlertActivity.class);
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