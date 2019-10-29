package fr.bigsis.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;

public class RegisterActivity extends AppCompatActivity {
    EditText emailBox, passwordBox;
    Button btRegister;
    FirebaseAuth mFirebaseAuth;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        constraintLayout = findViewById(R.id.constraint_layout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailBox = findViewById(R.id.etEmailRegister);
        passwordBox = findViewById(R.id.etPasswordRegister);

        btRegister = findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegister();
            }
        });
    }

    private void userRegister() {
        String email = emailBox.getText().toString();
        String password = passwordBox.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(constraintLayout, getString(R.string.required_fields), Snackbar.LENGTH_LONG)
                    .show();
        } else {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String user_id = mFirebaseAuth.getCurrentUser().getUid();
                        UserEntity user = new UserEntity(null, null, null, null, null);
                        db.collection("users")
                                .document(user_id).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            AlertDialog builder = new AlertDialog.Builder(RegisterActivity.this, R.style.AlertDialogStyle)
                                                    .setMessage("Un e-mail de vérification vient de vous etre envoyé")
                                                    .setPositiveButton(getString(R.string.ok), null)
                                                    .show();
                                            emailBox.setText("");
                                            passwordBox.setText("");
                                        } else {
                                            Snackbar.make(constraintLayout, task.getException().getMessage(), Snackbar.LENGTH_SHORT)
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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
