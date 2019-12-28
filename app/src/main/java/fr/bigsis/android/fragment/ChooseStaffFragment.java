package fr.bigsis.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import fr.bigsis.android.R;
import fr.bigsis.android.adapter.ChooseStaffAdapter;
import fr.bigsis.android.adapter.ChooseUsersAdapter;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.viewModel.ChooseUsersViewModel;

public class ChooseStaffFragment extends Fragment {
    private ChooseUsersViewModel viewModel;
    private OnFragmentInteractionListener mListener;
    Button btFinishStaff;
    ConstraintLayout transitionContainer;
    ImageButton imBtCancel, imgBtBack;
    public ChooseStaffFragment() {
        // Required empty public constructor
    }

    public static ChooseStaffFragment newInstance() {
        ChooseStaffFragment fragment = new ChooseStaffFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_staff, container, false);

        btFinishStaff = view.findViewById(R.id.btFinishStaff);
        viewModel = ViewModelProviders.of(this).get(ChooseUsersViewModel.class);
        transitionContainer = getActivity().findViewById(R.id.toolbarLayout);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        imBtCancel.setVisibility(View.GONE);
        imgBtBack.setVisibility(View.VISIBLE);
        Bundle arguments = getArguments();
        String idEvent = arguments.getString("ID");
        String campus = arguments.getString("campus");
        String organism = arguments.getString("organism");

        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.resetStaffMember();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addFrag = manager.findFragmentByTag("CHOOSE_STAFF");
                if(addFrag != null) {
                    ft.remove(addFrag).commitAllowingStateLoss();
                }
                getActivity().onBackPressed();

                imBtCancel.setVisibility(View.VISIBLE);
                imgBtBack.setVisibility(View.GONE);
            }
        });
        btFinishStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewModel = ViewModelProviders.of(getActivity()).get(ChooseUsersViewModel.class);

                viewModel.getStaffList().observe(getActivity(), new Observer<List<UserEntity>>() {
                    @Override
                    public void onChanged(List<UserEntity> userEntities) {
                    }
                });
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addFrag = manager.findFragmentByTag("CHOOSE_STAFF");
                ft.remove(addFrag).commitAllowingStateLoss();
                getActivity().onBackPressed();
            }
        });

        RecyclerView mRecyclerRequest = view.findViewById(R.id.rvStaff);

        Query query = FirebaseFirestore.getInstance()
                .collection("USERS");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, UserEntity.class)
                .build();

        ChooseStaffAdapter adapterRequest = new ChooseStaffAdapter(options, getContext(), campus, organism, idEvent);
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
