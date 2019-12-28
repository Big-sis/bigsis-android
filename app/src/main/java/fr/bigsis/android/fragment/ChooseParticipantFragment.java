package fr.bigsis.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.activity.ChooseGroupActivity;
import fr.bigsis.android.adapter.ChooseGroupAdapter;
import fr.bigsis.android.adapter.ChooseParticipantAdapter;
import fr.bigsis.android.adapter.ChooseUsersAdapter;
import fr.bigsis.android.entity.OrganismEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.viewModel.ChooseParticipantViewModel;
import fr.bigsis.android.viewModel.ChooseUsersViewModel;
import fr.bigsis.android.viewModel.SearchContactViewModel;


public class ChooseParticipantFragment extends Fragment {

    Button btFinish;
    private OnFragmentInteractionListener mListener;
    private ChooseParticipantViewModel viewModel;
    private FirebaseFirestore mFirestore;
    private String userId;
    private FirebaseAuth mAuth;
    private TextView tvAllCampus;
    ConstraintLayout transitionContainer;
    ImageButton imBtCancel, imgBtBack;
    public ChooseParticipantFragment() {
    }

    public static ChooseParticipantFragment newInstance() {
        ChooseParticipantFragment fragment = new ChooseParticipantFragment();
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
        ButterKnife.bind(getActivity());
        viewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
        btFinish = view.findViewById(R.id.btFinish);
        tvAllCampus = view.findViewById(R.id.tvAllCampus);
        transitionContainer = getActivity().findViewById(R.id.toolbarLayout);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        imBtCancel.setVisibility(View.GONE);
        imgBtBack.setVisibility(View.VISIBLE);
        tvAllCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setParticipant(tvAllCampus.getText().toString());
                tvAllCampus.setSelected(true);
            }
        });
        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setParticipant("");
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addFrag = manager.findFragmentByTag("CHOOSE");

                    ft.remove(addFrag).commitAllowingStateLoss();
                    getActivity().onBackPressed();

                imBtCancel.setVisibility(View.VISIBLE);
                imgBtBack.setVisibility(View.GONE);
            }
        });
        viewModel.getParticipant().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(viewModel.getParticipant().getValue() != tvAllCampus.getText().toString()){
                    tvAllCampus.setSelected(false);
                }
            }
        });

        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addFrag = manager.findFragmentByTag("CHOOSE");
                ft.remove(addFrag).commitAllowingStateLoss();
                getActivity().onBackPressed();
                imBtCancel.setVisibility(View.VISIBLE);
                imgBtBack.setVisibility(View.GONE);
            }
        });
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mFirestore.collection("USERS").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String organism = documentSnapshot.getString("organism");
                RecyclerView mRecyclerRequest = view.findViewById(R.id.rvParticipantGroup);
                Query query = mFirestore
                        .collection(organism).document("AllCampus").collection("AllCampus")
                        .orderBy("groupName", Query.Direction.ASCENDING);
                PagedList.Config config = new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPrefetchDistance(10)
                        .setPageSize(20)
                        .build();
                FirestorePagingOptions<OrganismEntity> options = new FirestorePagingOptions.Builder<OrganismEntity>()
                        .setLifecycleOwner(getActivity())
                        .setQuery(query, config, OrganismEntity.class)
                        .build();
                ChooseParticipantAdapter adapterRequest = new ChooseParticipantAdapter(options, getContext(), organism);
                mRecyclerRequest.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerRequest.setAdapter(adapterRequest);
            }
        });
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
