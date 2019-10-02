package fr.bigsis.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.bigsis.android.R;


public class ProfileFragment extends Fragment {

    private OnFragmentInteractionListenerProfile mListener;
    private TextView tvUserNameFragment;
    private TextView tvUserDescFragment;

    private String userId, user_name, description_user;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;


    public ProfileFragment() {
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
                .document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user_name = documentSnapshot.getString("username");
                        description_user = documentSnapshot.getString("description");
                        tvUserNameFragment.setText(user_name);
                        tvUserDescFragment.setText(description_user);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUserNameFragment = view.findViewById(R.id.tvUserNameFragment);
        tvUserDescFragment = view.findViewById(R.id.tvUserDescriptionFragment);

        return view;

    }


    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionProfile();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerProfile) {
            mListener = (OnFragmentInteractionListenerProfile) context;
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

    public interface OnFragmentInteractionListenerProfile {
        void onFragmentInteractionProfile();
    }
}
