package com.twilio.authenticatorsample.totp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twilio.authenticator.external.App;
import com.twilio.authenticatorsample.R;

import java.util.Iterator;
import java.util.List;

/**
 * Created by lvidal on 10/12/17.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private final OnClickListener clickListener;

    public interface OnClickListener {
        void onTokenClicked(App app);
    }

    private List<App> apps;

    public AppsAdapter(List<App> apps, OnClickListener clickListener) {
        super();
        this.apps = apps;
        this.clickListener = clickListener;
    }

    @Override
    public AppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.token_list_cell, parent, false);
        ViewHolder vh = new ViewHolder(rootView);
        return vh;
    }

    @Override
    public void onBindViewHolder(AppsAdapter.ViewHolder holder, final int position) {
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

    public void addApp(App app) {
        apps.add(app);
        notifyDataSetChanged();
    }

    public void removeApp(String appId) {
        Iterator<App> iterator = apps.iterator();
        while (iterator.hasNext()) {
            App app = iterator.next();
            if (app.getId().equals(appId)) {
                iterator.remove();
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void updateApp(App authenticatorToken) {
        removeApp(authenticatorToken.getId());
        addApp(authenticatorToken);
        notifyDataSetChanged();
    }

    public void setApps(List<App> authenticatorTokens) {
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
