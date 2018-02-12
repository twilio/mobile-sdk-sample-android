package com.twilio.authenticatorsample.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.external.App;
import com.twilio.authenticator.external.AuthenticatorObserver;
import com.twilio.authenticatorsample.R;
import com.twilio.authenticatorsample.totp.AppsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvidal on 10/12/17.
 */

public class AppsActivity extends AppCompatActivity implements AppsAdapter.OnClickListener, AuthenticatorObserver {

    static final String EXTRA_APP_ID = "APP_ID";

    private RecyclerView recyclerView;
    private TwilioAuthenticator twilioAuthenticator;
    private AppsAdapter appsAdapter;

    public static AppsFragment newInstance(TwilioAuthenticator twilioAuthenticator) {
        AppsFragment appsFragment = new AppsFragment();
        appsFragment.twilioAuthenticator = twilioAuthenticator;
        return appsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_apps);
        initViews();
    }


    private void initViews() {
        this.recyclerView = (RecyclerView) findViewById(R.id.tokens_list);
        this.recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        appsAdapter = new AppsAdapter(new ArrayList<App>(), this);
        recyclerView.setAdapter(appsAdapter);
    }

    @Override
    public void onTokenClicked(App app) {
        Intent intent = new Intent(this, AppDetailActivity.class);
        intent.putExtra(EXTRA_APP_ID, app.getId());
        intent.putExtra(Intent.EXTRA_TITLE, app.getName());

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        twilioAuthenticator.addObserver(this);

    }

    @Override
    public void onPause() {
        twilioAuthenticator.removeObserver(this);
        super.onPause();
    }

    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void onAppAdded(List<App> apps) {

    }

    @Override
    public void onAppUpdated(List<App> apps) {

    }

    @Override
    public void onAppDeleted(List<Long> appIds) {

    }

    @Override
    public void onNewCode(List<App> apps) {
        appsAdapter.setApps(apps);
    }

}

