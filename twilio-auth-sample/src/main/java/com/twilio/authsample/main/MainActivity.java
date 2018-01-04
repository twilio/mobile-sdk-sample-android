package com.twilio.authsample.main;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authsample.App;
import com.twilio.authsample.R;
import com.twilio.authsample.approvalrequests.RequestsFragment;
import com.twilio.authsample.registration.RegistrationActivity;
import com.twilio.authsample.ui.ClearDataConfirmationDialog;
import com.twilio.authsample.ui.ShowIdsDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClearDataConfirmationDialog.OnClearDataConfirmationListener {

    private TwilioAuthenticator twilioAuthenticator;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListeners();
        initVars();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_requests) {
            RequestsFragment requestsFragment = RequestsFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, requestsFragment)
                    .commit();
            getSupportActionBar().setTitle(R.string.menu_navigation_requests);
        } else if (id == R.id.nav_tokens) {
            TokensFragment tokensFragment = TokensFragment.newInstance(twilioAuthenticator);
            tokensFragment.setHasOptionsMenu(true);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, tokensFragment)
                    .commit();
            getSupportActionBar().setTitle(R.string.menu_navigation_tokens);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClearDataRequested() {
        twilioAuthenticator.clearLocalData();
        RegistrationActivity.startRegistrationActivity(this, R.string.registration_error_device_deleted);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    private void initListeners() {
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initVars() {
        twilioAuthenticator = ((App) getApplicationContext()).getTwilioAuthenticator();

        // Select the first item by default
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }
}
