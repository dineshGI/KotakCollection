package com.kotakcollection.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kotakcollection.Printer.PlayTicket;
import com.kotakcollection.R;

import org.json.JSONException;
import org.json.JSONObject;


public class AlertActivity extends AppCompatActivity {
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        alertDialogBuilder.setCancelable(false);

        String status = getIntent().getStringExtra("status");
        String StatusDesc = getIntent().getStringExtra("StatusDesc");
        String ReceiptNo = "Receipt No : "+getIntent().getStringExtra("ReceiptNo");
        String ReceiptDate = "Receipt Date : "+getIntent().getStringExtra("ReceiptDate");
        String PaymentType = "Payment Mode : "+getIntent().getStringExtra("PaymentType");
        String CollectedAmount = "Amount Received : "+getIntent().getStringExtra("CollectedAmount");
        String CustomerName = "Customer Name : "+getIntent().getStringExtra("CustomerName");
        String AccountNo = "Account No : "+getIntent().getStringExtra("AccountNo");
        String CustomerMobileNo = "Mobile No : "+getIntent().getStringExtra("CustomerMobileNo");
        String FEName = "FE Name : "+getIntent().getStringExtra("FEName");

        final JSONObject data=new JSONObject();
        try {
            data.put("ReceiptNo",ReceiptNo+"\n");
            data.put("ReceiptDate",ReceiptDate+"\n");
            data.put("PaymentType",PaymentType+"\n");
            data.put("CollectedAmount",CollectedAmount+"\n");
            data.put("CustomerName",CustomerName+"\n");
            data.put("AccountNo",AccountNo+"\n");
            data.put("CustomerMobileNo",CustomerMobileNo+"\n");
            data.put("FEName",FEName+"\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (status.equalsIgnoreCase("0")) {
            alertDialogBuilder.setMessage(StatusDesc);
            alertDialogBuilder.setPositiveButton("Print",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            PlayTicket ticket = new PlayTicket();
                            ticket.PrintReciept(AlertActivity.this,data);
                            finish();
                        }
                    });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            alertDialogBuilder.setMessage(StatusDesc);
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
    }
}
