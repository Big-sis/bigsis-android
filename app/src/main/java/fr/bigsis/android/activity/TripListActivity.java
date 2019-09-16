package fr.bigsis.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.adapter.TripListAdapter;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.fragment.AddTripFragment;
import fr.bigsis.android.fragment.SearchMenuFragment;
import fr.bigsis.android.viewModel.SearchMenuViewModel;

public class TripListActivity extends AppCompatActivity implements SearchMenuFragment.OnFragmentInteractionListener, AddTripFragment.OnFragmentInteractionListener {
    SearchMenuFragment fragmentOpen = SearchMenuFragment.newInstance();
    AddTripFragment fragmentAdd = AddTripFragment.newInstance();
    Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tripRef = db.collection("trips");
    private TripListAdapter adapter;
    private FrameLayout frameLayout;
    private SearchMenuViewModel viewModel;
    private RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView tvTitleToolBar = findViewById(R.id.tvTitleToolBar);
        tvTitleToolBar.setText(R.string.trips);

        tripsRecyclerView();
        frameLayout = findViewById(R.id.fragment_container);
        final ImageButton imbtSearch = findViewById(R.id.imBt_ic_search);
        final ImageButton imBtCancel = findViewById(R.id.ic_cancel);
        final ImageButton imBtAdd = findViewById(R.id.ic_add);
        viewModel = ViewModelProviders.of(this).get(SearchMenuViewModel.class);
        imBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imBtCancel.setVisibility(View.VISIBLE);
                imBtAdd.setVisibility(View.GONE);
                imbtSearch.setVisibility(View.GONE);
                tvTitleToolBar.setText(R.string.add_a_trip);
                addTrip();
            }
        });

        imbtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment();
                imBtCancel.setVisibility(View.VISIBLE);
                imbtSearch.setVisibility(View.GONE);
                imBtAdd.setVisibility(View.GONE);
                tvTitleToolBar.setText(R.string.search_trip);
            }
        });

        imBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragmentOpen.isAdded()) {
                    onFragmentInteraction("hello");
                }
                if (fragmentAdd.isAdded()) {
                    onFragmentInteractionAdd();
                }
                imBtCancel.setVisibility(View.GONE);
                imbtSearch.setVisibility(View.VISIBLE);
                imBtAdd.setVisibility(View.VISIBLE);
                tvTitleToolBar.setText(R.string.trips);
            }
        });
    }

    public void openFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container, fragmentOpen, "SEARCH_MENU_FRAGMENT")
                .commit();
    }

    @Override
    public void onFragmentInteraction(String fromLocation) {
        onBackPressed();
    }

    @Override
    public void onFragmentInteractionAdd() {
        onBackPressed();
    }

    private void addTrip() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container, fragmentAdd, "ADD_MENU_FRAGMENT")
                .commit();
    }

    private void tripsRecyclerView() {
        Query query = tripRef.orderBy("from", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<TripEntity> options = new FirestoreRecyclerOptions.Builder<TripEntity>()
                .setQuery(query, TripEntity.class)
                .build();
        adapter = new TripListAdapter(options);
        rvList = findViewById(R.id.rvListTrips);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);
    }

   /* private void tripsFiltered(String s) {
        adapter.stopListening();
        Query query = tripRef.whereEqualTo("from",s.toLowerCase());
        FirestoreRecyclerOptions<TripEntity> options = new FirestoreRecyclerOptions.Builder<TripEntity>()
                .setQuery(query, TripEntity.class)
                .build();
        adapter = new TripListAdapter(options);
        RecyclerView rvList = findViewById(R.id.rvListTrips);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter();
        adapter.startListening();
    }*/

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
