package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

import fr.bigsis.android.entity.TripEntity;

public class SearchMenuViewModel extends ViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerOptions<TripEntity> trips;
    private MutableLiveData<String> departure;
    private MutableLiveData<String> arrival;
    private MutableLiveData<Date> date;


    public SearchMenuViewModel() {
        departure = new MutableLiveData<>();
        departure.setValue("");
        arrival = new MutableLiveData<>();
        arrival.setValue("");
    }

    public MutableLiveData<String> getDeparture() {
        return this.departure;
    }

    public void setDeparture(String name) {
        this.departure.setValue(name);
    }

    public MutableLiveData<String> getArrival() {
        return this.arrival;
    }

    public void setArrival(String name) {
        this.arrival.setValue(name);
    }

    public MutableLiveData<Date> getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date.setValue(date);
    }
}
