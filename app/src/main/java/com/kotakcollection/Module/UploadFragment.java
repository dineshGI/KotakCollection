package com.kotakcollection.Module;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import com.kotakcollection.Http.CallApi;
import com.kotakcollection.R;
import com.kotakcollection.interfaces.VolleyResponseListener;
import com.kotakcollection.utils.CommonAlertDialog;
import com.kotakcollection.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import static com.kotakcollection.utils.Util.UPLOAD;


public class UploadFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //https://api.mlab.com/api/1/databases/blindapp/collections/beacons?apiKey=EVDAtEeJwaIMAwOpjOOxdN2IiMmfLDJI


    private String mParam1;
    private String mParam2;

    ListView layout;
    Button upload;
    TextView NoData;
    AlertDialog.Builder alertDialogBuilder;

    boolean disable = false;
    ProgressDialog progressDialog;
    CommonAlertDialog alert;
    private OnFragmentInteractionListener mListener;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */

    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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

        View rootView = inflater.inflate(R.layout.upload, container, false);
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
        progressDialog = new ProgressDialog(getActivity(), R.style.alertDialog);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        layout = rootView.findViewById(R.id.llayout);
        NoData = rootView.findViewById(R.id.nodata);
        upload = rootView.findViewById(R.id.btn_upload);
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alert = new CommonAlertDialog(getActivity());
        upload.setOnClickListener(this);
        return rootView;
    }

    private void uploadimage(String encodedImage, String waybillno, final String path, String filetype) {
        progressDialog.show();
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("WayBillNo", waybillno);
            obj.put("FileType", filetype);
            obj.put("Filepath", encodedImage);

            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(getActivity(), params.toString(), UPLOAD, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    progressDialog.dismiss();
                    if (message.contains("TimeoutError")) {
                        //alert.build(getString(R.string.timeout_error));
                    } else {
                        //alert.build(getString(R.string.server_error));
                    }
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse" + response);
                    try {
                        String hscsai = response.getString("Postresponse");
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(hscsai));
                        JSONObject resobject = new JSONObject(Util.Decrypt(hscsai));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            File target = new File(path);
                            if (target.exists() && target.isFile() && target.canWrite()) {
                                target.delete();
                                Log.d("file deleted", "" + target.getName());
                            }

                            loadfilenames();

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                            //  alert.build(resobject.getString("StatusDesc"));
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


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:

                File path = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "/" + Util.getData("directory", getActivity().getApplicationContext()));
                File[] files = path.listFiles();

                for (int i = 0; i < files.length; i++) {

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + files[i].getName());
                    String waybillno = files[i].getName().substring(2).replace(".jpg", "").trim();
                    String filetype;
                    if (files[i].getName().contains("P_")) {
                        filetype = "1";
                    } else {
                        filetype = "2";
                    }

                    Util.Logcat.e("waybillno:::"+ waybillno);
                    String filepath = path + "/" + files[i].getName();
                    Util.Logcat.e("Path:::"+ filepath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byte_arr = stream.toByteArray();
                    String encodedImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                    Log.v("da", String.valueOf(encodedImage));
                    ///Now set this bitmap on imageview
                    uploadimage(encodedImage, waybillno, filepath, filetype);

                }
                disable = true;
                break;

            default:
                break;
        }
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadfilenames();
    }

    private void loadfilenames() {
        ArrayList<String> filenames = new ArrayList<String>();
        File directory = new File(Environment.getExternalStorageDirectory()
                + File.separator + "/" + Util.getData("directory", getActivity().getApplicationContext()));
        File[] files = directory.listFiles();
        Log.e("file lenght", String.valueOf(files.length));
        for (int i = 0; i < files.length; i++) {
            String file_name = files[i].getName();
            // you can store name to arraylist and use it later
            filenames.add(file_name);
        }

        layout.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, filenames));
        Util.Logcat.e("filenames"+ filenames);
        if (filenames.isEmpty()) {
            layout.setVisibility(View.GONE);
            NoData.setVisibility(View.VISIBLE);
        }

        if (disable == true) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.start_stop);
        item.setVisible(false);

    }
}