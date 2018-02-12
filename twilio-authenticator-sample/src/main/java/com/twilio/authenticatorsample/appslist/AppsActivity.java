package com.twilio.authenticatorsample.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.external.App;
import com.twilio.authenticator.external.AuthenticatorObserver;
import com.twilio.authenticatorsample.R;
import com.twilio.authenticatorsample.SampleApp;
import com.twilio.authenticatorsample.totp.AppsAdapter;
import com.twilio.authenticatorsample.ui.ClearDataConfirmationDialog;
import com.twilio.authenticatorsample.ui.ShowIdsDialog;

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

    public static AppsActivity newInstance(TwilioAuthenticator twilioAuthenticator) {
        AppsActivity appsActivity = new AppsActivity();
        appsActivity.twilioAuthenticator = twilioAuthenticator;
        return appsActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        initViews();
        initVars();
    }

    private void initViews() {
        this.recyclerView = (RecyclerView) findViewById(R.id.tokens_list);
        this.recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        appsAdapter = new AppsAdapter(new ArrayList<App>(), this);
        recyclerView.setAdapter(appsAdapter);

    }

    private void initVars() {
        twilioAuthenticator = ((SampleApp) getApplicationContext()).getTwilioAuthenticator();
    }

    // Menu Options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear) {
            ClearDataConfirmationDialog clearDataConfirmationDialog = new ClearDataConfirmationDialog();
            clearDataConfirmationDialog.show(getSupportFragmentManager(), ClearDataConfirmationDialog.class.getSimpleName());
            return true;
        } else if (item.getItemId() == R.id.menu_ids) {
            ShowIdsDialog showIdsDialog = ShowIdsDialog.create(twilioAuthenticator.getAuthyId(), twilioAuthenticator.getDeviceId());
            showIdsDialog.show(getSupportFragmentManager(), ShowIdsDialog.class.getSimpleName());
        }
        return super.onOptionsItemSelected(item);
    }

    // App Selected
    @Override
    public void onAppClicked(App app) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_APP_ID, app.getId());
        intent.putExtra(Intent.EXTRA_TITLE, app.getName());

        startActivity(intent);
    }

    // Authenticator Observer
    @Override
    public void onStart() {
        super.onStart();
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

