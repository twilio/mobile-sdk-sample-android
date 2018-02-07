package com.twilio.authenticatorsample.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class AppsFragment extends Fragment implements AppsAdapter.OnClickListener, AuthenticatorObserver {
    private RecyclerView recyclerView;
    private TwilioAuthenticator twilioAuthenticator;
    private AppsAdapter appsAdapter;

    public static AppsFragment newInstance(TwilioAuthenticator twilioAuthenticator) {
        AppsFragment appsFragment = new AppsFragment();
        appsFragment.twilioAuthenticator = twilioAuthenticator;
        return appsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apps, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.tokens_list);
        this.recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        appsAdapter = new AppsAdapter(new ArrayList<App>(), this);
        recyclerView.setAdapter(appsAdapter);
    }

    @Override
    public void onTokenClicked(App app) {
        Intent intent = new Intent(getActivity(), AppDetailActivity.class);
        intent.putExtra(AppDetailActivity.EXTRA_APP_ID, app.getId());
        intent.putExtra(Intent.EXTRA_TITLE, app.getName());

        getActivity().startActivity(intent);
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
    public void onAppDeleted(@NonNull String appId) {
        appsAdapter.removeApp(appId);
    }


    @Override
    public void onNewCode(List<App> apps) {
        appsAdapter.setApps(apps);
    }

    @Override
    public void onAppAdded(App app) {
        appsAdapter.addApp(app);
    }

    @Override
    public void onAppUpdated(App app) {
        appsAdapter.updateApp(app);
    }
}