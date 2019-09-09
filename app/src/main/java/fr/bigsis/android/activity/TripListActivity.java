package fr.bigsis.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.adapter.TripListAdapter;
import fr.bigsis.android.entity.TripEntity;
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
        openAddTrips();
    }
    private void openAddTrips() {
        FloatingActionButton btAddTrip = findViewById(R.id.fbAddTrip);
        btAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TripListActivity.this, AddTripActivity.class));
            }
        });
    }
    private void tripsRecyclerView() {
        Query query = tripRef.orderBy("from", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<TripEntity> options = new FirestoreRecyclerOptions.Builder<TripEntity>()
                .setQuery(query, TripEntity.class)
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
