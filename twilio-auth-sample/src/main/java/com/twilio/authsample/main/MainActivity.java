package com.twilio.authsample.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.twilio.authsample.R;
import com.twilio.authsample.approvalrequests.RequestsFragment;
import com.twilio.authsample.totp.TokensFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
    }

    private void initViews() {
        bottomNavigationTabs = (BottomNavigationView)
                findViewById(R.id.bottomNavigationTabs);
    }

    private void initListeners() {
        bottomNavigationTabs.setOnNavigationItemSelectedListener(this);
        bottomNavigationTabs.setSelectedItemId(R.id.menu_requests);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tokens:
                TokensFragment tokensFragment = TokensFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, tokensFragment)
                        .commit();
                getSupportActionBar().setTitle(R.string.menu_bottom_navigation_tokens);
                return true;
            case R.id.menu_requests:
                RequestsFragment requestsFragment = RequestsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, requestsFragment)
                        .commit();
                getSupportActionBar().setTitle(R.string.menu_bottom_navigation_requests);
                return true;
        }
        return false;
    }
}
