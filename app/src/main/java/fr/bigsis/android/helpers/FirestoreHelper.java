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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fr.bigsis.android.entity.UserEntity;

public class FirestoreHelper {

    public static void addData(String principalCollection, String id, String subCollection, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(principalCollection)
                .document(id)
                .collection(subCollection)
                .add(object);
    }

    public static void setData(String principalCollection, String id, String subCollection, String idTwo, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(principalCollection)
                .document(id)
                .collection(subCollection)
                .document(idTwo)
                .set(object, SetOptions.merge());
    }

    public static void setStatusUser(String idGroup, String currentId, Boolean isOnline) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("GroupChat")
                .document(idGroup)
                .collection("participants")
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

    public static void getImageProfile(String principalCollection, String id, String subCollection,
                                       Boolean isCreator, String fieldfForImage, Context context,
                                       ImageView imageView) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(principalCollection).document(id).collection(subCollection)
                .limit(1).whereEqualTo("creator", isCreator).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageProfileUrl = document.getData().get(fieldfForImage).toString();
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

    public static void getCountOfParticipants(String principalCollection, String id, String subCollection,
                                              TextView textview, ImageView imageView) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(principalCollection).document(id).collection(subCollection)
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

    public static void updateUserProfile(String campusName, String id, String collection2,
                                         String idTwo, String subCollecion,  String field ) {
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("TBS").document("AllCampus").collection("AllCampus")
                .document(campusName).collection("users").document(id).get()
      .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageProfileUrlUpdated = documentSnapshot.getString("imageProfileUrl");
                DocumentReference documentReference = mFirestore.collection("TBS")
                        .document("AllCampus").collection("AllCampus")
                        .document(campusName).collection(collection2).document(idTwo)
                        .collection(subCollecion).document(id);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageProfileUrl = documentSnapshot.getString(field);
                        if(!imageProfileUrlUpdated.equals(imageProfileUrl)){
                            documentReference.update(field,imageProfileUrlUpdated);
                        }
                    }
                });
                mFirestore.collection("TBS")
                        .document("AllCampus")
                        .collection(collection2).document(idTwo)
                        .collection("participants").document(id)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageProfileUrl = documentSnapshot.getString(field);
                        if(!imageProfileUrlUpdated.equals(imageProfileUrl)){
                            documentReference.update(field,imageProfileUrlUpdated);
                        }
                    }
                });
            }
        });
    }

    public static void compare(String collection2,
                              String idTwo, String subCollecion, String field) {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idForimage = document.getId().toString();
                        String campusName = document.getString("groupCampus");
                        mFirestore.collection(collection2).document(idTwo)
                                .collection(subCollecion)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String id = document.getId().toString();
                                        if(idForimage.equals(id)) {
                                            updateUserProfile( campusName, idForimage, collection2, idTwo,subCollecion, field);
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
                UserEntity userEntity = new UserEntity(username, description, imageProfileUrl, firstname, lastname, admin, groupCampus, organism);
                mFirestore.collection(organism).document("AllCampus").collection("AllCampus").document(groupCampus)
                        .collection("Users").document(userID).set(userEntity, SetOptions.merge());
            }
        });
    }
}
