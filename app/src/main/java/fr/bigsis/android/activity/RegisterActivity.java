package fr.bigsis.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;

public class RegisterActivity extends AppCompatActivity {
    EditText emailBox, passwordBox, usernameBox, descriptionBox;
    EditText firstnameBox, lastnameBox;
    Button btRegister;
    FirebaseAuth mFirebaseAuth;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        constraintLayout= findViewById(R.id.constraint_layout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firstnameBox = findViewById(R.id.etFirstname);
        lastnameBox = findViewById(R.id.etLastname);
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
                String firstname = firstnameBox.getText().toString();
                String lastname = lastnameBox.getText().toString();
                if (email.isEmpty() || password.isEmpty() || username.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
                    Snackbar.make(constraintLayout, "Champs obligatoires", Snackbar.LENGTH_SHORT)
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
                                startActivity(new Intent(RegisterActivity.this, UserProfileActivity.class));
                                String user_id = mFirebaseAuth.getCurrentUser().getUid();
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bigsis-777.appspot.com/images/img_profile_default.png");

                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUrl = uri;
                                        String imageProfileUrl = downloadUrl.toString();
                                        UserEntity user = new UserEntity(username, descripiton, imageProfileUrl, firstname, lastname);
                                        db.collection("users")
                                                .document(user_id).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(RegisterActivity.this, "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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
            startActivity(new Intent(RegisterActivity.this, UserProfileActivity.class));
        }
    }
}
