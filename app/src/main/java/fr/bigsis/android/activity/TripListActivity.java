package fr.bigsis.android.activity;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.adapter.TripListAdapter;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.fragment.AddTripFragment;
import fr.bigsis.android.fragment.SearchMenuFragment;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;
import fr.bigsis.android.viewModel.SearchMenuViewModel;


public class TripListActivity extends AppCompatActivity implements SearchMenuFragment.OnFragmentInteractionListener, AddTripFragment.OnFragmentInteractionListener {

    private static final String TAG = "TripListActivity";

    SearchMenuFragment fragmentOpen = SearchMenuFragment.newInstance();
    AddTripFragment fragmentAdd = AddTripFragment.newInstance();
    Toolbar toolbar;
    private FrameLayout frameLayout;
    private SearchMenuViewModel viewModel;
    private RecyclerView rvList;
    private CollectionReference mItemsCollection;
    private FirebaseFirestore mFirestore;
    FirestorePagingAdapter<TripEntity, ItemViewHolder> adapter;

    @BindView(R.id.paging_recycler)
    RecyclerView mRecycler;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SearchMenuViewModel.class);

        setContentView(R.layout.activity_trip_list);
        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();
        mItemsCollection = mFirestore.collection("trips");

        setUpAdapter();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView tvTitleToolBar = findViewById(R.id.tvTitleToolBar);
        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        tvTitleToolBar.setText(R.string.trips);

        frameLayout = findViewById(R.id.fragment_container);
        final ImageButton imbtSearch = findViewById(R.id.imBt_ic_search);
        final ImageButton imBtCancel = findViewById(R.id.ic_cancel);
        final ImageButton imBtAdd = findViewById(R.id.ic_add);

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
                    onFragmentInteraction();
                }
                if (fragmentAdd.isAdded()) {
                    onFragmentInteractionAdd();
                }
                imBtCancel.setVisibility(View.GONE);
                imbtSearch.setVisibility(View.VISIBLE);
                imBtAdd.setVisibility(View.VISIBLE);
                tvTitleToolBar.setText(R.string.trips);

                KeyboardHelper.CloseKeyboard(TripListActivity.this, view);
            }
        });

        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });
        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        selectItem(selectedItem, curvedBottomNavigationView);

        viewModel.getDeparture().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TripListActivity.this.setUpAdapter();
            }
        });

        viewModel.getArrival().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TripListActivity.this.setUpAdapter();
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
    public void onFragmentInteraction() {
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
        Query baseQuery;

        if (!viewModel.getDeparture().getValue().equals("") || !viewModel.getArrival().getValue().equals("")) {
            baseQuery = mItemsCollection.whereArrayContains("filters", viewModel.getDeparture().getValue() + "-" + viewModel.getArrival().getValue());
        } else {
            baseQuery = mItemsCollection.orderBy("date", Query.Direction.DESCENDING);
        }

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<TripEntity> options = new FirestorePagingOptions.Builder<TripEntity>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, TripEntity.class)
                .build();

        adapter = new FirestorePagingAdapter<TripEntity, ItemViewHolder>(options) {
                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.trip_list_item, parent, false);
                        return new ItemViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder,
                                                    int position,
                                                    @NonNull TripEntity model) {
                        holder.bind(model);
                    }

                    @Override
                    protected void onLoadingStateChanged(@NonNull LoadingState state) {
                        switch (state) {
                            case LOADING_INITIAL:
                            case LOADING_MORE:
                                mSwipeRefreshLayout.setRefreshing(true);
                                break;
                            case LOADED:
                                mSwipeRefreshLayout.setRefreshing(false);
                                break;
                            case FINISHED:
                                mSwipeRefreshLayout.setRefreshing(false);
                                showToast("Reached end of data set.");
                                break;
                            case ERROR:
                                showToast("An error occurred.");
                                retry();
                                break;
                        }
                    }

                    @Override
                    protected void onError(@NonNull Exception e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, e.getMessage(), e);
                    }
                };

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });
    }

    private boolean selectItem(@NonNull MenuItem item, CurvedBottomNavigationView curvedBottomNavigationView) {
        switch (item.getItemId()) {
            case R.id.action_user_profile:
                Toast.makeText(TripListActivity.this, "hello", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_message:
                Toast.makeText(TripListActivity.this, "ddd", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_events:
                Toast.makeText(TripListActivity.this, "ii", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_route:
                Toast.makeText(TripListActivity.this, "hh", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTripsFrom)
        TextView mTextFrom;

        @BindView(R.id.tvTripsTo)
        TextView mTextTo;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(@NonNull TripEntity item) {
            mTextFrom.setText(item.getFrom());
            mTextTo.setText(item.getTo());
        }
    }

    private void showToast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
