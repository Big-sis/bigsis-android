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
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FirestoreDBHelper {

    public static void setDataInOneCampus(String organism, String nameCampus, String nameCollection, String idCollection, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                .document(nameCampus).collection(nameCollection).document(idCollection).set(object, SetOptions.merge());
    }

    public static void setParticipantTo(String organism, String allTripsOrEventsOrGroups, String idDoc, String idUser, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection(allTripsOrEventsOrGroups)
                .document(idDoc).collection("Participants")
                .document(idUser).set(object);
    }

    public static void setParticipantToCampus(String organism, String campusName, String tripsOrEventsOrGroups, String idDoc, String idUser, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        if(campusName.equals("Tous les campus") || campusName.equals("All campus")){
            mFirestore.collection(organism).document("AllCampus")
                    .collection("AllCampus").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            mFirestore.collection(organism).document("AllCampus")
                                    .collection("AllCampus").document(id)
                                    .collection(tripsOrEventsOrGroups).document(idDoc).collection("Participants")
                                    .document(idUser).set(object);
                        }
                    }
                }
            });
        } else {
            mFirestore.collection(organism).document("AllCampus")
                    .collection("AllCampus")
                    .document(campusName)
                    .collection(tripsOrEventsOrGroups)
                    .document(idDoc).collection("Participants")
                    .document(idUser).set(object);
        }
    }


    public static void deleteParticipantFromDatab (String organism, String allTripsOrEventsOrGroups,
                                                                       String idCollection, String idUser) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection(allTripsOrEventsOrGroups)
        .document(idCollection).collection("Participants")
                .document(idUser).delete();
    }


    public static void setData(String principalCollection, String id, String subCollection, String idTwo, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(principalCollection)
                .document(id)
                .collection(subCollection)
                .document(idTwo)
                .set(object, SetOptions.merge());
    }
    public static void deleteFromdb(String principalCollection, String id, String subCollection,
                                    String idTwo) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(principalCollection)
                .document(id)
                .collection(subCollection)
                .document(idTwo)
                .delete();
    }

    public static void deleteParticipantFromCampus(String organism, String campusName, String tripsOrEventsOrGroups, String idDoc, String idUser) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        if(campusName.equals("Tous les campus") || campusName.equals("All campus")){
            mFirestore.collection(organism).document("AllCampus")
                    .collection("AllCampus").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            mFirestore.collection(organism).document("AllCampus")
                                    .collection("AllCampus").document(id)
                                    .collection(tripsOrEventsOrGroups).document(idDoc).collection("Participants")
                                    .document(idUser).delete();
                        }
                    }
                }
            });
        } else {
            mFirestore.collection(organism).document("AllCampus")
                    .collection("AllCampus")
                    .document(campusName)
                    .collection(tripsOrEventsOrGroups)
                    .document(idDoc).collection("Participants")
                    .document(idUser).delete();
        }

    }
    public static void updateDataInOneCampus(String organism, String nameCampus, String nameCollection, String idCollection,
                                             Map<String, Object> hashMap) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                .document(nameCampus).collection(nameCollection).document(idCollection).update(hashMap);
    }
}
