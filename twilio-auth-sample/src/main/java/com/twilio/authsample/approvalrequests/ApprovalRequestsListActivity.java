package com.twilio.authsample.approvalrequests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.authy.commonandroid.external.TwilioException;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.twilio.auth.TwilioAuth;
import com.twilio.auth.external.ApprovalRequest;
import com.twilio.auth.external.ApprovalRequestStatus;
import com.twilio.auth.external.ApprovalRequests;
import com.twilio.auth.external.TOTPCallback;
import com.twilio.authsample.App;
import com.twilio.authsample.R;
import com.twilio.authsample.approvalrequests.adapters.ApprovalRequestsAdapter;
import com.twilio.authsample.approvalrequests.detail.ApprovalRequestDetailActivity;
import com.twilio.authsample.approvalrequests.events.ApprovalRequestsUpdatedEvent;
import com.twilio.authsample.approvalrequests.events.RefreshApprovalRequestsEvent;
import com.twilio.authsample.registration.RegistrationActivity;
import com.twilio.authsample.ui.ClearDataConfirmationDialog;
import com.twilio.authsample.utils.AuthyActivityListener;
import com.twilio.authsample.utils.AuthyTask;
import com.twilio.authsample.utils.MessageHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApprovalRequestsListActivity extends AppCompatActivity implements ApprovalRequestsListFragment.ApprovalRequestsSource,
        ApprovalRequestsAdapter.ApprovalRequestSelectedListener, AuthyActivityListener<ApprovalRequests>,
        ClearDataConfirmationDialog.OnClearDataConfirmationListener,
        TOTPCallback {

    private final static int TOTP_UPDATE_INTERVAL_MILLIS = 20000;
    Handler handler = new Handler();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter sectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;
    private TextView totpTextView;

    /**
     * List of approval requests to display
     */
    private ApprovalRequests approvalRequests;

    private TwilioAuth twilioAuth;
    private Bus bus;
    private TabLayout tabLayout;
    private MessageHelper messageHelper;

    Runnable updateTOTPRunnable = new Runnable() {
        public void run() {
            updateTOTP();
            handler.postDelayed(this, TOTP_UPDATE_INTERVAL_MILLIS);
        }
    };

    /**
     * Creates an intent to launch the ApprovalRequestsListActivity
     *
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ApprovalRequestsListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_requests_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        totpTextView = (TextView) findViewById(R.id.totp);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(ContextCompat.getColor(this, android.R.color.white), ContextCompat.getColor(this, R.color.colorAccent));

        twilioAuth = ((App) getApplicationContext()).getTwilioAuth();

        bus = ((App) getApplicationContext()).getBus();

        messageHelper = new MessageHelper();
    }

    private void startTOTPGeneration() {
        updateTOTP();

        handler.postDelayed(updateTOTPRunnable,
                TOTP_UPDATE_INTERVAL_MILLIS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
        viewPager.addOnPageChangeListener(sectionsPagerAdapter);
        fetchApprovalRequests();


        startTOTPGeneration();
    }

    @Override
    public void onTOTPError(Exception exception) {
        Log.e(ApprovalRequestsListActivity.class.getSimpleName(), "Error while generating TOTP", exception);
        messageHelper.show(viewPager, exception.getMessage());
    }

    @Override
    public void onTOTPReceived(String totp) {
        totpTextView.setText("TOTP = " + totp);
    }

    private void updateTOTP() {
        twilioAuth.getTOTP(this);
    }

    private void fetchApprovalRequests() {
        final List<ApprovalRequestStatus> statuses = Arrays.asList(ApprovalRequestStatus.approved, ApprovalRequestStatus.denied, ApprovalRequestStatus.expired, ApprovalRequestStatus.pending);

        new AuthyTask<ApprovalRequests>(this) {

            @Override
            public ApprovalRequests executeOnBackground() {
                return twilioAuth.getApprovalRequests(statuses, null);
            }
        }.execute();
    }

    @Override
    protected void onStop() {
        viewPager.removeOnPageChangeListener(sectionsPagerAdapter);
        messageHelper.dismiss();
        bus.unregister(this);
        handler.removeCallbacks(updateTOTPRunnable);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_approval_requests_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            updateTOTP();
            fetchApprovalRequests();
        } else if (item.getItemId() == R.id.menu_clear) {
            ClearDataConfirmationDialog clearDataConfirmationDialog = new ClearDataConfirmationDialog();
            clearDataConfirmationDialog.show(getSupportFragmentManager(), ClearDataConfirmationDialog.class.getSimpleName());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ApprovalRequestsUpdateInfo getApprovalRequestsUpdateInfo(int position) {
        List<ApprovalRequestStatus> statusList = Collections.emptyList();
        boolean hideStatusInfo = true;
        switch (position) {
            case ApprovalRequestsListActivity.SectionsPagerAdapter.PENDING_PAGE_POSITION:
                statusList = Arrays.asList(ApprovalRequestStatus.pending);
                hideStatusInfo = false;
                break;
            case ApprovalRequestsListActivity.SectionsPagerAdapter.ARCHIVE_PAGE_POSITION:
                statusList = Arrays.asList(ApprovalRequestStatus.approved, ApprovalRequestStatus.denied, ApprovalRequestStatus.expired);
                hideStatusInfo = false;
                break;
        }
        return new ApprovalRequestsUpdateInfo(getApprovalRequests(), statusList, hideStatusInfo);
    }

    @Override
    public void startApprovalRequestUpdate() {
        fetchApprovalRequests();
    }

    @Override
    public void onClearDataRequested() {
        twilioAuth.clearLocalData();
        RegistrationActivity.startRegistrationActivity(this, R.string.registration_error_device_deleted);
    }

    public ApprovalRequests getApprovalRequests() {
        if (approvalRequests == null) {
            approvalRequests = new ApprovalRequests();
        }
        return approvalRequests;
    }

    @Override
    public void onApprovalRequestSelected(ApprovalRequest approvalRequest) {
        final Intent intent = ApprovalRequestDetailActivity.createIntent(this, approvalRequest);
        startActivity(intent);
    }

    @Override
    public void onSuccess(ApprovalRequests result) {
        approvalRequests = result;
        if (!isFinishing()) {
            sectionsPagerAdapter.notifyDataSetChanged();
            bus.post(new ApprovalRequestsUpdatedEvent(true));
        }
    }

    @Override
    public void onError(Exception exception) {
        Log.e(ApprovalRequestsListActivity.class.getSimpleName(), "Error while getting approval requests for device", exception);
        String errorMessage = exception instanceof TwilioException ? ((TwilioException) exception).getBody() : getString(R.string.approval_request_fetch_error);
        final Snackbar snackbar = messageHelper.show(viewPager, errorMessage);
        bus.post(new ApprovalRequestsUpdatedEvent(true));

        if (!twilioAuth.isDeviceRegistered()) {
            RegistrationActivity.startRegistrationActivity(this, R.string.registration_error_device_deleted);
            finish();
            return;
        }

        // Create refresh button
        snackbar.setAction(R.string.approval_request_action_refresh, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                // Try to fetch requests again
                fetchApprovalRequests();
            }
        });
    }

    @Subscribe
    public void onRefreshApprovalRequestEvent(RefreshApprovalRequestsEvent refreshApprovalRequestsEvent) {
        fetchApprovalRequests();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public static final int PENDING_PAGE_POSITION = 0;
        public static final int ARCHIVE_PAGE_POSITION = 1;
        public static final int FRAGMENT_COUNT = 2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ApprovalRequestsListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PENDING_PAGE_POSITION:
                    return getString(R.string.fragment_approval_requests_pending_title);
                case ARCHIVE_PAGE_POSITION:
                    return getString(R.string.fragment_approval_requests_archive_title);
            }
            return null;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
