package fr.bigsis.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.adapter.TripListAdapter;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.fragment.SearchMenuFragment;
import fr.bigsis.android.viewModel.SearchMenuViewModel;

public class TripListActivity extends AppCompatActivity implements SearchMenuFragment.OnFragmentInteractionListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tripRef = db.collection("trips");
    private TripListAdapter adapter;
    private FrameLayout frameLayout;
    private SearchMenuViewModel viewModel;
   // Button btSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

            tripsRecyclerView();

        openAddTrips();
        frameLayout = findViewById(R.id.fragment_container);
        final Button btSearch = findViewById(R.id.btSearchTrip);
        viewModel = ViewModelProviders.of(this).get(SearchMenuViewModel.class);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment();
            }
        });
    }

    public void openFragment() {
        SearchMenuFragment fragment = SearchMenuFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container, fragment, "SEARCH_MENU_FRAGMENT")
                .commit();
        if(viewModel.getText().toString().length()>0) {
            tripsFiltered();
        }
    }

    @Override
    public void onFragmentInteraction(String fromLocation) {
        onBackPressed();
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

    private void tripsFiltered() {

        Query query = tripRef.whereEqualTo("from", viewModel.getText());
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
