package fr.bigsis.android.helpers;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;

public class FirestoreHelper {

    public static void addChat(String organism, String idGroup, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism)
                .document("AllCampus")
                .collection("AllChatGroups").document(idGroup)
                .collection("Chats")
                .add(object);
    }

    public static void setEvent(CollectionReference collectionReference, String eventId, String subCollection, String idTwo, Object object) {
        collectionReference.document(eventId).collection(subCollection)
                .document(idTwo)
                .set(object, SetOptions.merge());
    }

    public static void deleteUserFromCampus(String organism, String campusUser, String idUser) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism)
                .document("AllCampus")
                .collection("AllCampus").document(campusUser)
                .collection("Users")
                .document(idUser).delete();
    }

    public static void deleteUser(String idUser) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        DocumentReference docRefUser = mFirestore.collection("USERS").document(idUser);

        deleteCollectionFromDoc(docRefUser, "ChatGroup");
        deleteCollectionFromDoc(docRefUser, "EventList");
        deleteCollectionFromDoc(docRefUser, "Friends");
        deleteCollectionFromDoc(docRefUser, "RequestSent");
        deleteCollectionFromDoc(docRefUser, "StaffOf");
        deleteCollectionFromDoc(docRefUser, "TripList");
        deleteCollectionFromDoc(docRefUser, "RequestReceived");
        docRefUser.delete();
    }

    public static void setStatusUser(String organism, String idGroup, String currentId, Boolean isOnline) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism)
                .document("AllCampus")
                .collection("AllChatGroups").document(idGroup)
                .collection("Participants")
                .document(currentId)
                .update("online", isOnline);
    }

    public static void updateData(String principalCollection, String id, String subCollection, String id2, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(principalCollection)
                .document(id)
                .collection(subCollection)
                .document(id2)
                .set(object);
    }

    public static void getImageProfile(String organism, String id, Context context,
                                       ImageView imageView) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllEvents")
                .document(id).collection("Participants")
                .whereEqualTo("creator", false).limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageProfileUrl = document.getString("imageProfileUrl");
                                if (imageProfileUrl != null) {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageProfileUrl);
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Uri downloadUrl = uri;
                                            String urlImage = downloadUrl.toString();
                                            Glide.with(context)
                                                    .load(urlImage)
                                                    .into(imageView);
                                        }
                                    });
                                } else {
                                    Glide.with(context)
                                            .load(R.drawable.ic_profile)
                                            .into(imageView);
                                }
                            }
                        }
                    }
                });
    }

    public static void getStorage(String image, Context context, ImageView imageView) {
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(image);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Uri downloadUrl = uri;
                String urlImage = downloadUrl.toString();
                Glide.with(context)
                        .asBitmap()
                        .load(urlImage)
                        .into(imageView);
            }
        });
    }

    public static void getCountOfParticipants(String organism, String id,
                                              TextView textview, ImageView imageView) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllEvents")
                .document(id).collection("Participants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            textview.setText("+" + (count - 2));
                            if (count < 2) {
                                imageView.setVisibility(View.GONE);
                                textview.setText("...");
                            }
                        }
                    }
                });
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

    public static void compareForFriends(String friendsOrRequestRecevied) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idUser = document.getId().toString();
                        String organism = document.getString("organism");
                        String imageProfileUrlUpdated = document.getString("imageProfileUrl");
                        mFirestore.collection("USERS")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String idCollection = document.getId().toString();

                                        CollectionReference collectionReference = mFirestore.collection("USERS").document(idCollection)
                                                .collection(friendsOrRequestRecevied);
                                        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String idForUpdateImage = document.getId();
                                                        String imageProfileUrl = document.getString("imageProfileUrl");
                                                        if (idUser.equals(idForUpdateImage) && (!imageProfileUrlUpdated.equals(imageProfileUrl))) {
                                                            collectionReference.document(idForUpdateImage).update("imageProfileUrl", imageProfileUrlUpdated);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public static void deleteImageFromStorage(String idUserTodelete) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(idUserTodelete).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String image = documentSnapshot.getString("imageProfileUrl");
                if (image != null) {
                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
                    photoRef.delete();
                }
            }
        });
    }

    public static void deleteUserFromDbUsers(String idUserTodelete, String subCollectionUsers) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String idUser = document.getId();
                        mFirestore.collection("USERS").document(idUser).collection(subCollectionUsers)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String idFriend = document.getId();
                                        if (idFriend.equals(idUserTodelete)) {
                                            mFirestore.collection("USERS").document(idUser).collection("Friends")
                                                    .document(idFriend).delete();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public static void deleteUserFromOrganism(String organism, String idUserTodelete) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllUsers")
                .document(idUserTodelete).delete();
    }

    public static void deleteUserFromAll(String organism, String idUserTodelete) {
        compareForDelete(organism, idUserTodelete, "AllTrips", "Creator");
        compareForDelete(organism, idUserTodelete, "AllTrips", "Participants");
        compareForDelete(organism, idUserTodelete, "AllEvents", "Creator");
        compareForDelete(organism, idUserTodelete, "AllEvents", "Participants");
        compareForDelete(organism, idUserTodelete, "AllEvents", "StaffMembers");
        compareForDelete(organism, idUserTodelete, "AllChatGroups", "StaffMembers");
        compareForDelete(organism, idUserTodelete, "AllChatGroups", "Participants");
        compareForDelete(organism, idUserTodelete, "AllChatGroups", "Creator");

    }

    public static void compareForDelete(String organism, String idUserTodelete, String subcollectionName, String subcollectionForDelete) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection(subcollectionName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idSubcollection = document.getId();
                        mFirestore.collection(organism).document("AllCampus")
                                .collection(subcollectionName).document(idSubcollection).collection(subcollectionForDelete).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String id = document.getId();
                                        if (id.equals(idUserTodelete)) {
                                            mFirestore.collection(organism).document("AllCampus")
                                                    .collection(subcollectionName).document(idSubcollection).collection(subcollectionForDelete)
                                                    .document(idUserTodelete).delete();
                                        }
                                    }
                                }
                            }
                        });


                    }
                }
            }
        });
    }

    public static void compareForParticipants(String tripOrGroupOrEvent, String participantOrCreator) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idUser = document.getId().toString();
                        String organism = document.getString("organism");
                        String imageProfileUrlUpdated = document.getString("imageProfileUrl");
                        mFirestore.collection(organism).document("AllCampus").collection(tripOrGroupOrEvent)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String idCollection = document.getId().toString();

                                        CollectionReference collectionReference = mFirestore.collection(organism).document("AllCampus")
                                                .collection(tripOrGroupOrEvent)
                                                .document(idCollection).collection(participantOrCreator);
                                        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String idForUpdateImage = document.getId();
                                                        String imageProfileUrl = document.getString("imageProfileUrl");
                                                        if (imageProfileUrl == null && idUser.equals(idForUpdateImage) && imageProfileUrlUpdated != null) {
                                                            collectionReference.document(idForUpdateImage).update("imageProfileUrl", imageProfileUrlUpdated);
                                                        }
                                                        if (imageProfileUrl != null && idUser.equals(idForUpdateImage) && (!imageProfileUrlUpdated.equals(imageProfileUrl))) {
                                                            collectionReference.document(idForUpdateImage).update("imageProfileUrl", imageProfileUrlUpdated);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public static void updateImage(String idUser, String image) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String campusName = documentSnapshot.getString("groupCampus");
                String organism = documentSnapshot.getString("organism");
                String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");

                mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                        .document(campusName).collection("Users").document(idUser).update("imageProfileUrl", image).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

            }
        });
    }

    public static void setDataUserInCampus(String userID) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").
                document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Boolean admin = documentSnapshot.getBoolean("admin");
                String description = documentSnapshot.getString("description");
                String firstname = documentSnapshot.getString("firstname");
                String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                String lastname = documentSnapshot.getString("lastname");
                String organism = documentSnapshot.getString("organism");
                String username = documentSnapshot.getString("username");
                String groupCampus = documentSnapshot.getString("groupCampus");
                String lastnameAndFirstname = lastname + " " + firstname;
                UserEntity userEntity = new UserEntity(username, description, imageProfileUrl, firstname, lastname, admin, groupCampus, organism, lastnameAndFirstname);
                mFirestore.collection(organism).document("AllCampus").collection("AllCampus").document(groupCampus)
                        .collection("Users").document(userID).set(userEntity, SetOptions.merge());
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

    public static void verifyIfAdmin(String userId) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Boolean admin = documentSnapshot.getBoolean("admin");
                String description = documentSnapshot.getString("description");
                String firstname = documentSnapshot.getString("firstname");
                String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                String lastname = documentSnapshot.getString("lastname");
                String organism = documentSnapshot.getString("organism");
                String username = documentSnapshot.getString("username");
                String groupCampus = documentSnapshot.getString("groupCampus");
                String lastnameAndFirstname = lastname + " " + firstname;
                UserEntity userEntity = new UserEntity(username, description, imageProfileUrl, firstname, lastname, admin, groupCampus, organism, lastnameAndFirstname);


            }
        });
    }

    private void deleteAlertAfterTime(String userId) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Boolean admin = documentSnapshot.getBoolean("admin");
                String description = documentSnapshot.getString("description");
                String firstname = documentSnapshot.getString("firstname");
                String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                String lastname = documentSnapshot.getString("lastname");
                String organism = documentSnapshot.getString("organism");
                String username = documentSnapshot.getString("username");
                String groupCampus = documentSnapshot.getString("groupCampus");
                String lastnameAndFirstname = lastname + " " + firstname;
                UserEntity userEntity = new UserEntity(username, description, imageProfileUrl, firstname, lastname, admin, groupCampus, organism, lastnameAndFirstname);

                if (admin) {
                    mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                            .document(groupCampus).collection("StaffMembers").document(userId)
                            .set(userEntity);
                }
            }
        });
    }

    public static void deleteTrip(String organism, String campusName) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllTrips")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        Date today = calendar.getTime();
                        Date dateTrip = document.getDate("date");
                        if (dateTrip.before(today)) {
                            String id = document.getId();
                            DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus")
                                    .collection("AllTrips").document(id);
                            documentReference.collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idParticipant = document.getId();
                                            documentReference.collection("Participants").document(idParticipant).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.collection("Creator").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idCreator = document.getId();
                                            documentReference.collection("Creator").document(idCreator).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.delete();
                        }
                    }
                }
            }
        });
    }

    public static void deleteEvent(String organism) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllEvents")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        Date today = calendar.getTime();
                        Date dateTrip = document.getDate("dateEnd");
                        if (dateTrip.before(today)) {
                            String id = document.getId();
                            DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus")
                                    .collection("AllEvents").document(id);
                            documentReference.collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idParticipant = document.getId();
                                            documentReference.collection("Participants").document(idParticipant).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.collection("Creator").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idCreator = document.getId();
                                            documentReference.collection("Creator").document(idCreator).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.collection("StaffMembers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idCreator = document.getId();
                                            documentReference.collection("Creator").document(idCreator).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.delete();
                        }
                    }
                }
            }
        });
    }

    public static void deleteTripFromCampus(String organism, String campusName) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                .document(campusName).collection("Trips").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        Date today = calendar.getTime();
                        Date dateTrip = document.getDate("date");
                        if (dateTrip.before(today)) {
                            String id = document.getId();
                            DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus")
                                    .collection("AllCampus").document(campusName).collection("Trips").document(id);
                            documentReference.collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idParticipant = document.getId();
                                            documentReference.collection("Participants").document(idParticipant).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.collection("Creator").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idCreator = document.getId();
                                            documentReference.collection("Creator").document(idCreator).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.delete();
                        }
                    }
                }
            }
        });
    }

    public static void deleteEventFromCampus(String organism, String campusName, String userId) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                .document(campusName).collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        Date today = calendar.getTime();
                        Date dateEnd = document.getDate("dateEnd");
                        if (dateEnd.before(today)) {
                            String id = document.getId();
                            mFirestore.collection("USERS").document(userId).collection("ParticipateToEvents").document(id).delete();
                            DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus")
                                    .collection("AllCampus").document(campusName).collection("Events").document(id);
                            documentReference.collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idParticipant = document.getId();
                                            documentReference.collection("Participants").document(idParticipant).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.collection("Creator").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idCreator = document.getId();
                                            documentReference.collection("Creator").document(idCreator).delete();
                                        }
                                    }
                                }
                            });
                            documentReference.delete();
                        }
                    }
                }
            }
        });
    }
}
