package com.twilio.authenticatorsample.apps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.external.App;
import com.twilio.authenticator.external.MultiAppListener;
import com.twilio.authenticatorsample.R;
import com.twilio.authenticatorsample.SampleApp;
import com.twilio.authenticatorsample.appdetail.AppsAdapter;
import com.twilio.authenticatorsample.registration.RegistrationActivity;
import com.twilio.authenticatorsample.ui.ClearDataConfirmationDialog;
import com.twilio.authenticatorsample.ui.ShowIdsDialog;
import com.twilio.authenticatorsample.utils.MessageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvidal on 10/12/17.
 */

public class AppsActivity extends AppCompatActivity implements AppsAdapter.OnClickListener, MultiAppListener, ClearDataConfirmationDialog.OnClearDataConfirmationListener {

    public static final String EXTRA_APP_ID = "APP_ID";
    static final String EXTRA_APP_NAME = "APP_NAME";

    private RecyclerView recyclerView;
    private MessageHelper messageHelper;
    private TwilioAuthenticator twilioAuthenticator;
    private AppsAdapter appsAdapter;
    private Toolbar toolbar;

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

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        appsAdapter = new AppsAdapter(new ArrayList<App>(), this);
        recyclerView.setAdapter(appsAdapter);

    }

    private void initVars() {
        twilioAuthenticator = ((SampleApp) getApplicationContext()).getTwilioAuthenticator();
        messageHelper = new MessageHelper();
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
        intent.putExtra(EXTRA_APP_NAME, app.getName());

        startActivity(intent);
    }

    // Authenticator Observer
    @Override
    protected void onResume() {
        super.onResume();
        twilioAuthenticator.setMultiAppListener(this);
    }

    @Override
    protected void onStop() {
        messageHelper.dismiss();
        super.onStop();
    }

    // Authenticator Observer
    @Override
    public void onError(Exception exception) {
        Snackbar snackbar = messageHelper.show(recyclerView, exception.getMessage());
    }

    @Override
    public void onAppAdded(final List<App> apps) {
        appsAdapter.addApps(apps);
    }

    @Override
    public void onAppUpdated(final List<App> apps) {
        appsAdapter.updateApps(apps);
    }

    @Override
    public void onAppDeleted(final List<Long> appIds) {
        appsAdapter.removeApps(appIds);

    }

    @Override
    public void onNewCode(final List<App> apps) {
        appsAdapter.setApps(apps);
    }

    @Override
    public void onClearDataRequested() {
        twilioAuthenticator.clearLocalData();
        RegistrationActivity.startRegistrationActivity(this, R.string.registration_error_device_deleted);
    }
}

