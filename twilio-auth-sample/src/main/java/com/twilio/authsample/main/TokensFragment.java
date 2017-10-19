package com.twilio.authsample.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.external.AuthenticatorToken;
import com.twilio.authsample.R;
import com.twilio.authsample.totp.TokensAdapter;

import java.util.List;

/**
 * Created by lvidal on 10/12/17.
 */

public class TokensFragment extends Fragment implements TokensAdapter.OnClickListener {
    private RecyclerView recyclerView;
    private TwilioAuthenticator twilioAuthenticator;
    private TokensAdapter tokensAdapter;

    public static TokensFragment newInstance(TwilioAuthenticator twilioAuthenticator) {
        TokensFragment tokensFragment = new TokensFragment();
        tokensFragment.twilioAuthenticator = twilioAuthenticator;
        return tokensFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tokens, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.tokens_list);
        this.recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        List<AuthenticatorToken> apps = twilioAuthenticator.getApps();

        tokensAdapter = new TokensAdapter(apps, this);
        recyclerView.setAdapter(tokensAdapter);
    }

    @Override
    public void onTokenClicked(AuthenticatorToken app) {
        Intent intent = new Intent(getActivity(), TokenDetailsActivity.class);
        intent.putExtra(TokenDetailsActivity.EXTRA_APP_ID, app.getAppId());
        intent.putExtra(Intent.EXTRA_TITLE, app.getName());

        getActivity().startActivity(intent);
    }
}
