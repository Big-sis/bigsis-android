package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fr.bigsis.android.R;

public class SignInActivity extends AppCompatActivity {

    EditText emailBox, passwordBox;
    Button btSignIn;
    TextView tvForgotPassword;
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
            emailBox.setError(getString(R.string.email_adress));
            emailBox.requestFocus();
        }
        if (TextUtils.isEmpty(password)) {
            passwordBox.setError(getString(R.string.enter_password));
            passwordBox.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, R.string.email_or_password_incorrect, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void resetPassword() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        final AppCompatEditText input = view.findViewById(R.id.editText);
        AlertDialog builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.forgot_password)
                .setMessage(getString(R.string.email_adress))
                .setPositiveButton(getString(R.string.send), null)
                .setNegativeButton(getString(R.string.cancel), null)
                .setView(view)
                .show();

        Button positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = input.getText().toString();
                if (email.isEmpty()) {
                    input.setError(getString(R.string.email_adress));
                    input.requestFocus();
                } else {
                    mAuth.setLanguageCode("fr");
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, R.string.email_was_sent, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
