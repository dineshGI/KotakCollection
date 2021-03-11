package com.kotakcollection.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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


public class Denomination extends AppCompatActivity {

    AlertDialog.Builder alertDialogBuilder;

    CommonAlertDialog alert;
    TextView amt;
    static TextView TxtDTotal;
    Button submit;
    String apidata = "";
    EditText EdD_2000, EdD_500, EdD_200, EdD_100, EdD_50, EdD_20, EdD_10, EdD_5, EdD_2, EdD_1;
    public static EditText Ed_2000, Ed_500, Ed_200, Ed_100, Ed_50, Ed_20, Ed_10, Ed_5, Ed_2, Ed_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.denomination);
        //  getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alert = new CommonAlertDialog(this);
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        alertDialogBuilder.setCancelable(false);
        amt = findViewById(R.id.amt);
        submit = findViewById(R.id.submit);
        TxtDTotal = findViewById(R.id.total);

        apidata = getIntent().getStringExtra("apidata");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calldata();

            }
        });

        Init();
    }

    private void Init() {
        EdD_2000 = findViewById(R.id.d_2000);
        EdD_2000.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 2000;
                    Ed_2000.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_2000.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_500 = findViewById(R.id.d_500);
        EdD_500.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 500;
                    Ed_500.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_500.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_200 = findViewById(R.id.d_200);
        EdD_200.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 200;
                    Ed_200.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_200.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_100 = findViewById(R.id.d_100);
        EdD_100.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 100;
                    Ed_100.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_100.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_50 = findViewById(R.id.d_50);
        EdD_50.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 50;
                    Ed_50.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_50.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_20 = findViewById(R.id.d_20);
        EdD_20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 20;
                    Ed_20.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_20.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_10 = findViewById(R.id.d_10);
        EdD_10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 10;
                    Ed_10.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_10.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_5 = findViewById(R.id.d_5);
        EdD_5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 5;
                    Ed_5.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_5.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_2 = findViewById(R.id.d_2);
        EdD_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 2;
                    Ed_2.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_2.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EdD_1 = findViewById(R.id.d_1);
        EdD_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!String.valueOf(s).equalsIgnoreCase("")) {
                    int value = Integer.parseInt(String.valueOf(s)) * 1;
                    Ed_1.setText(String.valueOf(value));
                    getdenomination();
                } else {
                    Ed_1.setText("");
                    getdenomination();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Ed_2000 = findViewById(R.id.ed_2000);
        Ed_500 = findViewById(R.id.ed_500);
        Ed_200 = findViewById(R.id.ed_200);
        Ed_100 = findViewById(R.id.ed_100);
        Ed_50 = findViewById(R.id.ed_50);
        Ed_20 = findViewById(R.id.ed_20);
        Ed_10 = findViewById(R.id.ed_10);
        Ed_5 = findViewById(R.id.ed_5);
        Ed_2 = findViewById(R.id.ed_2);
        Ed_1 = findViewById(R.id.ed_1);
    }

    private static int getdenomination() {
        int value = 0;
        if (!Ed_2000.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_2000.getEditableText().toString());
        }
        if (!Ed_500.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_500.getEditableText().toString());
        }
        if (!Ed_200.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_200.getEditableText().toString());
        }
        if (!Ed_100.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_100.getEditableText().toString());
        }
        if (!Ed_50.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_50.getEditableText().toString());
        }
        if (!Ed_20.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_20.getEditableText().toString());
        }
        if (!Ed_10.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_10.getEditableText().toString());
        }
        if (!Ed_5.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_5.getEditableText().toString());
        }
        if (!Ed_2.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_2.getEditableText().toString());
        }
        if (!Ed_1.getEditableText().toString().isEmpty()) {
            value = value + Integer.parseInt(Ed_1.getEditableText().toString());
        }

        TxtDTotal.setText(String.valueOf(value));

        return value;
    }

    private void reset() {

        EdD_2000.setText("");
        EdD_500.setText("");
        EdD_200.setText("");
        EdD_100.setText("");
        EdD_50.setText("");
        EdD_20.setText("");
        EdD_10.setText("");
        EdD_5.setText("");
        EdD_2.setText("");
        EdD_1.setText("");

        Ed_2000.setText("");
        Ed_500.setText("");
        Ed_200.setText("");
        Ed_100.setText("");
        Ed_50.setText("");
        Ed_20.setText("");
        Ed_10.setText("");
        Ed_5.setText("");
        Ed_2.setText("");
        Ed_1.setText("");


    }

    private void calldata() {
        try {

            CallApi.postResponse(Denomination.this, apidata, COLLECT, new VolleyResponseListener() {
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

                            Intent alert = new Intent(Denomination.this, AlertActivity.class);
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