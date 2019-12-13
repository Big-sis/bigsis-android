package fr.bigsis.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.GroupConversationActivity;
import fr.bigsis.android.adapter.ChooseUsersAdapter;
import fr.bigsis.android.adapter.GroupConversationAdapter;
import fr.bigsis.android.adapter.RequestListAdapter;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.viewModel.ChooseUsersViewModel;


public class ChooseFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Button btFinish;
    private ChooseUsersViewModel viewModel;
    private boolean mIsAttached = false;


    public ChooseFragment() {
    }

    public static ChooseFragment newInstance() {
        ChooseFragment fragment = new ChooseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        btFinish = view.findViewById(R.id.btFinish);
        viewModel = ViewModelProviders.of(this).get(ChooseUsersViewModel.class);

        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    viewModel = ViewModelProviders.of(getActivity()).get(ChooseUsersViewModel.class);
                    viewModel.getName().observe(getActivity(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            Toast.makeText(getContext(), viewModel.getName().getValue(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    Fragment addFrag = manager.findFragmentByTag("CHOOSE_FG");
                    ft.remove(addFrag).commitAllowingStateLoss();
                    getActivity().onBackPressed();
                }

        });
        String mCurrentUserId;
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        RecyclerView mRecyclerRequest = view.findViewById(R.id.rvssss);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        Query query = FirebaseFirestore.getInstance()
                .collection("users")
        ;

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, UserEntity.class)
                .build();

        ChooseUsersAdapter adapterRequest = new ChooseUsersAdapter(options, getContext());
        mRecyclerRequest.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerRequest.setAdapter(adapterRequest);
        return view;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
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
        mIsAttached = true;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }


}
