package fr.bigsis.android.helpers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UpdateProfileHelper {


    public static void updateProfileFriends(String mCurrentUserId, String friendOrRequest) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String organism = documentSnapshot.getString("organism");
                String getFirstname = documentSnapshot.getString("firstname");
                String getLastname = documentSnapshot.getString("lastname");
                String getUsername = documentSnapshot.getString("username");
                String getDescription = documentSnapshot.getString("description");
                mFirestore.collection("USERS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idCollection = document.getId().toString();
                                DocumentReference collectionReference = mFirestore.collection("USERS").document(idCollection)
                                        .collection(friendOrRequest).document(mCurrentUserId);
                                String firstnameToUpdate = document.getString("firstname");
                                String lastnameToUpdate = document.getString("lastname");
                                String usernameToUpdate = document.getString("username");
                                String descriptionToUpdate = document.getString("description");
                                if (!getFirstname.equals(firstnameToUpdate)) {
                                    collectionReference.update("firstname", getFirstname);
                                }
                                if (!getLastname.equals(lastnameToUpdate)) {
                                    collectionReference.update("lastname", getLastname);
                                }
                                if (!getUsername.equals(usernameToUpdate)) {
                                    collectionReference.update("username", getUsername);
                                }
                                if (!getDescription.equals(descriptionToUpdate)) {
                                    collectionReference.update("description", getDescription);
                                }
                            }
                            }
                    }
                });
            }
        });
    }
    public static void updateFieldProfile(String mCurrentUserId, String tripOrGroupOrEvent, String participantOrCreator) {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String organism = documentSnapshot.getString("organism");
                String getFirstname = documentSnapshot.getString("firstname");
                String getLastname = documentSnapshot.getString("lastname");
                String getUsername = documentSnapshot.getString("username");
                String getDescription = documentSnapshot.getString("description");

                mFirestore.collection(organism).document("AllCampus").collection(tripOrGroupOrEvent)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idCollection = document.getId().toString();

                                DocumentReference collectionReference = mFirestore.collection(organism).document("AllCampus")
                                        .collection(tripOrGroupOrEvent).document(idCollection).collection(participantOrCreator)
                                        .document(mCurrentUserId);
                                collectionReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String firstnameToUpdate = document.getString("firstname");
                                        String lastnameToUpdate = document.getString("lastname");
                                        String usernameToUpdate = document.getString("username");
                                        String descriptionToUpdate = document.getString("description");
                                        if (!getFirstname.equals(firstnameToUpdate)) {
                                            collectionReference.update("firstname", getFirstname);
                                        }
                                        if (!getLastname.equals(lastnameToUpdate)) {
                                            collectionReference.update("lastname", getLastname);
                                        }
                                        if (!getUsername.equals(usernameToUpdate)) {
                                            collectionReference.update("username", getUsername);
                                        }
                                        if (!getDescription.equals(descriptionToUpdate)) {
                                            collectionReference.update("description", getDescription);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    public static void updateFieldAllUsers(String mCurrentUserId) {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String organism = documentSnapshot.getString("organism");
                String getFirstname = documentSnapshot.getString("firstname");
                String getLastname = documentSnapshot.getString("lastname");
                String getUsername = documentSnapshot.getString("username");
                String getDescription = documentSnapshot.getString("description");

                DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus").collection("AllUsers")
                        .document(mCurrentUserId);

                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String firstnameToUpdate = documentSnapshot.getString("firstname");
                        String lastnameToUpdate = documentSnapshot.getString("lastname");
                        String usernameToUpdate = documentSnapshot.getString("username");
                        String descriptionToUpdate = documentSnapshot.getString("description");
                        if (!getFirstname.equals(firstnameToUpdate)) {
                            documentReference.update("firstname", getFirstname);
                        }
                        if (!getLastname.equals(lastnameToUpdate)) {
                            documentReference.update("lastname", getLastname);
                        }
                        if (!getUsername.equals(usernameToUpdate)) {
                            documentReference.update("username", getUsername);
                        }
                        if (!getDescription.equals(descriptionToUpdate)) {
                            documentReference.update("description", getDescription);
                        }
                    }
                });
            }
        });
    }
}
