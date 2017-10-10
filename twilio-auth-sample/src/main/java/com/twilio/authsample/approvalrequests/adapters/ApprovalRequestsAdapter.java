package com.twilio.authsample.approvalrequests.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twilio.authenticator.external.ApprovalRequest;
import com.twilio.authsample.R;
import com.twilio.authsample.approvalrequests.ApprovalRequestComparator;
import com.twilio.authsample.utils.TimeFormattingUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jsuarez on 3/11/16.
 */
public class ApprovalRequestsAdapter extends RecyclerView.Adapter<ApprovalRequestsAdapter.ApprovalRequestViewHolder> {

    private final ApprovalRequestSelectedListener approvalRequestSelectedListener;
    private List<ApprovalRequest> approvalRequests;
    final LayoutInflater layoutInflater;
    private final Picasso picasso;
    private boolean hideStatusInfo;

    public ApprovalRequestsAdapter(Context context, ApprovalRequestSelectedListener approvalRequestSelectedListener) {
        layoutInflater = LayoutInflater.from(context);
        this.approvalRequestSelectedListener = approvalRequestSelectedListener;
        picasso = Picasso.with(context);
    }

    @Override
    public ApprovalRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.approval_request_list_item, parent, false);
        return new ApprovalRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovalRequestViewHolder holder, int position) {
        final ApprovalRequest approvalRequest = getApprovalRequests().get(position);

        // Message
        CharSequence message = TextUtils.isEmpty(approvalRequest.getMessage()) ? "" : Html.fromHtml(approvalRequest.getMessage());
        holder.transactionMessage.setText(message);

        // Time ago
        if (approvalRequest.getCreationDate() != null) {
            holder.transactionTime.setText(TimeFormattingUtils.formatTransactionTime(holder.transactionTime.getContext(), approvalRequest.getCreationDate()));
        } else {
            holder.transactionTime.setText("");
        }

        // Status
        bindStatus(holder, approvalRequest);

        // Divider
        if (position == getItemCount() - 1) {
            holder.transactionDivider.setVisibility(View.GONE);
        } else {
            holder.transactionDivider.setVisibility(View.VISIBLE);
        }

        // TODO: Load the correct account logo

        // Click listener
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (approvalRequestSelectedListener != null) {
                    approvalRequestSelectedListener.onApprovalRequestSelected(approvalRequest);
                }
            }
        });
    }

    private void bindStatus(ApprovalRequestViewHolder holder, ApprovalRequest approvalRequest) {
        if (!isHideStatusInfo()) {
            switch (approvalRequest.getStatus()) {
                case pending:
                    holder.transactionStatusMessage.setVisibility(View.VISIBLE);
                    if (approvalRequest.getExpirationTimestamp() >= 0) {
                        String expiresAtMessage = TimeFormattingUtils.formatExpirationTime(approvalRequest.getExpirationTimestamp());
                        holder.transactionStatusMessage.setText(holder.transactionStatusMessage.getContext().getString(R.string.expires_at_message, expiresAtMessage));
                    } else {
                        holder.transactionStatusMessage.setText("");
                    }
                    break;
                case expired:
                    holder.transactionStatusMessage.setVisibility(View.VISIBLE);
                    String expiredOnMessage = holder.transactionStatusMessage.getContext().getString(R.string.expired_on_message, TimeFormattingUtils.formatExpirationTime(approvalRequest.getExpirationTimestamp()));
                    holder.transactionStatusMessage.setText(expiredOnMessage);
                    break;
                case denied:
                    holder.transactionStatusMessage.setVisibility(View.VISIBLE);
                    holder.transactionStatusMessage.setText(R.string.transaction_denied_title);
                    break;
                case approved:
                    holder.transactionStatusMessage.setVisibility(View.VISIBLE);
                    holder.transactionStatusMessage.setText(R.string.transaction_approved_title);
                    break;
                default:
                    holder.transactionStatusMessage.setVisibility(View.GONE);
                    break;
            }
        } else {
            holder.transactionStatusMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return getApprovalRequests().size();
    }

    public List<ApprovalRequest> getApprovalRequests() {
        if (approvalRequests == null) {
            approvalRequests = new ArrayList<>();
        }
        return approvalRequests;
    }

    public void setApprovalRequests(List<ApprovalRequest> approvalRequests) {
        this.approvalRequests = approvalRequests;
        Collections.sort(this.approvalRequests, new ApprovalRequestComparator());
    }

    public void setHideStatusInfo(boolean hideStatusInfo) {
        this.hideStatusInfo = hideStatusInfo;
    }

    public boolean isHideStatusInfo() {
        return hideStatusInfo;
    }

    public class ApprovalRequestViewHolder extends RecyclerView.ViewHolder {
        ImageView transactionLogo;
        TextView transactionMessage;
        TextView transactionStatusMessage;
        TextView transactionTime;
        View transactionDivider;
        View container;

        public ApprovalRequestViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            transactionLogo = (ImageView) itemView.findViewById(R.id.transactionLogo);
            transactionMessage = (TextView) itemView.findViewById(R.id.transactionMessage);
            transactionStatusMessage = (TextView) itemView.findViewById(R.id.transactionStatusMessage);
            transactionTime = (TextView) itemView.findViewById(R.id.transactionTime);
            transactionDivider = itemView.findViewById(R.id.transactionDivider);
        }
    }

    public interface ApprovalRequestSelectedListener {
        void onApprovalRequestSelected(ApprovalRequest approvalRequest);
    }
}
