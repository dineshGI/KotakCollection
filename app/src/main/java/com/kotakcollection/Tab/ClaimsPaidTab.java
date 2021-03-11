package com.kotakcollection.Tab;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;

import com.kotakcollection.Module.ClaimsPaid;
import com.kotakcollection.R;


public class ClaimsPaidTab extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FragmentTabHost mTabHost;

    ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public ClaimsPaidTab() {
        // Required empty public constructor
    }

    public static ClaimsPaidTab newInstance(String param1, String param2) {
        ClaimsPaidTab fragment = new ClaimsPaidTab();
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

        View rootView = inflater.inflate(R.layout.tab_layout, container, false);
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
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
        mTabHost = rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        Bundle arg1 = new Bundle();
        arg1.putString("Title", "New");
        mTabHost.addTab(mTabHost.newTabSpec("New").setIndicator("New"),
                ClaimsPaid.class, arg1);

        Bundle arg2 = new Bundle();
        arg2.putString("Title", "Completed");
        mTabHost.addTab(mTabHost.newTabSpec("Collected").setIndicator("Collected"),
                ClaimsPaid.class, arg2);

        Bundle arg3 = new Bundle();
        arg3.putString("Title", "NotCompleted");
        mTabHost.addTab(mTabHost.newTabSpec("Not Collected").setIndicator("Not Collected"),
                ClaimsPaid.class, arg3);

        TextView one = mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        one.setTextSize(12);
        one.setAllCaps(false);
        one.setTextColor(getResources().getColor(R.color.white));

        TextView two =mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        two.setTextSize(12);
        two.setAllCaps(false);
        two.setTextColor(getResources().getColor(R.color.white));

        TextView three =mTabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        three.setTextSize(12);
        three.setAllCaps(false);
        three.setTextColor(getResources().getColor(R.color.white));
        return rootView;

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
    public void onResume() {
        super.onResume();
      //  ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Kotak- Chennai - CC");
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
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.start_stop);
        item.setVisible(true);

    }
}