package fr.bigsis.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import fr.bigsis.android.R;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.viewModel.SignUpViewModel;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class StartSignUpFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    EditText etUserNameSignUp;
    EditText etNameSignUp;
    EditText etFirstnameSignUp;
    EditText etDescriptionSignUp;
    String username;
    String lastname;
    String firstname;
    String description;
    Button btSignUpStart;
    private FirebaseFirestore mFirestore;

    private SignUpViewModel viewModel;
    SignUpFragment continueSignUp = new SignUpFragment();

    public StartSignUpFragment() {
        // Required empty public constructor
    }

    public static StartSignUpFragment newInstance() {
        StartSignUpFragment fragment = new StartSignUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_start_sign_up, container, false);
        etUserNameSignUp = view.findViewById(R.id.etUserNameSignUp);
        etNameSignUp = view.findViewById(R.id.etNameSignUp);
        etFirstnameSignUp = view.findViewById(R.id.etFirstnameSignUp);
        etDescriptionSignUp = view.findViewById(R.id.etDescriptionSignUp);
        btSignUpStart = view.findViewById(R.id.btSignUpContinuation);
        viewModel = ViewModelProviders.of(getActivity()).get(SignUpViewModel.class);

        btSignUpStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUserNameSignUp.getText().toString().toLowerCase().trim();
                if (TextUtils.isEmpty(username)) {
                    etUserNameSignUp.setError(getString(R.string.required_field));
                    etUserNameSignUp.requestFocus();
                    return;
                }
                lastname = etNameSignUp.getText().toString().trim();
                if (TextUtils.isEmpty(lastname)) {
                    etNameSignUp.setError(getString(R.string.required_field));
                    etNameSignUp.requestFocus();
                    return;
                }
                firstname = etFirstnameSignUp.getText().toString().trim();
                if (TextUtils.isEmpty(firstname)) {
                    etFirstnameSignUp.setError(getString(R.string.required_field));
                    etFirstnameSignUp.requestFocus();
                    return;
                }
                mFirestore = FirebaseFirestore.getInstance();
                CollectionReference usersRef = mFirestore.collection("USERS");
                Query query = usersRef.whereEqualTo("username", username);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                String user = documentSnapshot.getString("username").toLowerCase().trim();
                                if(user.equals(username.toLowerCase())){
                                    etUserNameSignUp.setError(getString(R.string.username_exist));
                                    etUserNameSignUp.requestFocus();
                                return;
                                }
                            }
                        }
                        if(task.getResult().size() == 0 ){
                            description = etDescriptionSignUp.getText().toString().trim();
                            UserEntity userEntity = new UserEntity(username, description, null, firstname, lastname, false, null, null);
                            viewModel.setUser(userEntity);
                            openFragment();
                            KeyboardHelper.CloseKeyboard(getContext(), view);
                        }
                    }
                });

            }
        });
        return view;
    }
    private void openFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.sign_up_enter, R.animator.sign_up_exit);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragmentContaienrSignUp, continueSignUp, "ContinueSignUP")
                .commit();
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
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }

    private void verifyUsername(String etUsername, EditText editText){


    }
}
