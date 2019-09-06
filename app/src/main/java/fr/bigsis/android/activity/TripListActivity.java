package fr.bigsis.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.adapter.TripListAdapter;
import fr.bigsis.android.model.TripModel;

public class TripListActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tripRef = db.collection("trips");

    private TripListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        tripsRecyclerView();
    }

    private void tripsRecyclerView() {
        Query query = tripRef.orderBy("from", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<TripModel> options = new FirestoreRecyclerOptions.Builder<TripModel>()
                .setQuery(query, TripModel.class)
                .build();
        adapter = new TripListAdapter(options);
        RecyclerView rvList = findViewById(R.id.rvListTrips);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
