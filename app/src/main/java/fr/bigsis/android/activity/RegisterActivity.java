package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;

public class RegisterActivity extends AppCompatActivity {
    EditText emailBox, passwordBox, usernameBox, descriptionBox;
    Button btRegister;
    FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailBox = findViewById(R.id.etEmailRegister);
        passwordBox = findViewById(R.id.etPasswordRegister);
        usernameBox = findViewById(R.id.etUserName);
        descriptionBox = findViewById(R.id.etDescription);

        btRegister = findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailBox.getText().toString();
                String password = passwordBox.getText().toString();
                String username = usernameBox.getText().toString();
                String descripiton = descriptionBox.getText().toString();
                if(email.isEmpty()){
                    emailBox.setError("pleaase");
                    emailBox.requestFocus();
                } else {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            } else {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                startActivity(new Intent(RegisterActivity.this, UserProfileActivity.class ));
                                String user_id = mFirebaseAuth.getCurrentUser().getUid();
                                UserEntity user = new UserEntity(username, descripiton);
                                db.collection("users")
                                        .document(user_id).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(RegisterActivity.this, "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(RegisterActivity.this, UserProfileActivity.class ));
        }
    }
}
