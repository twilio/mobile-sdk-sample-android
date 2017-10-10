package com.twilio.authsample.approvalrequests;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.twilio.authenticator.external.ApprovalRequest;
import com.twilio.authenticator.external.ApprovalRequestStatus;
import com.twilio.authsample.App;
import com.twilio.authsample.R;
import com.twilio.authsample.approvalrequests.adapters.ApprovalRequestsAdapter;
import com.twilio.authsample.approvalrequests.events.ApprovalRequestsUpdatedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment containing a list of ApprovalRequest objects.
 */
public class ApprovalRequestsListFragment extends Fragment {

    private static final String ARGS_POSITION = "approval_position";
    private RecyclerView approvalRequests;
    private ApprovalRequestsAdapter approvalRequestsAdapter;

    private ApprovalRequestsSource approvalRequestsSource;
    private ApprovalRequestsAdapter.ApprovalRequestSelectedListener approvalRequestSelectedListener;
    private Bus bus;

    private SwipeRefreshLayout swipeRefresh;
    private TextView emptyView;

    public ApprovalRequestsListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ApprovalRequestsListFragment newInstance(int position) {
        final ApprovalRequestsListFragment approvalRequestsListFragment = new ApprovalRequestsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_POSITION, position);
        approvalRequestsListFragment.setArguments(args);
        return approvalRequestsListFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Detect if we are a inside fragment or activity
        Object parent = getParentFragment();
        if (parent == null) {
            parent = context;
        }

        if (parent instanceof ApprovalRequestsSource) {
            approvalRequestsSource = (ApprovalRequestsSource) parent;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ApprovalRequestsSource");
        }

        if (parent instanceof ApprovalRequestsAdapter.ApprovalRequestSelectedListener) {
            approvalRequestSelectedListener = (ApprovalRequestsAdapter.ApprovalRequestSelectedListener) parent;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ApprovalRequestSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        approvalRequestsSource = null;
        approvalRequestSelectedListener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_approval_requests_list, container, false);
        approvalRequests = (RecyclerView) rootView.findViewById(R.id.approvalRequests);
        approvalRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
        approvalRequestsAdapter = new ApprovalRequestsAdapter(getActivity(), approvalRequestSelectedListener);
        approvalRequests.setAdapter(approvalRequestsAdapter);
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        emptyView = (TextView) rootView.findViewById(R.id.emptyView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bus = ((App) getContext().getApplicationContext()).getBus();
        initRefreshListener();
        updateApprovalRequestList(approvalRequestsSource.getApprovalRequestsUpdateInfo(getPosition()));
    }

    private void initRefreshListener() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                emptyView.setVisibility(View.GONE);
                approvalRequestsSource.startApprovalRequestUpdate();
            }
        });
    }

    private void updateApprovalRequestList(final ApprovalRequestsUpdateInfo approvalRequestsUpdateInfo) {
        List<ApprovalRequest> filteredApprovalRequests = new ArrayList<>();
        List<ApprovalRequestStatus> validStatus = approvalRequestsUpdateInfo.getStatusList();
        if (validStatus.contains(ApprovalRequestStatus.pending)) {
            filteredApprovalRequests.addAll(approvalRequestsUpdateInfo.getApprovalRequests().getPending());
        }

        if (validStatus.contains(ApprovalRequestStatus.approved)) {
            filteredApprovalRequests.addAll(approvalRequestsUpdateInfo.getApprovalRequests().getApproved());
        }

        if (validStatus.contains(ApprovalRequestStatus.denied)) {
            filteredApprovalRequests.addAll(approvalRequestsUpdateInfo.getApprovalRequests().getDenied());
        }

        if (validStatus.contains(ApprovalRequestStatus.expired)) {
            filteredApprovalRequests.addAll(approvalRequestsUpdateInfo.getApprovalRequests().getExpired());
        }

        updateEmptyView(filteredApprovalRequests.size(), approvalRequestsUpdateInfo.getStatusList());
        approvalRequestsAdapter.setApprovalRequests(filteredApprovalRequests);
        approvalRequestsAdapter.setHideStatusInfo(approvalRequestsUpdateInfo.isHideStatusInfo());
        approvalRequestsAdapter.notifyDataSetChanged();
    }

    private void updateEmptyView(int numberOfApprovalRequests, List<ApprovalRequestStatus> statusList) {
        if (numberOfApprovalRequests != 0) {
            emptyView.setVisibility(View.GONE);
            return;
        }

        emptyView.setVisibility(View.VISIBLE);

        if (statusList.size() == 1 && statusList.get(0) == ApprovalRequestStatus.pending) {
            emptyView.setText(R.string.transactions_empty_pending);
            return;
        }
        emptyView.setText(R.string.transactions_empty_generic);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onApprovalRequestsUpdated(ApprovalRequestsUpdatedEvent approvalRequestsUpdatedEvent) {
        if (approvalRequestsUpdatedEvent.isNetworkFetchFinished() && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
        updateApprovalRequestList(approvalRequestsSource.getApprovalRequestsUpdateInfo(getPosition()));
    }

    private int getPosition() {
        return getArguments().getInt(ARGS_POSITION);
    }


    public interface ApprovalRequestsSource {
        ApprovalRequestsUpdateInfo getApprovalRequestsUpdateInfo(int position);

        void startApprovalRequestUpdate();
    }
}
