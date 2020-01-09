package fr.bigsis.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.MainActivity;
import fr.bigsis.android.activity.PolicyCGUActivity;
import fr.bigsis.android.activity.SignInActivity;
import fr.bigsis.android.activity.SignUpActivity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.viewModel.SignUpViewModel;

public class SignUpFragment extends Fragment {

    EditText etEmail;
    EditText etPassword;
    EditText etConfirmPassword;
    Button btSignUp;
    SignUpViewModel viewModel;
    RelativeLayout relativeLayoutSignUp;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBarSign;
    CheckBox acceptCGU;
    CheckBox acceptPolicy;
    FirebaseAuth.AuthStateListener mAuthListener;
    TextView textViewPolicy, textViewCgu;
    private OnFragmentInteractionListener mListener;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        etEmail = view.findViewById(R.id.etMailAdressSignUp);
        etPassword = view.findViewById(R.id.etPasswordSignUp);
        etConfirmPassword = view.findViewById(R.id.etConfirmPasswordSignUp);
        btSignUp = view.findViewById(R.id.btSignUpComplete);
        relativeLayoutSignUp = view.findViewById(R.id.relativeLayoutSignUp);
        progressBarSign = view.findViewById(R.id.progressBarSign);
        acceptCGU = view.findViewById(R.id.checkBoxCgu);
        acceptPolicy = view.findViewById(R.id.checkBoxPolicy);
        progressBarSign = view.findViewById(R.id.progressBarSignUp);
        textViewCgu = view.findViewById(R.id.textViewCgu);
        textViewPolicy = view.findViewById(R.id.textViewPolicy);
        textViewPolicy.getPaint().setUnderlineText(true);
        textViewCgu.getPaint().setUnderlineText(true);
        textViewPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PolicyCGUActivity.class);
                intent.putExtra("POLICY", "policy");
                startActivity(intent);
            }
        });
        textViewCgu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PolicyCGUActivity.class);
                intent.putExtra("CGU", "cgu");
                startActivity(intent);
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
            }
        });
        return view;
    }

    private void userRegister() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        //TODO enlever hotmail, gmail..
       if(!email.contains("@tbs-education") || !email.contains("@hotmail.fr")
               || !email.contains("@gmail.com") || !email.contains("@bigsis.fr")) {
            Snackbar.make(relativeLayoutSignUp, "Vous devez vous inscrire avec l'adresse e-mail de votre campus", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        if ((!acceptCGU.isChecked()) || (!acceptPolicy.isChecked())) {
            Snackbar.make(relativeLayoutSignUp, getString(R.string.required_fields), Snackbar.LENGTH_LONG)
                    .show();
            progressBarSign.setVisibility(View.GONE);
            return;
        }
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Snackbar.make(relativeLayoutSignUp, getString(R.string.required_fields), Snackbar.LENGTH_LONG)
                    .show();
            progressBarSign.setVisibility(View.GONE);
            return;
        } else if (!password.equals(confirmPassword)) {
            Snackbar.make(relativeLayoutSignUp, getString(R.string.password_not_identic), Snackbar.LENGTH_LONG)
                    .show();
            progressBarSign.setVisibility(View.GONE);
            return;
        } else {
            progressBarSign.setVisibility(View.VISIBLE);
            viewModel = ViewModelProviders.of(getActivity()).get(SignUpViewModel.class);
            viewModel.getUser().getValue();
            viewModel.getUser().observe(getActivity(), new Observer<UserEntity>() {
                @Override
                public void onChanged(UserEntity userEntity) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        progressBarSign.setVisibility(View.GONE);
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                    } else {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        String user_id = mFirebaseAuth.getCurrentUser().getUid();
                                        String organism = "";
                                        if (email.contains("@tbs-education") || email.contains("@hotmail.fr")
                                                || email.contains("@gmail.com")
                                                || email.contains("@bigsis.fr") || email.contains("@waxym.com")) {
                                            organism = "TBS";
                                            userEntity.setOrganism(organism);
                                        }
                                        db.collection("USERS").document(user_id).set(userEntity, SetOptions.merge());
                                        db.collection(organism).document("AllCampus").collection("AllUsers")
                                                .document(user_id).set(userEntity, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressBarSign.setVisibility(View.GONE);
                                                            AlertDialog builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                                                                    //TODO EXTARCT STRING
                                                                    .setMessage("Un e-mail de vérification vient de vous etre envoyé")
                                                                    .setPositiveButton(getString(R.string.ok), null)
                                                                    .show();
                                                            Button positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                                                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                                                }
                                                            });

                                                        } else {
                                                            progressBarSign.setVisibility(View.GONE);
                                                            Snackbar.make(relativeLayoutSignUp, task.getException().getMessage(), Snackbar.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                }
            });
        }
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
