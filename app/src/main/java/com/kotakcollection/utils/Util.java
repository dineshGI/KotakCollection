package com.kotakcollection.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.MODE_PRIVATE;

public class Util {

    public static String[] url_name = {"S", "L"};
    public static String app_version = "1.0.0";
    public static String directoryname = "Kotak Collection";
    //http://220.225.104.144/KotakAPI
    public static String[] urlarray = {"http://220.225.104.144/KotakAPI/", "http://14.141.212.203/gicashcollectionapi/"};
    //public static String[] urlarray = {"http://14.141.212.203/CashCollectionAPI/", "http://14.141.212.203/gicashcollectionapi/", "http://smartcargos.in/gicashcollectionapi/"};
    public static int url_type = 0;
    public static String MOBILE_API = String.valueOf(urlarray[url_type]);
    public static String app_version_name = "V " + app_version + " " + url_name[url_type];

    //LOGIN
    public static String LOGIN = MOBILE_API + "RoadRunners/SALogin";

    //9899914217
    //581470

    // TANVEER
    //9944260624
    //581470

    //PICK UP
    public static String PICKUP = MOBILE_API + "RoadRunners/GetIndostarShipment";
    public static String GET_PAYMENT = MOBILE_API + "RoadRunners/GetPaymentMode";
    public static String COLLECT = MOBILE_API + "RoadRunners/CreditCardUpdate_Web";
    //public static String RADIOBUTTON = MOBILE_API + "RoadRunners/PickupFailedReason";
    public static String RADIOBUTTON = MOBILE_API + "RoadRunners/PickupFailedReasonIndoStar";
    public static String DISPUTE_MGNT_RADIO = MOBILE_API + "Dashboard/GetStatusUpdate";
    public static String RADIOBUTTON_UPDATE = MOBILE_API + "RoadRunners/RoadRunnerPickupFailed_Web";
    public static String WORK_STATUS = MOBILE_API + "Warehouse/AddSAUsersWorkStatus";
    public static String MOBILE_TRACK = MOBILE_API + "Settings/MobileTrack";
    public static String INVOICE = MOBILE_API + "RoadRunners/GetProductList";
    public static String UPLOAD = MOBILE_API + "RoadRunners/FileUpload";
    public static String SCAN_BULKUPLOAD = MOBILE_API + "RoadRunners/AddPickUpBulk";
    public static String BANK_PICKUP = MOBILE_API + "RoadRunners/CreditCardUpdate";
    public static String FAQ = MOBILE_API + "faq.html";
    // public static String FAQ = "http://14.141.212.203/gicashcollection/account/faq";

    public static String CHANGEPASSWORD = MOBILE_API + "RoadRunners/RRChangePassword";
    public static String FORGETPASSWORD = MOBILE_API + "RoadRunners/UpdateSAForgetPasswordReset";

    private SharedPreferences preferences;
    public static String getstatus;

    public Util(Context appContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public synchronized static boolean isFirstLaunch(Context context) {
        boolean launchFlag = false;
        SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("install", false);
        return launchFlag;
    }

    public static final class Operations {
        private Operations() throws InstantiationException {
            throw new InstantiationException("This class is not for instantiation");
        }

        public static boolean isOnline(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }

    public static void hideKeypad(Context context, View view) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void saveData(String key, String value, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("INDOSTAR", Activity.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getData(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("INDOSTAR", Activity.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void clearSession(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("INDOSTAR", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static String getDeviceId(Context ctx) {
        String android_id = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static boolean isOnline(Context con) {
        ConnectivityManager cm = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.i("netInfo", "" + netInfo);
            return true;
        }
        return false;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    @SuppressLint("NewApi")
    public static String EncryptURL(String data) {

        String strret = "";
        try {
            byte[] sharedkey = "A1234&ABCDE/98745#000078".getBytes();
            byte[] sharedvector = {8, 7, 5, 6, 4, 1, 2, 3, 18, 17, 15, 16, 14, 11, 12, 13};
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sharedkey, "AES"), new IvParameterSpec(sharedvector));
            byte[] encrypted = c.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // strret = Base64.getEncoder().encodeToString(encrypted);
            strret = Base64.encodeToString(encrypted, Base64.DEFAULT);
            strret = strret.replace("\n", "");
            strret = URLEncoder.encode(strret, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Util.Logcat.e("Encrypted Data:" + strret);
        return strret;
    }

    public static String Decrypt(String data) {

        String decrypt = "";
        byte[] sharedkey = "A1234&ABCDE/98745#000078".getBytes();
        byte[] sharedvector = {8, 7, 5, 6, 4, 1, 2, 3, 18, 17, 15, 16, 14, 11, 12, 13};

        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sharedkey, "AES"), new IvParameterSpec(sharedvector));
            byte[] decrypted = c.doFinal(Base64.decode((URLDecoder.decode(data, "UTF-8")), Base64.DEFAULT));
            decrypt = new String(decrypted, StandardCharsets.UTF_8);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        System.out.print("DECRYPT" + decrypt);
        return decrypt;

    }

    public static String getdatetime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
        String datetime = dateformat.format(c.getTime());
        return datetime.replace("-", "/");
    }

    public static String getdateonly() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        String datetime = dateformat.format(c.getTime());
        return datetime.replace("-", "/");
    }

    public static class Logcat {
        private static final String TAG = "KOTAK";

        public static void e(String msg) {
            if (url_type == 0) {
                Log.e(TAG, msg);
                //Util.Logcat.e();
            }
        }
    }
}

