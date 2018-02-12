package com.twilio.authenticatorsample.approvalrequests;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.authy.commonandroid.external.TwilioException;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.TwilioAuthenticatorTaskCallback;
import com.twilio.authenticator.external.ApprovalRequest;
import com.twilio.authenticator.external.ApprovalRequestStatus;
import com.twilio.authenticator.external.ApprovalRequests;
import com.twilio.authenticatorsample.R;
import com.twilio.authenticatorsample.SampleApp;
import com.twilio.authenticatorsample.approvalrequests.adapters.ApprovalRequestsAdapter;
import com.twilio.authenticatorsample.approvalrequests.detail.ApprovalRequestDetailActivity;
import com.twilio.authenticatorsample.approvalrequests.events.ApprovalRequestsUpdatedEvent;
import com.twilio.authenticatorsample.approvalrequests.events.RefreshApprovalRequestsEvent;
import com.twilio.authenticatorsample.registration.RegistrationActivity;
import com.twilio.authenticatorsample.utils.MessageHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment implements
        TwilioAuthenticatorTaskCallback<ApprovalRequests>,
        ApprovalRequestsListFragment.ApprovalRequestsSource,
        ApprovalRequestsAdapter.ApprovalRequestSelectedListener {


    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private TwilioAuthenticator twilioAuthenticator;
    private Bus bus;
    private MessageHelper messageHelper;

    private ApprovalRequests approvalRequests;
    private Long appId;

    /**
     * Use this factory method to create a new instance of Requests fragment
     *
     * @return A new instance of fragment RequestsFragment.
     */
    public static RequestsFragment newInstance(Long appId, TwilioAuthenticator twilioAuthenticator) {

        RequestsFragment requestsFragment = new RequestsFragment();
        requestsFragment.appId = appId;
        requestsFragment.twilioAuthenticator = twilioAuthenticator;

        return requestsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);
        initViews(rootView);
        initVars();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
        viewPager.addOnPageChangeListener(sectionsPagerAdapter);
        fetchApprovalRequests();
    }

    @Override
    public void onStop() {
        viewPager.removeOnPageChangeListener(sectionsPagerAdapter);
        messageHelper.dismiss();
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onSuccess(ApprovalRequests result) {
        approvalRequests = result;
        if (getActivity() != null && !getActivity().isFinishing()) {
            sectionsPagerAdapter.notifyDataSetChanged();
            bus.post(new ApprovalRequestsUpdatedEvent(true));
        }
    }

    @Override
    public void onError(Exception exception) {
        Log.e(RequestsFragment.class.getSimpleName(), "Error while getting approval requests for device", exception);
        String errorMessage = exception instanceof TwilioException ? ((TwilioException) exception).getBody() : getString(R.string.approval_request_fetch_error);
        final Snackbar snackbar = messageHelper.show(viewPager, errorMessage);
        bus.post(new ApprovalRequestsUpdatedEvent(true));

        if (!twilioAuthenticator.isDeviceRegistered() && getActivity() != null) {
            RegistrationActivity.startRegistrationActivity(getActivity(), R.string.registration_error_device_deleted);
            getActivity().finish();
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

    @Override
    public ApprovalRequestsUpdateInfo getApprovalRequestsUpdateInfo(int position) {
        List<ApprovalRequestStatus> statusList = Collections.emptyList();
        boolean hideStatusInfo = true;
        switch (position) {
            case SectionsPagerAdapter.PENDING_PAGE_POSITION:
                statusList = Arrays.asList(ApprovalRequestStatus.pending);
                hideStatusInfo = false;
                break;
            case SectionsPagerAdapter.ARCHIVE_PAGE_POSITION:
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
    public void onApprovalRequestSelected(ApprovalRequest approvalRequest) {
        final Intent intent = ApprovalRequestDetailActivity.createIntent(getContext(), approvalRequest);
        startActivity(intent);
    }

    @Subscribe
    public void onRefreshApprovalRequestEvent(RefreshApprovalRequestsEvent refreshApprovalRequestsEvent) {
        fetchApprovalRequests();
    }

    private void initViews(View rootView) {
        setHasOptionsMenu(true);
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) rootView.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), android.R.color.white), ContextCompat.getColor(getContext(), R.color.colorAccent));

    }

    private void initVars() {
        SampleApp sampleApp = (SampleApp) getActivity().getApplicationContext();
        bus = (sampleApp).getBus();
        messageHelper = new MessageHelper();
    }

    private void fetchApprovalRequests() {
        final List<ApprovalRequestStatus> statuses = Arrays.asList(ApprovalRequestStatus.approved, ApprovalRequestStatus.denied, ApprovalRequestStatus.expired, ApprovalRequestStatus.pending);

        twilioAuthenticator.getApprovalRequests(appId, statuses, null, this);
    }
    public ApprovalRequests getApprovalRequests() {
        if (approvalRequests == null) {
            approvalRequests = new ApprovalRequests();
        }
        return approvalRequests;
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
