package com.kotakcollection.Printer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class MiddleWare {

    //https://github.com/diegoveloper/quickprinter-integration
    //https://play.google.com/store/apps/details?id=pe.diegoveloper.printerserverapp
    //https://github.com/iYaroslav/esc-pos-android

    public static BluetoothSocket mBTSocket;
    public static BluetoothDevice PrnBdevice;
    public static final boolean DEBUG = false;
    public boolean printerOpen(Context cx) {
        return ESCPrinter_init(cx, 1);
    }

    public boolean ESCPrinter_init(Context _context, int flag) {
        try {

            try {
                if (MiddleWare.mBTSocket != null)

                    mBTSocket.close();

            } catch (Exception ex) {
            }
            UUID dvcUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            try {
                BluetoothSocket tmp = null;
                tmp = PrnBdevice.createRfcommSocketToServiceRecord(dvcUUID);
                //for avoid null pointer exception
                //Method m = MiddleWare.PrnBdevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
                //tmp = (BluetoothSocket) m.invoke(MiddleWare.PrnBdevice, 1);
                mBTSocket = tmp;
            } catch (IOException e) {
                if (DEBUG)
                    Log.d("MiddleWare", "I ESCPrinter_init: " + e.toString());
                if (flag == 1)
                    //  showAlertMsg(_context, "Error...", _context.getString(R.string.prn_status8));
                    Toast.makeText(_context, "Create SocketToService Fail" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            try {
                mBTSocket.connect();
                return true;
            } catch (IOException e) {
                if (DEBUG)
                    Log.d("MiddleWare", "ESCPrinter_init: " + e.toString());
                if (flag == 1)
                    //   showAlertMsg(_context, "Error...", _context.getString(R.string.prn_status8));
                    Toast.makeText(_context, "connect SocketToService Fail" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {

            if (DEBUG)
                Log.d("MiddleWare", "ex ESCPrinter_init: " + ex.toString());
            if (flag == 1)
                //  showAlertMsg(_context, "Error...", _context.getString(R.string.prn_status8));
                Toast.makeText(_context, ex.toString(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
