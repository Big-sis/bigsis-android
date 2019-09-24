package fr.bigsis.android.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.model.TripModel;

public class TripRepository {

    private static TripRepository instance;
    private ArrayList<TripEntity> arrayList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "TripRepository";

    public static TripRepository getInstance(Context context){
        if(instance == null) {
            instance = new TripRepository();
        }
        return instance;
    }

    public MutableLiveData<List<TripEntity>> getNameDeparture() {

        loadSearch();
        MutableLiveData<List<TripEntity>> data = new MutableLiveData<>();
        data.setValue(arrayList);
        return data;
    }

    private void loadSearch() {
        db.collection("trips").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : list) {
                        arrayList.add(documentSnapshot.toObject(TripEntity.class));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure : ");
            }
        });
    }
}
