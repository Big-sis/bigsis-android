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

import java.util.ArrayList;

import fr.bigsis.android.entity.UserEntity;

public class UpdateHelper {

    public static void updateEventToAllCampus(String organism, String idEvent, String newIdEvent) {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus").collection("AllEvents")
                .document(idEvent);
        CollectionReference collectionReference = mFirestore.collection(organism).document("AllCampus").collection("AllCampus");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                documentReference.collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idUser = document.getId();
                                documentReference.collection("Participants")
                                        .document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String username = documentSnapshot.getString("username");
                                        String description = documentSnapshot.getString("description");
                                        String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                                        String firstname = documentSnapshot.getString("firstname");
                                        String lastname = documentSnapshot.getString("lastname");
                                        boolean isAdmin = documentSnapshot.getBoolean("admin");
                                        String groupCampus = documentSnapshot.getString("groupCampus");
                                        String organism = documentSnapshot.getString("organism");
                                        String lastnameAndFirstname = documentSnapshot.getString("lastnameAndFirstname");
                                        UserEntity userEntity = new UserEntity(username, description, imageProfileUrl, firstname, lastname, isAdmin, groupCampus
                                                , organism, lastnameAndFirstname);

                                            collectionReference.document("Campus Paris").collection("Events")
                                                    .document(newIdEvent).collection("Participants").document(idUser)
                                                    .set(userEntity);
                                        }
                                });
                            }
                        }
                    }
                });
            }
        });
}
        }
