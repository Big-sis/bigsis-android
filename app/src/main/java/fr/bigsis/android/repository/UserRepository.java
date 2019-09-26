package fr.bigsis.android.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class UserRepository {
    private static final String TAG = "AuthenticationRepo";

    private FirebaseFirestore db;

    public UserRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void login(String key,
                      final OnSuccessListener<DocumentReference> successCallback,
                      final OnFailureListener failureCallback) {

        DocumentReference userRef = db.collection("users").document(key);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userSnapshot = task.getResult();
                    if (userSnapshot != null && userSnapshot.exists()) {
                        successCallback.onSuccess(userSnapshot.getReference());
                    } else {
                        createNewUser(successCallback, failureCallback);
                    }
                } else {
                    failureCallback.onFailure(task.getException());
                }
            }
        });
    }

    public void createNewUser(final OnSuccessListener<DocumentReference> successCallback,
                              final OnFailureListener failureCallback) {

        Map<String, Object> user = new HashMap<>();
        user.put("name", "Arth Limchiu");

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        successCallback.onSuccess(documentReference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        failureCallback.onFailure(e);
                    }
                });
    }
}
