package com.kotakcollection.Module;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.kotakcollection.Http.CallApi;
import com.kotakcollection.adapter.PTPOnlineAdapter;
import com.kotakcollection.R;
import com.kotakcollection.interfaces.VolleyResponseListener;
import com.kotakcollection.utils.CommonAlertDialog;
import com.kotakcollection.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kotakcollection.utils.Util.PICKUP;


public class PTPOnline extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    List<Map<String, String>> searchResults;
    ListView listView;

    public PTPOnlineAdapter adapter;

    TextView count;
    private EditText filterText;
    CommonAlertDialog alert;
    ProgressDialog progressDialog;
    String filter;

    private OnFragmentInteractionListener mListener;

    public PTPOnline() {
        // Required empty public constructor
    }

    public static PTPOnline newInstance(String param1, String param2) {
        PTPOnline fragment = new PTPOnline();
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

        View rootView = inflater.inflate(R.layout.pickup, container, false);
        // Inflate the layout for this fragment
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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        alert = new CommonAlertDialog(getActivity());
        ListCollection = new ArrayList<>();
        listView = rootView.findViewById(R.id.listview);
        filterText = rootView.findViewById(R.id.search);
        count = rootView.findViewById(R.id.count);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        if (bundle.getString("Title").equalsIgnoreCase("New")) {
            filter ="0";
        }else if (bundle.getString("Title").equalsIgnoreCase("Completed")){
            filter="1";
        }else if (bundle.getString("Title").equalsIgnoreCase("NotCompleted")){
            filter="2";
        }
        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    private void loadpickuplist() {
        filterText.setText("");
        ListCollection.clear();
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAId", Util.getData("SAId", getActivity().getApplicationContext()));
            obj.put("SABranchId", Util.getData("SABranchId", getActivity().getApplicationContext()));
            obj.put("TaskType", "2");
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("ClientId", getString(R.string.clientid));
            obj.put("FilterType", filter);
            obj.put("product", getString(R.string.product_ptp_online));
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), PICKUP, new VolleyResponseListener() {
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
                    ListCollection.clear();
                    Util.Logcat.e("onResponse" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("orders");

                            if (jsonArray == null) {
                                //txtnodata.setVisibility(View.VISIBLE);
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                count.setText("Total - " + "0");
                                alert.build(getString(R.string.nopickup_available));
                            } else if (jsonArray.length() == 0) {
                                count.setText("Total - " + "0");
                                alert.build(getString(R.string.nopickup_available));
                            } else {
                                Util.Logcat.e("length" + jsonArray.length());
                                count.setText("Total - " + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    DataHashMap = new HashMap<>();
                                    DataHashMap.put("ShipmentId", jsonObject.optString("ShipmentId"));
                                    DataHashMap.put("CreatedDT", jsonObject.optString("CreatedDT"));
                                    DataHashMap.put("SellerName", jsonObject.optString("SellerName"));
                                    DataHashMap.put("SellerAddress", jsonObject.optString("SellerAddress") + " " + jsonObject.optString("SellerCity"));
                                    DataHashMap.put("SellerContactNo", jsonObject.optString("SellerMobileNo"));
                                    DataHashMap.put("Lat", jsonObject.optString("Lat"));
                                    DataHashMap.put("Long", jsonObject.optString("Long"));
                                    DataHashMap.put("ClientName", jsonObject.optString("ClientName"));
                                    DataHashMap.put("SellerPin", jsonObject.optString("SellerPin"));
                                    DataHashMap.put("ShipmentType", jsonObject.optString("ShipmentType"));
                                    DataHashMap.put("PTPDate", jsonObject.optString("PTPDate"));
                                    DataHashMap.put("Remarks", jsonObject.optString("Remarks"));

                                    DataHashMap.put("filter", filter);
                                    DataHashMap.put("Lead_Id", jsonObject.optString("Lead_Id"));
                                    DataHashMap.put("IsCurrentDayShipment", jsonObject.optString("IsCurrentDayShipment"));
                                    DataHashMap.put("RequestPickupTime", jsonObject.optString("RequestPickupTime"));
                                    DataHashMap.put("RequestPickupDT", jsonObject.optString("RequestPickupDT"));
                                    DataHashMap.put("PickupType", jsonObject.optString("PickupType"));

                                    DataHashMap.put("SAName", jsonObject.optString("SAName"));
                                    DataHashMap.put("SABranchName", jsonObject.optString("SABranchName"));
                                    DataHashMap.put("NACH", jsonObject.getString("NACH"));
                                    DataHashMap.put("PenaltyCharges", jsonObject.getString("PenaltyCharges"));
                                    DataHashMap.put("PaymentMode", jsonObject.getString("PaymentMode"));
                                    DataHashMap.put("CodAmount", jsonObject.getString("CodAmount"));

                                    DataHashMap.put("OSBounce", jsonObject.getString("OSBounce"));
                                    DataHashMap.put("OSCharges", jsonObject.getString("OSCharges"));
                                    DataHashMap.put("OSPenalty", jsonObject.getString("OSPenalty"));
                                    DataHashMap.put("ForeclosureValue", jsonObject.getString("ForeclosureValue"));

                                    //credit
                                    DataHashMap.put("ClientOrderNumber", jsonObject.getString("ClientOrderNumber"));
                                    DataHashMap.put("InvoiceRefNo", jsonObject.getString("InvoiceRefNo"));
                                    DataHashMap.put("BalanceAmount", jsonObject.getString("BalanceAmount"));
                                    DataHashMap.put("TotalBillAmount", jsonObject.getString("TotalBillAmount"));
                                    DataHashMap.put("PaymentDueDate", jsonObject.getString("Cycle"));

                                    DataHashMap.put("WaybillNumber", jsonObject.getString("WaybillNumber"));
                                    DataHashMap.put("PickupZone", jsonObject.getString("PickupZone"));
                                    DataHashMap.put("DeliveryZone", jsonObject.getString("DeliveryZone"));
                                    DataHashMap.put("TotalQty", jsonObject.getString("TotalQty"));
                                    DataHashMap.put("InvoiceRefNo", jsonObject.getString("InvoiceRefNo"));
                                    DataHashMap.put("AppointmentId", jsonObject.getString("AppointmentId"));
                                    DataHashMap.put("OrderNumber", jsonObject.getString("OrderNumber"));
                                    DataHashMap.put("ButtonStatus", jsonObject.getString("ButtonStatus"));
                                    ListCollection.add(DataHashMap);

                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        searchResults = ListCollection;
                                        adapter = new PTPOnlineAdapter(getActivity(), ListCollection);
                                        listView.setAdapter(adapter);

                                    }
                                });
                            }

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

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Kotak PTPOnline");
        loadpickuplist();

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.start_stop);
        item.setVisible(true);
    }

}
