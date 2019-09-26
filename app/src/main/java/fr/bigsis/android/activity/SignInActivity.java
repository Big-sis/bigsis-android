package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import fr.bigsis.android.Constants;
import fr.bigsis.android.R;
import fr.bigsis.android.repository.UserRepository;

public class SignInActivity extends AppCompatActivity {

    EditText emailBox, passwordBox;
    Button btSignIn;
    UserRepository authentication;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        emailBox = findViewById(R.id.etEmail);
        passwordBox = findViewById(R.id.etPassword);
        mAuth = FirebaseAuth.getInstance();
        btSignIn = findViewById(R.id.btSignIn);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                    startActivity(new Intent(SignInActivity.this, TripListActivity.class)); //TODO ProfileUserActivity with button log out
                }
            }
        };

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn() {
        String email = emailBox.getText().toString();
        String password = passwordBox.getText().toString();
        //mProgressBarSign.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(email)) {
            emailBox.setError("Veuillez entrer votre adresse mail");
            emailBox.requestFocus();
        } if ( TextUtils.isEmpty(password)){
            passwordBox.setError("Veuillez entrer votre mot de passe");
            passwordBox.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, "mot de passe ou e-mail incorrect", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
