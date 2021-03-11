package com.kotakcollection.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.kotakcollection.R;
import com.kotakcollection.activity.NotCollect;
import com.kotakcollection.activity.Collect;
import com.kotakcollection.utils.CommonAlertDialog;
import com.kotakcollection.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PickupAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    private Activity activity;
    ProgressDialog progressDialog;
    List<Map<String, String>> originaldata;
    List<Map<String, String>> filterData;
    CommonAlertDialog alert;

    public PickupAdapter(Activity context, List<Map<String, String>> listCollectionone) {

        activity = context;
        originaldata = listCollectionone;
        filterData = listCollectionone;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        alert = new CommonAlertDialog(activity);
    }

    @Override
    public int getCount() {
        return filterData.size();
    }

    @Override
    public Object getItem(int position) {
        return filterData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void pos(int position) {
        filterData.remove(filterData.get(position));
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (vi == null) {

            vi = inflater.inflate(R.layout.pickup_adapter, null);
            holder = new ViewHolder();

            holder.TxtshipmentID = vi
                    .findViewById(R.id.shipmentid);
            holder.TxtCreatedDate = vi
                    .findViewById(R.id.crtddate);

            holder.arrow = vi
                    .findViewById(R.id.arrow);
            holder.logo = vi
                    .findViewById(R.id.company_logo);
            holder.lytop = vi
                    .findViewById(R.id.layouttop);
            holder.lybottom = vi
                    .findViewById(R.id.layoutbottom);
            holder.lycredit = vi
                    .findViewById(R.id.layoutcredit);
            holder.lyButton = vi
                    .findViewById(R.id.hide_layout);

            holder.rootlayout = vi
                    .findViewById(R.id.root);
            holder.line = vi
                    .findViewById(R.id.removeline);

            //Bank layout
            holder.NameC = vi
                    .findViewById(R.id.c_name);
            holder.MobileNoC = vi
                    .findViewById(R.id.c_mobileno);
            holder.AddressC = vi
                    .findViewById(R.id.c_address);
            holder.BtnCollectC = vi
                    .findViewById(R.id.c_collect);
            holder.BtnNotCollectC = vi
                    .findViewById(R.id.c_notcollect);
            holder.MobileCallCredit = vi
                    .findViewById(R.id.ly_c_mobileno);

            holder.AmountTobeCollectedC = vi
                    .findViewById(R.id.amt_tobecollect);
            holder.DueDateC = vi
                    .findViewById(R.id.c_paymentduedate);
            holder.DateC = vi
                    .findViewById(R.id.c_date);
            holder.TimeC = vi
                    .findViewById(R.id.c_time);
            holder.LyReshedule = vi
                    .findViewById(R.id.ly_reshedule);
            holder.ResheduleC = vi
                    .findViewById(R.id.c_reshedule);
            holder.TxtRemarks = vi
                    .findViewById(R.id.txt_remarks);
            holder.TxtPTPDate = vi
                    .findViewById(R.id.ptp_date);
            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (filterData.get(position).get(
                "filter").equalsIgnoreCase("0")) {
            holder.lyButton.setVisibility(View.VISIBLE);
        } else {
            holder.lyButton.setVisibility(View.GONE);
        }

        holder.TxtshipmentID.setText(filterData.get(position).get(
                "ShipmentId") + " - " + filterData.get(position).get(
                "ClientName"));
        holder.lycredit.setVisibility(View.VISIBLE);

        holder.NameC.setText(filterData.get(position).get(
                "SellerName"));

        holder.MobileNoC.setText(filterData.get(position).get(
                "SellerContactNo"));
        holder.AddressC.setText(filterData.get(position).get(
                "SellerAddress"));

        holder.AmountTobeCollectedC.setText("Amount Due :" + filterData.get(position).get(
                "CodAmount"));
        holder.DueDateC.setText("Due Date :" + filterData.get(position).get(
                "PaymentDueDate"));
        holder.DateC.setText("Lead Date:" + filterData.get(position).get(
                "CreatedDT"));
        if (!filterData.get(position).get(
                "Remarks").isEmpty()) {
            holder.TxtRemarks.setVisibility(View.VISIBLE);
            holder.TxtRemarks.setText("Remarks :" + filterData.get(position).get(
                    "Remarks"));
        }

        if (!filterData.get(position).get(
                "PTPDate").isEmpty()) {
            holder.TxtPTPDate.setVisibility(View.VISIBLE);
            holder.TxtPTPDate.setText("PTP Date :" + filterData.get(position).get(
                    "PTPDate"));
        }
        holder.TimeC.setVisibility(View.GONE);
        holder.ResheduleC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((Util.getData("WorkStatus", activity.getApplicationContext()).equalsIgnoreCase("1"))) {
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Util.saveData("Lead_Id", filterData.get(position).get(
                            "Lead_Id"), activity.getApplicationContext());
                    Util.saveData("SAName", filterData.get(position).get(
                            "SAName"), activity.getApplicationContext());
                    Util.saveData("SABranchName", filterData.get(position).get(
                            "SABranchName"), activity.getApplicationContext());
                    Util.saveData("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"), activity.getApplicationContext());
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Intent i = new Intent(activity, NotCollect.class);
                    i.putExtra("AppointmentId", filterData.get(position).get(
                            "AppointmentId"));
                    i.putExtra("WaybillNumber", filterData.get(position).get(
                            "WaybillNumber"));
                    i.putExtra("amount", filterData.get(position).get(
                            "CodAmount"));
                    i.putExtra("from", "collect");
                    activity.startActivity(i);
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(activity.getString(R.string.start_msg));
                }

            }
        });

        //credit
        holder.BtnCollectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((Util.getData("WorkStatus", activity.getApplicationContext()).equalsIgnoreCase("1"))) {
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Util.saveData("Lead_Id", filterData.get(position).get(
                            "Lead_Id"), activity.getApplicationContext());
                    Util.saveData("SAName", filterData.get(position).get(
                            "SAName"), activity.getApplicationContext());
                    Util.saveData("SABranchName", filterData.get(position).get(
                            "SABranchName"), activity.getApplicationContext());
                    Util.saveData("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"), activity.getApplicationContext());
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());

                    Intent i = new Intent(activity, Collect.class);
                    i.putExtra("AppointmentId", filterData.get(position).get(
                            "AppointmentId"));
                    i.putExtra("WaybillNumber", filterData.get(position).get(
                            "WaybillNumber"));
                    i.putExtra("amount", filterData.get(position).get(
                            "CodAmount"));
                    i.putExtra("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"));
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    i.putExtra("from", "collect");
                    activity.startActivity(i);
                } else {
                    alert.build(activity.getString(R.string.start_msg));
                }

            }
        });

        holder.BtnNotCollectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Util.getData("WorkStatus", activity.getApplicationContext()).equalsIgnoreCase("1"))) {
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Util.saveData("Lead_Id", filterData.get(position).get(
                            "Lead_Id"), activity.getApplicationContext());
                    Util.saveData("SAName", filterData.get(position).get(
                            "SAName"), activity.getApplicationContext());
                    Util.saveData("SABranchName", filterData.get(position).get(
                            "SABranchName"), activity.getApplicationContext());
                    Util.saveData("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"), activity.getApplicationContext());
                    Intent i = new Intent(activity, NotCollect.class);
                    i.putExtra("AppointmentId", filterData.get(position).get(
                            "AppointmentId"));
                    i.putExtra("WaybillNumber", filterData.get(position).get(
                            "WaybillNumber"));
                    i.putExtra("amount", filterData.get(position).get(
                            "CodAmount"));
                    i.putExtra("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"));
                    i.putExtra("from", "notcollect");
                    activity.startActivity(i);
                } else {
                    alert.build(activity.getString(R.string.start_msg));
                }

            }
        });

        holder.MobileCallCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tel", holder.MobileNoC.getText().toString());
                if (!holder.MobileNoC.getText().toString().isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + holder.MobileNoC.getText().toString()));
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    activity.startActivity(callIntent);
                } else {
                    alert.build(activity.getString(R.string.mobileno_notfound));
                }
            }
        });

        holder.TxtCreatedDate.setText("Pickup Date:" + " " + filterData.get(position).get(
                "RequestPickupDT") + " " + filterData.get(position).get(
                "RequestPickupTime"));
        holder.arrow.setTag(position);

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View vas) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (holder.lybottom.getVisibility() == View.VISIBLE) {
                            holder.lybottom.setVisibility(View.GONE);
                            holder.arrow.setImageResource(R.drawable.down);
                            holder.rootlayout.setBackgroundResource(0);
                            holder.lytop.setBackgroundResource(0);
                            holder.line.setVisibility(View.VISIBLE);
                        } else {
                            holder.lybottom.setVisibility(View.VISIBLE);
                            holder.arrow.setImageResource(R.drawable.up);
                             holder.rootlayout.setBackgroundResource(R.drawable.editext);
                            holder.lytop.setBackgroundResource(R.color.collapse_header);
                            holder.line.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        return vi;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = originaldata;
                    results.count = originaldata.size();
                } else {

                    List<Map<String, String>> filterResultsData = new ArrayList<>();
                    for (Map<String, String> data : originaldata) {
                        if (data.get("ShipmentId").contains(constraint) || data.get("PickupType").contains(constraint) || data.get("SellerName").contains(constraint) || data.get("SellerContactNo").contains(constraint) || data.get("ClientOrderNumber").contains(constraint)) {
                            filterResultsData.add(data);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterData = (List<Map<String, String>>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder {
        private TextView TxtshipmentID, TxtCreatedDate;
        private LinearLayout lytop, lybottom, rootlayout;
        private LinearLayout lycredit, MobileCallCredit, lyButton, LyReshedule;
        ImageView arrow, logo;
        View line;
        private TextView BtnCollectC, BtnNotCollectC, NameC, AddressC, MobileNoC;
        private TextView  AmountTobeCollectedC, DueDateC, DateC, TimeC, ResheduleC, TxtRemarks, TxtPTPDate;
    }
}

