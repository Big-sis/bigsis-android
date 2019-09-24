package fr.bigsis.android.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.repository.TripRepository;

public class SearchMenuViewModel extends ViewModel {
    private MutableLiveData<String> departureName;
    private TripRepository repository = new TripRepository();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    public SearchMenuViewModel() {
        departureName = new MutableLiveData<>();
    }

    public String getDepartureName() {
        return this.departureName != null ? this.departureName.getValue() : "";
    }

    public void setDepartureName(String name) {
        this.departureName.setValue(name);
    }

    public void loadSearch() {
        db.collection("trips").whereEqualTo("from", departureName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    TripEntity tripEntity = new TripEntity(documentSnapshot.getString("from"), documentSnapshot.getString("to"), documentSnapshot.getDate("date"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
