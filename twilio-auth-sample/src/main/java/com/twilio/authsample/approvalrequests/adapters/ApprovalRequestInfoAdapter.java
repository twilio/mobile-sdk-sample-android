package com.twilio.authsample.approvalrequests.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twilio.authsample.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jsuarez on 3/16/16.
 */
public class ApprovalRequestInfoAdapter extends RecyclerView.Adapter<ApprovalRequestInfoAdapter.ApprovalRequestInfoViewHolder> {

    private final LayoutInflater layoutInflater;
    private LinkedHashMap<String, String> info;

    public ApprovalRequestInfoAdapter(@NonNull Context context, @NonNull Map<String, String> info) {
        this.layoutInflater = LayoutInflater.from(context);
        this.info = info == null ? new LinkedHashMap<String, String>() : new LinkedHashMap<>(info);
    }

    @Override
    public ApprovalRequestInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.approval_request_info_item, parent, false);
        return new ApprovalRequestInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovalRequestInfoViewHolder holder, int position) {
        final Map.Entry<String, String> entry = getItemForPosition(position);
        holder.info_key.setText(entry.getKey()+":");
        holder.info_value.setText(entry.getValue());
    }

    @Override
    public int getItemCount() {
        return getInfo().size();
    }

    private LinkedHashMap<String, String> getInfo() {
        if (info == null) {
            info = new LinkedHashMap<>();
        }
        return info;
    }

    private Map.Entry<String, String> getItemForPosition(int position) {
        final Set<Map.Entry<String, String>> entries = getInfo().entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            if (i == position) {
                return entry;
            }
            i++;
        }
        return null;
    }

    public static class ApprovalRequestInfoViewHolder extends RecyclerView.ViewHolder {
        TextView info_key;
        TextView info_value;

        public ApprovalRequestInfoViewHolder(View itemView) {
            super(itemView);
            info_key = (TextView) itemView.findViewById(R.id.info_key);
            info_value = (TextView) itemView.findViewById(R.id.info_value);
        }
    }
}
