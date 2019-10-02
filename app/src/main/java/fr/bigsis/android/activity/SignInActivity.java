package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fr.bigsis.android.R;

public class SignInActivity extends AppCompatActivity {

    EditText emailBox, passwordBox;
    Button btSignIn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    TextView tvForgotPassword;

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
                    startActivity(new Intent(SignInActivity.this, UserProfileActivity.class));
                }
            }
        };

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();
            }
        });

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
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

        if (TextUtils.isEmpty(email)) {
            emailBox.setError("Veuillez entrer votre adresse e-mail");
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

    private void resetPassword() {
        String email = emailBox.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailBox.setError("Veuillez indiquer votre adresse e-mail");
            emailBox.requestFocus();
            return;
        }
        mAuth.setLanguageCode("fr");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "e-mail envoyé", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
