package com.twilio.authsample.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authsample.App;
import com.twilio.authsample.R;
import com.twilio.authsample.totp.TokenDetailsFragment;

public class TokenDetailsActivity extends AppCompatActivity {

    static final String EXTRA_APP_ID = "APP_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_details);

        String appId = getIntent().getStringExtra(EXTRA_APP_ID);
        String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);

        TwilioAuthenticator twilioAuthenticator = ((App) getApplicationContext()).getTwilioAuthenticator();

        TokenDetailsFragment tokenDetailsFragment =
                TokenDetailsFragment.newInstance(twilioAuthenticator, appId);

        // Enable the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, tokenDetailsFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
