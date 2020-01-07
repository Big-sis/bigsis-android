package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.adapter.TripListAdapter;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.fragment.AddTripFragment;
import fr.bigsis.android.fragment.ChooseParticipantFragment;
import fr.bigsis.android.fragment.SearchMenuFragment;
import fr.bigsis.android.fragment.ToolBarFragment;
import fr.bigsis.android.helpers.FirestoreHelper;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;
import fr.bigsis.android.viewModel.SearchMenuViewModel;

import static fr.bigsis.android.helpers.FirestoreHelper.deleteTrip;


public class TripListActivity extends BigsisActivity implements SearchMenuFragment.OnFragmentInteractionListener, AddTripFragment.OnFragmentInteractionListener,
        ToolBarFragment.OnFragmentInteractionListener, ChooseParticipantFragment.OnFragmentInteractionListener {

    private static final String TAG = "TripListActivity";
    SearchMenuFragment fragmentOpen = SearchMenuFragment.newInstance();
    AddTripFragment fragmentAdd = AddTripFragment.newInstance();
    ConstraintLayout transitionContainer;
    ImageButton imbtSearch, imBtCancel, imBtAdd;
    TextView tvTitleToolbar;
    @BindView(R.id.paging_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    String id;
    private FrameLayout frameLayout;
    private SearchMenuViewModel viewModel;
    private CollectionReference mItemsCollection;
    private FirebaseFirestore mFirestore;
    private String userId;
    private FirebaseAuth mAuth;
    private FloatingActionButton btMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        viewModel = ViewModelProviders.of(this).get(SearchMenuViewModel.class);

        ButterKnife.bind(this);
        FirestoreHelper.compareForParticipants("AllTrips", "Participants");
        FirestoreHelper.compareForParticipants("AllTrips", "Creator");
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imbtSearch = transitionContainer.findViewById(R.id.imBt_search_frag);
        imBtAdd = transitionContainer.findViewById(R.id.imBt_add_frag);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        mFirestore = FirebaseFirestore.getInstance();
        mItemsCollection = mFirestore.collection("trips");
        btMap = findViewById(R.id.imageBtMap);
        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TripListActivity.this, MapsActivity.class));
            }
        });
        setUpAdapter();
        setToolBar();

        frameLayout = findViewById(R.id.fragment_container);

        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        curvedBottomNavigationView.setSelectedItemId(R.id.action_trip);
        curvedBottomNavigationView.setItemIconTintList(null);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });
        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_trip);
        item.setIcon(R.drawable.ic_trip_selected);
        selectItem(selectedItem, curvedBottomNavigationView);

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
    public void onFragmentInteraction() {
        onBackPressed();
    }

    private void setToolBar() {

        tvTitleToolbar.setText(R.string.trips);
        imbtSearch.setVisibility(View.VISIBLE);
        imBtAdd.setVisibility(View.VISIBLE);

        imBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTrip();
                imBtAdd.setVisibility(View.GONE);
                imBtCancel.setVisibility(View.VISIBLE);
                imbtSearch.setVisibility(View.GONE);
                tvTitleToolbar.setText(R.string.add_a_trip);
            }
        });

        imbtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment();
                imBtCancel.setVisibility(View.VISIBLE);
                imbtSearch.setVisibility(View.GONE);
                imBtAdd.setVisibility(View.GONE);
                tvTitleToolbar.setText(R.string.search_trip);
            }
        });

        imBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imBtCancel.setVisibility(View.GONE);
                imbtSearch.setVisibility(View.VISIBLE);
                imBtAdd.setVisibility(View.VISIBLE);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addMenu = manager.findFragmentByTag("ADD_MENU_FRAGMENT");
                Fragment searchMenu = manager.findFragmentByTag("SEARCH_MENU_FRAGMENT");

                if (searchMenu != null) {
                    ft.remove(searchMenu).commitAllowingStateLoss();
                }
                if (fragmentOpen.isAdded()) {
                    onFragmentInteraction();
                }
                if (fragmentAdd.isAdded()) {
                    ft.remove(fragmentAdd).commitAllowingStateLoss();
                    onBackPressed();
                }
                tvTitleToolbar.setText(R.string.trips);
                KeyboardHelper.CloseKeyboard(TripListActivity.this, view);
            }
        });
    }

    @Override
    public void onFragmentInteractionTool() {
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

    private void setUpAdapter() {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        viewModel = ViewModelProviders.of(TripListActivity.this).get(SearchMenuViewModel.class);
        viewModel.getDateTrip().observe(TripListActivity.this, new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                mFirestore.collection("USERS").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nameCampus = documentSnapshot.getString("groupCampus");
                        String organism = documentSnapshot.getString("organism");

//In which you need put here
                        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM", Locale.ENGLISH);
                        String dateViewModel = dateFormat.format(date);

                        Query query = FirebaseFirestore.getInstance().collection(organism)
                                .document("AllCampus")
                                .collection("AllCampus")
                                .document(nameCampus)
                                .collection("Trips")
                                .orderBy("dateToString")
                                .startAt(dateViewModel)
                                .endAt(dateViewModel + "\uf8ff");
                              //  .whereGreaterThan("dateToString", dateViewModel);

                        PagedList.Config config = new PagedList.Config.Builder()
                                .setEnablePlaceholders(false)
                                .setPrefetchDistance(10)
                                .setPageSize(20)
                                .build();
                        FirestorePagingOptions<TripEntity> options = new FirestorePagingOptions.Builder<TripEntity>()
                                .setLifecycleOwner(TripListActivity.this)
                                .setQuery(query, config, TripEntity.class)
                                .build();
                        TripListAdapter adapter = new TripListAdapter(options, TripListActivity.this, mSwipeRefreshLayout, nameCampus, organism, fragmentAdd);
                        mRecycler.setLayoutManager(new LinearLayoutManager(TripListActivity.this));
                        mRecycler.setAdapter(adapter);
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        Fragment searchMenu = manager.findFragmentByTag("SEARCH_MENU_FRAGMENT");
                        if(searchMenu != null) {
                            ft.remove(searchMenu).commitAllowingStateLoss();
                        }
                        imBtCancel.setVisibility(View.GONE);
                        imbtSearch.setVisibility(View.VISIBLE);
                        imBtAdd.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                adapter.refresh();
                            }
                        });
                    }
                });
            }
        });
            mFirestore.collection("USERS").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String nameCampus = documentSnapshot.getString("groupCampus");
                    String organism = documentSnapshot.getString("organism");
                    PagedList.Config config = new PagedList.Config.Builder()
                            .setEnablePlaceholders(false)
                            .setPrefetchDistance(10)
                            .setPageSize(20)
                            .build();
                    deleteTrip(organism, nameCampus);
                    Query query = FirebaseFirestore.getInstance().collection(organism).document("AllCampus")
                            .collection("AllCampus").document(nameCampus)
                            .collection("Trips").orderBy("date");
                    FirestorePagingOptions<TripEntity> options = new FirestorePagingOptions.Builder<TripEntity>()
                            .setLifecycleOwner(TripListActivity.this)
                            .setQuery(query, config, TripEntity.class)
                            .build();

                    TripListAdapter adapter = new TripListAdapter(options, TripListActivity.this, mSwipeRefreshLayout, nameCampus, organism, fragmentAdd);

                    mRecycler.setLayoutManager(new LinearLayoutManager(TripListActivity.this));
                    mRecycler.setAdapter(adapter);
                    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            adapter.refresh();
                        }
                    });
                }
            });
}


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
