package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.bigsis.android.R;

public class MainActivity extends BigsisActivity {

    Button btSignIn, btSignUp;
    FirebaseAuth mFirebaseAuth;
    String firstname;
    String userId;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btSignUp = findViewById(R.id.btSignUp);


        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();

        btSignIn = findViewById(R.id.btSignIn);
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mFirebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                } else if (mFirebaseAuth.getCurrentUser() != null) {
                    if (!mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    } else {
                        registerContinuation();
                    }
                }
            }

        });
    }

    private void registerContinuation() {
        userId = mFirebaseAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
                .document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        firstname = documentSnapshot.getString("firstname");
                        if (firstname != null) {
                            startActivity(new Intent(MainActivity.this, MapsActivity.class));

                        } else {
                            mFirebaseAuth.signOut();
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                    }
                });
    }
}
