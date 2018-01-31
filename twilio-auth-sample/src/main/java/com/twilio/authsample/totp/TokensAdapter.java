package com.twilio.authsample.totp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twilio.authenticator.external.AuthenticatorToken;
import com.twilio.authsample.R;

import java.util.Iterator;
import java.util.List;

/**
 * Created by lvidal on 10/12/17.
 */

public class TokensAdapter extends RecyclerView.Adapter<TokensAdapter.ViewHolder> {

    private final OnClickListener clickListener;

    public interface OnClickListener {
        void onTokenClicked(AuthenticatorToken app);
    }

    private List<AuthenticatorToken> apps;

    public TokensAdapter(List<AuthenticatorToken> apps, OnClickListener clickListener) {
        super();
        this.apps = apps;
        this.clickListener = clickListener;
    }

    @Override
    public TokensAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.token_list_cell, parent, false);
        ViewHolder vh = new ViewHolder(rootView);
        return vh;
    }

    @Override
    public void onBindViewHolder(TokensAdapter.ViewHolder holder, final int position) {
        holder.tokenName.setText(apps.get(position).getName());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onTokenClicked(apps.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps == null ? 0 : apps.size();
    }

    public void addApp(AuthenticatorToken authenticatorToken) {
        apps.add(authenticatorToken);
        notifyDataSetChanged();
    }

    public void removeApp(String appId) {
        Iterator<AuthenticatorToken> iterator = apps.iterator();
        while (iterator.hasNext()) {
            AuthenticatorToken app = iterator.next();
            if (app.getAppId().equals(appId)) {
                iterator.remove();
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void updateApp(AuthenticatorToken authenticatorToken) {
        removeApp(authenticatorToken.getAppId());
        addApp(authenticatorToken);
        notifyDataSetChanged();
    }

    public void setApps(List<AuthenticatorToken> authenticatorTokens) {
        this.apps = authenticatorTokens;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tokenName;
        View rootView;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            tokenName = (TextView) rootView.findViewById(R.id.token_name);
        }
    }
}
