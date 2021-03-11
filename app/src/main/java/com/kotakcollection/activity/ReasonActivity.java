package com.kotakcollection.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.kotakcollection.Http.CallApi;
import com.kotakcollection.R;
import com.kotakcollection.interfaces.VolleyResponseListener;
import com.kotakcollection.utils.CommonAlertDialog;
import com.kotakcollection.utils.PhotoProvider;
import com.kotakcollection.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import static com.kotakcollection.utils.Util.RADIOBUTTON_UPDATE;


public class ReasonActivity extends AppCompatActivity implements View.OnClickListener {

    TextView TxtTitle, TxtDate;
    LinearLayout layout, hidelayout;

    TextView BtnSubmit, BtnCancel;
    private GpsTracker gpsTracker;
    CommonAlertDialog alert;
    RadioGroup RadioSubReason;
    EditText EdReason;
    Uri fileUri;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    String imageStoragePath;
    AlertDialog.Builder alertDialogBuilder;
    static String appid, waybillno, radioselection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kcc_bankreason);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        appid = getIntent().getStringExtra("AppointmentId");
        waybillno = getIntent().getStringExtra("WaybillNumber");
        radioselection = getIntent().getStringExtra("radioselection");
        Util.Logcat.e("radioselection" + radioselection);
        gpsTracker = new GpsTracker(this);
        alert = new CommonAlertDialog(this);
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        alertDialogBuilder.setCancelable(false);
        EdReason = findViewById(R.id.reason);
        RadioSubReason = findViewById(R.id.radiobtn);
        hidelayout = findViewById(R.id.dialog_layout);
        TxtDate = findViewById(R.id.from_date);
        TxtTitle = findViewById(R.id.title);
        layout = findViewById(R.id.from_calendar);
        layout.setOnClickListener(this);
        BtnSubmit = findViewById(R.id.submit);
        BtnSubmit.setOnClickListener(this);
        BtnCancel = findViewById(R.id.cancel);
        BtnCancel.setOnClickListener(this);

        Util.Logcat.e(Util.getData("radioresponse", getApplicationContext()));
        TxtTitle.setText(getString(R.string.select_subreason));
        SetSubreason(radioselection);
    }

    private void SetSubreason(String radioselection) {
        Util.saveData("radiosubselection", "", getApplicationContext());
        try {
            JSONObject resobject = new JSONObject(Util.getData("radioresponse", getApplicationContext()));
            JSONArray jsonArray = resobject.optJSONArray("_RRFailedModel");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (radioselection.equalsIgnoreCase(jsonObject.optString("PickupFailedReasonDesc"))) {
                    JSONArray model = jsonObject.getJSONArray("_RRFailedDetailsModel");
                    for (int j = 0; j < model.length(); j++) {
                        JSONObject inside = model.getJSONObject(j);
                        RadioButton radioButton = new RadioButton(ReasonActivity.this);
                        radioButton.setTextSize(15);
                        radioButton.setText(inside.optString("PickupFailedSubReasonDesc"));
                        radioButton.setTextColor(getResources().getColor(R.color.text_secondary));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                        }
                        radioButton.setHighlightColor(getResources().getColor(R.color.colorPrimary));
                        radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                        RadioSubReason.addView(radioButton);
                        Util.Logcat.e("PickupSubReasonDesc>>" + inside.optString("PickupFailedSubReasonDesc"));
                    }
                }
                RadioSubReason.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int checkedRadioButtonId = group.getCheckedRadioButtonId();
                        View radioButton = RadioSubReason.findViewById(checkedRadioButtonId);
                        int radioId = RadioSubReason.indexOfChild(radioButton);
                        RadioButton btn = (RadioButton) RadioSubReason.getChildAt(radioId);
                        String selection = (String) btn.getText();
                        Util.saveData("radiosubselection", selection, getApplicationContext());
                        //if (selection.contains("PTP - DATE")||selection.contains("CALL BACK")) {
                        if (selection.contains("PTP - DATE")) {
                            layout.setVisibility(View.VISIBLE);
                        } else {
                            layout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.submit:

                if (RadioSubReason.getVisibility() == View.VISIBLE && Util.getData("radiosubselection", getApplicationContext()).isEmpty()) {

                    alert.build(getString(R.string.select_subreason));

                } else if (EdReason.getEditableText().toString().isEmpty()) {
                    alert.build(getString(R.string.enter_remarks));
                } else if (layout.getVisibility() == View.VISIBLE && TxtDate.getText().toString().isEmpty()) {
                    alert.build(getString(R.string.select_date));
                } else {
                    Util.Logcat.e("CashCollectionFailed" + "Success");
                    hidelayout.setVisibility(View.GONE);
                    CashCollectionFailedwithoutImg();
                    /*if (radioselection.equalsIgnoreCase("CUST NOT RESPONDING")) {
                        Opencamera();
                    } else {
                        CashCollectionFailedwithoutImg();
                    }*/
                }
                break;

            case R.id.from_calendar:
                final Calendar c = Calendar.getInstance();
                Integer mYear = c.get(Calendar.YEAR);
                Integer mMonth = c.get(Calendar.MONTH);
                Integer mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ReasonActivity.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (monthOfYear <= 8 && dayOfMonth > 9) {
                            String _data = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                            TxtDate.setText(_data);
                        } else if (monthOfYear <= 8 && dayOfMonth <= 9) {
                            String _data = "0" + dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                            TxtDate.setText(_data);
                        } else {
                            if (dayOfMonth <= 9) {
                                String _data = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                TxtDate.setText(_data);
                            } else {
                                String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                TxtDate.setText(_data);
                            }

                        }

                        /*String _data = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                        TxtDate.setText(_data);*/
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ReasonActivity.this, R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // String _dataHora = TxtDate.getText().toString() + " " + hourOfDay + ":" + minute + ":" + "00";
                                // String _dataHora = TxtDateTime.getText().toString() + " " + "15" + ":" + "00" + ":" + "00";
                                String _dataHora = TxtDate.getText().toString() + " " + String.format("%02d:%02d", hourOfDay, minute) + ":" + "00";
                                TxtDate.setText(_dataHora);
                            }
                        }, 0, 0, true);
                        timePickerDialog.setCancelable(false);
                        timePickerDialog.show();
                        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                break;

            case R.id.cancel:
                //hidelayout.setVisibility(View.INVISIBLE);
                finish();
                break;
            default:
                break;
        }
    }

    private void CashCollectionFailedwithoutImg() {
        hidelayout.setVisibility(View.INVISIBLE);
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("SAUserId", Util.getData("UserId", getApplicationContext()));
            obj.put("AppointmentId", appid);
            obj.put("WaybillNo", waybillno);
            obj.put("PickupFailedUpdate", Util.getData("radiosubselection", getApplicationContext()));
            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }
            obj.put("Remarks", EdReason.getEditableText().toString());
            obj.put("PTPDate", TxtDate.getText().toString());
            obj.put("DeliveryDate", TxtDate.getText().toString());
            obj.put("StatusId", "2");
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(this, params.toString(), RADIOBUTTON_UPDATE, new VolleyResponseListener() {
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
                            alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                            alertDialogBuilder.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                            alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                            alertDialogBuilder.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            finish();

                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        }

                        Util.saveData("radioselection", "nodata", getApplicationContext());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void Opencamera() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    //  PROFILE_PIC_COUNT = 1;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File file = PhotoProvider.getOutputMediaFile(1);
                    if (file != null) {
                        imageStoragePath = file.getAbsolutePath();
                    }

                    Uri fileUri = PhotoProvider.getOutputMediaFileUri(ReasonActivity.this, file);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[item].equals("Choose from Library")) {
                    //  PROFILE_PIC_COUNT = 1;
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    //  PROFILE_PIC_COUNT = 0;
                    dialog.dismiss();
                    finish();
                }
            }
        });
        builder.show();
    }

    private void CashCollectionFailed(final Bitmap bitmap) {
        hidelayout.setVisibility(View.INVISIBLE);

        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("SAUserId", Util.getData("UserId", getApplicationContext()));
            obj.put("AppointmentId", appid);
            obj.put("WaybillNo", waybillno);
            obj.put("PickupFailedUpdate", Util.getData("radiosubselection", getApplicationContext()));
            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }
            obj.put("Remarks", EdReason.getEditableText().toString());
            obj.put("PTPDate", TxtDate.getText().toString());
            obj.put("DeliveryDate", TxtDate.getText().toString());

            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(this, params.toString(), RADIOBUTTON_UPDATE, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse:" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));
                        saveImage(bitmap, resobject.getString("StatusDesc"));
                        Util.saveData("radioselection", "nodata", getApplicationContext());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        finish();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    final void saveImage(Bitmap signature, String msg) {

        String root = Environment.getExternalStorageDirectory().toString();

        // the directory where the signature will be saved

        File myDir = new File(root + "/" + Util.getData("directory", getApplicationContext()));

        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        // set the file name of your choice
        String fname = "P_" + waybillno + ".jpg";

        // in our case, we delete the previous file, you can remove this
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        try {
            // save the signature
            FileOutputStream out = new FileOutputStream(file);
            signature.compress(Bitmap.CompressFormat.JPEG, 100, out);
            //signature.compress(Bitmap.CompressFormat.JPEG, 100, out);
            signature.sameAs(signature);
            out.flush();
            out.close();

            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Util.Logcat.e("RESULT:::" + String.valueOf(requestCode));
        // if the result is capturing Image
        switch (requestCode) {
            case REQUEST_CAMERA:
                Bitmap bitmap = PhotoProvider.optimizeBitmap(8, imageStoragePath);
                if (bitmap != null) {
                    try {
                        // PhotoProvider.refreshGallery(getActivity(), imageStoragePath);
                        bitmap = PhotoProvider.optimizeBitmap(8, imageStoragePath);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
                        CashCollectionFailed(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;
            case SELECT_FILE:
                if (resultCode == RESULT_OK && data != null) {
                    Toast.makeText(this,
                            "Image Selected ", Toast.LENGTH_SHORT)
                            .show();

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bmap = BitmapFactory.decodeFile(picturePath);

                   /* Uri selectedImageUri = data.getData();
                    String selectedImagePath = getRealPathFromURI(selectedImageUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    options.inSampleSize = 8;
                    Bitmap bmap = BitmapFactory.decodeFile(selectedImagePath,
                            options);
                    bmap.compress(Bitmap.CompressFormat.PNG, 90, stream);*/
                    CashCollectionFailed(bmap);
                } else {
                    finish();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}