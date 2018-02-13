package com.twilio.authenticatorsample.appdetail;

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
        void onAppClicked(App app);
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
                clickListener.onAppClicked(apps.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps == null ? 0 : apps.size();
    }

    public void addApps(List<App> apps) {
        for (App app: apps) {
            apps.add(app);
        }
        notifyDataSetChanged();
    }

    private void removeApp(Long appId) {
        Iterator<App> iterator = apps.iterator();
        while (iterator.hasNext()) {
            App app = iterator.next();
            if (app.getId() == appId) {
                iterator.remove();
                break;
            }
        }
    }

    public void removeApps(List<Long> appIds) {
        for (Long appId: appIds) {
            removeApp(appId);
        }

        notifyDataSetChanged();

    }

    public void updateApps(List<App> apps) {

        for (App app: apps) {
            removeApp(app.getId());
            apps.add(app);
        }

        notifyDataSetChanged();

    }

    public void setApps(List<App> authenticatorTokens) {
        this.apps = authenticatorTokens;
        notifyDataSetChanged();
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
