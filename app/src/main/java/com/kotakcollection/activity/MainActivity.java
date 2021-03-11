package com.kotakcollection.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.kotakcollection.Http.CallApi;
import com.kotakcollection.Module.ChangePassword;
import com.kotakcollection.Module.ClaimsPaid;
import com.kotakcollection.Module.UploadFragment;
import com.kotakcollection.R;
import com.kotakcollection.Tab.ClaimsPaidTab;
import com.kotakcollection.Tab.DisputeMgntTab;
import com.kotakcollection.Tab.NCValidationTab;
import com.kotakcollection.Tab.PTPBranchTab;
import com.kotakcollection.Tab.PTPOnlineTab;
import com.kotakcollection.Tab.PickupTab;
import com.kotakcollection.interfaces.VolleyResponseListener;
import com.kotakcollection.service.Location;
import com.kotakcollection.utils.CommonAlertDialog;
import com.kotakcollection.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    NavigationView navigationView;
    private DrawerLayout drawer;
    public static int navItemIndex = 0;
    // tags used to attach the fragments
    private static final String TAG_PICKUP = "pickup";
    private static final String TAG_PTP_BRANCH = "ptpbranch";
    private static final String TAG_PTPONLINE = "ptponline";
    private static final String TAG_CLAIMS_PAID = "claimspaid";
    private static final String TAG_DISPUTE_MGNT = "disputemgnt";
    private static final String TAG_NC_VALIDATION = "ncvalidation";
    private static final String TAG_UPLOAD = "upload";
    private static final String TAG_CHANGEPASSWORD = "changepassword";
    public static String CURRENT_TAG = TAG_PICKUP;
    private String[] activityTitles;
    private View navHeader;
    private TextView txtName, txtloginid, txtbranch, txttime, txtappname;
    //flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private Menu menu;
    TextView name;
    private GpsTracker gpsTracker;
    //TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createpath();
        //change menu
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);

        String workstatus = Util.getData("WorkStatus", getApplicationContext());
        if (workstatus.equalsIgnoreCase("1")) {
            startService(new Intent(this, Location.class));
        }

        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        gpsTracker = new GpsTracker(this);
        loadNavHeader();
        setUpNavigationView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_PICKUP;
            loadHomeFragment();
        }
    }

    private void createpath() {
        String root = Environment.getExternalStorageDirectory().toString();
        Util.saveData("directory", Util.directoryname, getApplicationContext());
        // the directory where the signature will be saved
        File myDir = new File(root + "/" + Util.getData("directory", getApplicationContext()));
        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.pickup:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_PICKUP;
                        // return homeFragment;
                        break;

                    case R.id.ptp_branch:
                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 1;
                            CURRENT_TAG = TAG_PTP_BRANCH;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        break;

                    case R.id.ptp_online:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 2;
                            CURRENT_TAG = TAG_PTPONLINE;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }

                        break;

                    case R.id.claims_paid:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 3;
                            CURRENT_TAG = TAG_CLAIMS_PAID;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));

                        }
                        break;

                    case R.id.dispute_mgnt:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 4;
                            CURRENT_TAG = TAG_DISPUTE_MGNT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));

                        }
                        break;

                    case R.id.nc_validation:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 5;
                            CURRENT_TAG = TAG_NC_VALIDATION;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        break;
                    case R.id.upload:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 6;
                            CURRENT_TAG = TAG_UPLOAD;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        break;
                    case R.id.change_password:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 7;
                            CURRENT_TAG = TAG_CHANGEPASSWORD;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        break;

                    case R.id.Logout:
                        AlertDialog.Builder alertDialogBuilder;
                        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.alertDialog);
                        alertDialogBuilder.setMessage(R.string.want_to_logout);
                        alertDialogBuilder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        drawer.closeDrawers();
                        return true;

                    default:
                        navItemIndex = 0;

                }
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                loadHomeFragment();
                return true;

            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                //txttime.setText(Util.getdatetime());
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_PICKUP;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // set toolbar title
        setToolbarTitle();
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {

        switch (navItemIndex) {

            case 0:
                // pickup
                PickupTab pickup = new PickupTab();
                return pickup;

            case 1:
                // PTP Branch
                PTPBranchTab ptpbranch = new PTPBranchTab();
                return ptpbranch;

            case 2:
                // PTP Online
                PTPOnlineTab ptponline = new PTPOnlineTab();
                return ptponline;
            case 3:
                // Claims Paid
                ClaimsPaidTab claimspaid = new ClaimsPaidTab();
                return claimspaid;

            case 4:
                // Dispute Mgmt
                DisputeMgntTab disputemgnt = new DisputeMgntTab();
                return disputemgnt;
            case 5:
                // NC Validation
                NCValidationTab ncvalidation = new NCValidationTab();
                return ncvalidation;
            case 6:
                // Upload
                UploadFragment upload = new UploadFragment();
                return upload;
            case 7:
                // change password
                ChangePassword changepassword = new ChangePassword();
                return changepassword;

            default:
                return new ClaimsPaid();

        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void loadNavHeader() {
        // name, website
        txtName = navHeader.findViewById(R.id.username_header);
        txtloginid = navHeader.findViewById(R.id.loginid);
        txtbranch = navHeader.findViewById(R.id.branch);
        txttime = navHeader.findViewById(R.id.showtime);
        txtappname = navHeader.findViewById(R.id.appname);
        //Util.Logcat.e("navusername", Util.getData("LoginId", getApplicationContext()));
        if (Util.getData("UserName", getApplicationContext()) != "") {
            txtName.setText(Util.getData("UserName", getApplicationContext()));
            txtloginid.setText(Util.getData("EmpCode", getApplicationContext()));
            txtbranch.setText(Util.getData("SABranchName", getApplicationContext()));
        }

     /*   // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);*/

        // showing dot next to notifications label
        // navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;

        MenuItem MenuItem = menu.findItem(R.id.start_stop);
        // menu.findItem(R.id.bulk_scan).setVisible(false);
        String workstatus = Util.getData("WorkStatus", getApplicationContext());
        if (workstatus.equals("2")) {
            MenuItem.setTitle("START");
        } else if (workstatus.equals("1")) {
            MenuItem.setTitle("STOP");
        } else {
            MenuItem.setTitle("");
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            // action with ID action_refresh was selected

            case R.id.start_stop:

                if (Util.isOnline(this)) {
                    if (item.getTitle().equals("STOP")) {

                        callapi("2", item.getTitle().toString());

                    } else if (item.getTitle().equals("START")) {

                        callapi("1", item.getTitle().toString());

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }

                break;
           /* case R.id.bulk_scan:
                Intent i = new Intent(this, BarcodeScan.class);
                i.putExtra("frombulkscan", "true");
                startActivity(i);

                break;
            case R.id.delivery:

                Intent collectlist = new Intent(this, CollectListActivity.class);
                startActivity(collectlist);
                break;*/
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callapi(String status, final String title) {
        if (Util.isOnline(getApplicationContext())) {

            try {
                JSONObject obj = new JSONObject();
                obj.put("LoggedStatus", status);

                if (gpsTracker.canGetLocation()) {
                    obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                    obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
                }

                obj.put("SAUserId", Util.getData("UserId", getApplicationContext()));
                Util.Logcat.e("INPUT:::" + obj.toString());
                String data = Util.EncryptURL(obj.toString());

                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);
                CallApi.postResponse(MainActivity.this, params.toString(), Util.WORK_STATUS, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {

                        if (message.contains("TimeoutError")) {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.timeout_error));

                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.server_error));

                        }
                        Util.Logcat.e("onError" + message);
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        Util.Logcat.e("onResponse" + response);
                        try {
                            Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                            JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                            Util.saveData("WorkStatus", resobject.getString("WorkStatus"), getApplicationContext());

                            if (resobject.getString("Status").equalsIgnoreCase("0")) {
                                MenuItem myItem = menu.findItem(R.id.start_stop);
                                if (title.equalsIgnoreCase("START")) {
                                    myItem.setTitle("STOP");
                                    startService(new Intent(getApplicationContext(), Location.class));
                                } else if (title.equalsIgnoreCase("STOP")) {
                                    myItem.setTitle("START");
                                    stopService(new Intent(getApplicationContext(), Location.class));
                                    Location.mTimer.cancel();
                                }
                                CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                                alert.build(resobject.getString("StatusDesc"));

                            } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                                CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
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

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.app_name) + "\n" + getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

}
