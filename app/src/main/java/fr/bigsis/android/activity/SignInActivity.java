package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.bigsis.android.R;

public class SignInActivity extends AppCompatActivity {

    EditText etMailAdressSignIn;
    EditText etPasswordSignIn;
    Button btSignInComplete;
    ProgressBar progressBarSign;
    TextView tvForgotPassword;
    RelativeLayout relativeLayoutSignIn;
    String groupCampus;
    String userId;
    FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        relativeLayoutSignIn = findViewById(R.id.relativeLayoutSignIn);
        etMailAdressSignIn = findViewById(R.id.etMailAdressSignIn);
        etPasswordSignIn = findViewById(R.id.etPasswordSignIn);
        mAuth = FirebaseAuth.getInstance();
        btSignInComplete = findViewById(R.id.btSignInComplete);
        progressBarSign = findViewById(R.id.progressBarSignIn);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                        btSignInComplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                verifyEmailSignIn();
                            }
                        });
                    } else {
                        registerContinuation();
                    }
                }
            }
        };
        btSignInComplete.setOnClickListener(new View.OnClickListener() {
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

    private void startSignIn() {
        String email = etMailAdressSignIn.getText().toString();
        String password = etPasswordSignIn.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etMailAdressSignIn.setError(getString(R.string.email_adress));
            etMailAdressSignIn.requestFocus();
        }
        if (TextUtils.isEmpty(password)) {
            etPasswordSignIn.setError(getString(R.string.enter_password));
            etPasswordSignIn.requestFocus();
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

    private void verifyEmailSignIn() {
        AlertDialog builder = new AlertDialog.Builder(SignInActivity.this, R.style.AlertDialogStyle)
                .setTitle("Un e-mail de vérification vous a déjà été envoyé")
                .setMessage("Renvoyer un autre e-mail pour vérification ?")
                .setPositiveButton(getString(R.string.yes), null)
                .setNegativeButton(getString(R.string.no), null)
                .show();
        etMailAdressSignIn.setText("");
        etPasswordSignIn.setText("");
        Button positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog builder = new AlertDialog.Builder(SignInActivity.this, R.style.AlertDialogStyle)
                                    .setMessage("Un e-mail de vérification vient de vous etre envoyé")
                                    .setPositiveButton("OK", null)
                                    .show();
                            Button positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(SignInActivity.this, SignInActivity.class));
                                }
                            });
                        } else {
                            Snackbar.make(relativeLayoutSignIn, task.getException().getMessage(), Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        });
    }

    private void registerContinuation() {
        userId = mAuth.getCurrentUser().getUid();
        String email = etMailAdressSignIn.getText().toString();
        progressBarSign.setVisibility(View.VISIBLE);
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        groupCampus = documentSnapshot.getString("groupCampus");

                        if (groupCampus != null) {
                            startActivity(new Intent(SignInActivity.this, MapsActivity.class));
                        } else {
                            String organism = documentSnapshot.getString("organism");
                            Intent intent = new Intent(SignInActivity.this, ChooseGroupActivity.class);
                            intent.putExtra("ORGANISM", organism);
                            startActivity(intent);
                        }
                    }
                });
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressBarSign.setVisibility(View.GONE);
    }
}
