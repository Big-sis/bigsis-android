package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import fr.bigsis.android.entity.TripEntity;

public class SearchMenuViewModel extends ViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerOptions<TripEntity> trips;
    private MutableLiveData<Date> dateTrip;
    private MutableLiveData<String> arrival;
    private MutableLiveData<Date> date;

    public SearchMenuViewModel() {
        dateTrip = new MutableLiveData<>();
        arrival = new MutableLiveData<>();
    }

    public MutableLiveData<Date> getDateTrip() {
        return this.dateTrip;
    }

    public void setDateTrip (Date dateTrip) {
        this.dateTrip.setValue(dateTrip);
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
