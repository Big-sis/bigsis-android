package fr.bigsis.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;

public class RegisterContinuationActivity extends AppCompatActivity {

    EditText usernameBox, descriptionBox, firstnameBox, lastnameBox;
    Button btRegister;
    FirebaseAuth mFirebaseAuth;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_continuation);

        constraintLayout = findViewById(R.id.constraint_layout_page_two);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firstnameBox = findViewById(R.id.etFirstname);
        lastnameBox = findViewById(R.id.etLastname);
        usernameBox = findViewById(R.id.etUserName);
        descriptionBox = findViewById(R.id.etDescription);

        btRegister = findViewById(R.id.btRegisterPageTwo);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               userCompleteProfile();
            }
        });
    }

    private void userCompleteProfile() {
        String username = usernameBox.getText().toString();
        String description = descriptionBox.getText().toString();
        String firstname = firstnameBox.getText().toString();
        String lastname = lastnameBox.getText().toString();

        if (username.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
            Snackbar.make(constraintLayout, getString(R.string.required_fields), Snackbar.LENGTH_LONG)
                    .show();
        } else {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            startActivity(new Intent(RegisterContinuationActivity.this, UserProfileActivity.class));
            String user_id = mFirebaseAuth.getCurrentUser().getUid();
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bigsis-777.appspot.com/images/img_profile_default.png");

            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUrl = uri;
                    String imageProfileUrl = downloadUrl.toString();

                    db.collection("users")
                            .document(user_id).update("username", username,
                            "description", description,
                            "imageProfileUrl", imageProfileUrl,
                            "firstname", firstname,
                            "lastname", lastname
                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar.make(constraintLayout, "Votre profil a bien été crée", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            });
        }
    }
}
