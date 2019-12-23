package fr.bigsis.android.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.adapter.RequestListAdapter;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.FirestoreHelper;


public class RequestFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public RequestFragment() {
    }

    public static RequestFragment newInstance() {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_fragment);
        RecyclerView mRecyclerRequest = view.findViewById(R.id.rvRequestList);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        FirestoreHelper.compareForParticipants("AllTrips", "Participants");
        FirestoreHelper.compareForParticipants("AllTrips", "Creator");
        FirestoreHelper.compareForParticipants("AllChatGroups", "Participants");
        FirestoreHelper.compareForParticipants("AllChatGroups", "Creator");
        //TODO


        Query query = FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(mCurrentUserId)
                .collection("RequestReceived");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, UserEntity.class)
                .build();

        RequestListAdapter adapterRequest = new RequestListAdapter(options, getContext(), mSwipeRefreshLayout);
        mRecyclerRequest.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerRequest.setAdapter(adapterRequest);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapterRequest.refresh();
            }
        });
        return view;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionRequest();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteractionRequest();
    }
}
