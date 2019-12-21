package fr.bigsis.android.helpers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;


public class AddTripHelper {

    public static void setDataInCampus(String organism, String groupCampusName, Object object, Object objectGroup, Object objectUser, String tripId, String userId) {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference tripReference = mFirestore
                .collection(organism).document("AllCampus").collection("AllCampus")
                .document(groupCampusName)
                .collection("Trips");
        CollectionReference userListsRef = mFirestore.collection("USERS").document(userId)
                .collection("TripList");
        DocumentReference userDocumentRef = mFirestore.collection("USERS").document(userId);
        tripReference.document(tripId).set(object, SetOptions.merge());
        FirestoreDBHelper.setDataInOneCampus(organism, groupCampusName, "Trips", tripId, object);
        FirestoreDBHelper.setDataInOneCampus(organism, groupCampusName, "ChatGroup", tripId, objectGroup);
        tripReference.document(tripId).collection("Creator").document(userId).set(objectUser, SetOptions.merge());
        tripReference.document(tripId).collection("Participants").document(userId).set(objectUser, SetOptions.merge());
        CollectionReference groupChatRef = mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                .document(groupCampusName).collection("ChatGroup");
        groupChatRef.document(tripId).set(object);
        groupChatRef.document(tripId).collection("Participants")
                .document(userId)
                .set(objectUser);
        groupChatRef.document(tripId).set(object);
        groupChatRef.document(tripId).collection("Creator")
                .document(userId)
                .set(objectUser);
    }

    public static void updateDataInCampus(String organism, String groupCampusName
            , String tripId, Map<String, Object> hashMapTrip, Map<String, Object> hashMapGroup, String userId) {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference tripReference = mFirestore
                .collection(organism).document("AllCampus").collection("AllCampus")
                .document(groupCampusName)
                .collection("Trips");

        DocumentReference userDocumentRef = mFirestore.collection("USERS").document(userId);
        tripReference.document(tripId).update(hashMapTrip);
        FirestoreDBHelper.updateDataInOneCampus(organism, groupCampusName, "Trips", tripId, hashMapTrip);
        FirestoreDBHelper.updateDataInOneCampus(organism, groupCampusName, "ChatGroup", tripId, hashMapGroup);

        CollectionReference groupChatRef = mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                .document(groupCampusName).collection("ChatGroup");
        groupChatRef.document(tripId).update(hashMapTrip);

    }

    public static void deleteFromDB(String organism, String idTrip, String idUser) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentRefAllTrips = mFirestore.collection(organism).document("AllCampus")
                .collection("AllTrips").document(idTrip);
        DocumentReference documentRefAll = mFirestore.collection(organism).document("AllCampus")
                .collection("AllChatGroups").document(idTrip);
        deleteCollectionFromDoc(documentRefAllTrips, "Creator");
        deleteCollectionFromDoc(documentRefAllTrips, "Participants");
        documentRefAllTrips.delete();
        deleteCollectionFromDoc(documentRefAll, "Creator");
        deleteCollectionFromDoc(documentRefAll, "Participants");
        documentRefAll.delete();
        mFirestore.collection(organism).document("AllCampus").collection("AllCampus").
                get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mFirestore.collection("USERS").document(idUser).collection("ChatGroup").document(idTrip).delete();
                            mFirestore.collection("USERS").document(idUser).collection("TripList").document(idTrip).delete();

                            mFirestore.collection(organism).document("AllCampus").collection("AllTrips").document(idTrip).delete();
                            mFirestore.collection(organism).document("AllCampus").collection("AllChatGroups").document(idTrip).delete();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String groupName = document.getData().get("groupName").toString();
                                DocumentReference docTripRefInCampus = mFirestore.collection(organism).document("AllCampus")
                                        .collection("AllCampus").document(groupName).collection("Trips")
                                        .document(idTrip);
                                DocumentReference docGroupChatInCampus = mFirestore.collection(organism).document("AllCampus")
                                        .collection("AllCampus").document(groupName).collection("ChatGroup")
                                        .document(idTrip);
                                deleteCollectionFromDoc(docTripRefInCampus, "Creator");
                                deleteCollectionFromDoc(docTripRefInCampus, "Participants");
                                docTripRefInCampus.delete();
                                deleteCollectionFromDoc(docGroupChatInCampus, "Participants");
                                deleteCollectionFromDoc(docGroupChatInCampus, "Creator");
                                docGroupChatInCampus.delete();
                            }
                        }
                    }
                });
    }

    private static void deleteCollectionFromDoc(DocumentReference docRef, String collection) {
        docRef.collection(collection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        docRef.collection(collection).document(id).delete();
                    }
                }
            }
        });
    }
}
