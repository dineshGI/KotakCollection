package com.kotakcollection.Module;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;


import com.kotakcollection.Http.CallApi;
import com.kotakcollection.R;
import com.kotakcollection.interfaces.VolleyResponseListener;
import com.kotakcollection.utils.CommonAlertDialog;
import com.kotakcollection.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import static com.kotakcollection.utils.Util.CHANGEPASSWORD;


public class ChangePassword extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ProgressDialog progressDialog;
    EditText OldPassword, NewPassword, ConfirmPassword;
    Button BtnSubmit;
    CommonAlertDialog alert;

    private OnFragmentInteractionListener mListener;

    public ChangePassword() {
        // Required empty public constructor
    }


    public static ChangePassword newInstance(String param1, String param2) {
        ChangePassword fragment = new ChangePassword();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.change_password, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
                return false;
            }
        });
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        alert = new CommonAlertDialog(getActivity());
        OldPassword = rootView.findViewById(R.id.old_pass);
        NewPassword = rootView.findViewById(R.id.new_pass);
        ConfirmPassword = rootView.findViewById(R.id.confirm_pass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager autofillManager = getActivity().getSystemService(AutofillManager.class);
            autofillManager.disableAutofillServices();
            OldPassword.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
            NewPassword.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
            ConfirmPassword.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }
        BtnSubmit = rootView.findViewById(R.id.change_pass);
        BtnSubmit.setOnClickListener(this);
        return rootView;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

      @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_pass:
                //OldPassword, NewPassword, ConfirmPassword
                if (OldPassword.getEditableText().toString().isEmpty()) {
                    alert.build("Enter Old Password");
                } else if (NewPassword.getEditableText().toString().isEmpty()) {
                    alert.build("Enter New Password");
                } else if (ConfirmPassword.getEditableText().toString().isEmpty()) {
                    alert.build("Enter Confirm Password");
                } else if (!NewPassword.getEditableText().toString().equalsIgnoreCase(ConfirmPassword.getEditableText().toString())) {
                    alert.build("New Password & Confirm Password should be same");
                } else {
                    callApi();
                }
                break;
            default:
                break;
        }
    }

    private void callApi() {

        try {
            JSONObject obj = new JSONObject();
            obj.put("LoginId", Util.getData("LoginId", getActivity().getApplicationContext()));
            obj.put("OldPassword", Util.EncryptURL(OldPassword.getEditableText().toString()));
            obj.put("NewPassord", Util.EncryptURL(NewPassword.getEditableText().toString()));
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            Util.Logcat.e("INPUT:::"+ obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(getActivity(), params.toString(), CHANGEPASSWORD, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(getString(R.string.timeout_error));
                    } else {
                        alert.build(getString(R.string.server_error));
                    }
                     Util.Logcat.e("onError"+ message);
                }

                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            alert.build(resobject.getString("StatusDesc"));
                            OldPassword.setText("");
                            NewPassword.setText("");
                            ConfirmPassword.setText("");

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.start_stop);
        item.setVisible(false);
       /* MenuItem bulk_scan = menu.findItem(R.id.bulk_scan);
        bulk_scan.setVisible(false);
        MenuItem delivery = menu.findItem(R.id.delivery);
        delivery.setVisible(false);*/
    }
}