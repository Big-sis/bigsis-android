package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.adapter.ParticipantListAdapter;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.OtherUserProfileFragment;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class ParticipantsListActivity extends BigsisActivity implements OtherUserProfileFragment.OnFragmentInteractionListenerProfile {

    FloatingActionButton fbTrip;
    @BindView(R.id.rvPartcipantList)
    RecyclerView mRecycler;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageButton imgBtBack;
    TextView tvTitle;
    ConstraintLayout transitionContainer;
    private String mCurrentUser;
    private FirebaseAuth mAuth;
    String idTrip;
    String idEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();
        ButterKnife.bind(this);
        setToolBar();
        setUpAdapter();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
         idTrip = extras.getString("ID_TRIP");
         idEvent = extras.getString("ID_EVENT");
        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        if (idTrip != null) {
            curvedBottomNavigationView.setSelectedItemId(R.id.action_trip);
        }
        if (idEvent != null) {
            curvedBottomNavigationView.setSelectedItemId(R.id.action_events);
        }
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });

        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        selectItem(selectedItem, curvedBottomNavigationView);
        fbTrip = findViewById(R.id.fbTrip);
        fbTrip.setOnClickListener(view -> {
            startActivity(new Intent(ParticipantsListActivity.this, TripListActivity.class));
        });
    }

    private void setToolBar() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        tvTitle = transitionContainer.findViewById(R.id.tvTitleToolbar);
        imgBtBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.participants));
        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idTrip != null) {
                    startActivity(new Intent(ParticipantsListActivity.this, TripListActivity.class));                }
                if (idEvent != null) {
                    startActivity(new Intent(ParticipantsListActivity.this, EventListActivity.class));
                }
            }
        });
    }

    private void setUpAdapter() {
        String idTrip;
        String idEvent;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        idTrip = extras.getString("ID_TRIP");
        idEvent = extras.getString("ID_EVENT");
        if (idTrip != null) {
            Query query = FirebaseFirestore.getInstance()
                    .collection("trips").document(idTrip).collection("participants");
            PagedList.Config config = new PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPrefetchDistance(10)
                    .setPageSize(20)
                    .build();
            FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                    .setLifecycleOwner(this)
                    .setQuery(query, config, UserEntity.class)
                    .build();
            ParticipantListAdapter adapter = new ParticipantListAdapter(options, this, mSwipeRefreshLayout);
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRecycler.setAdapter(adapter);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    adapter.refresh();
                }
            });
        }

        if (idEvent != null) {
            Query query = FirebaseFirestore.getInstance()
                    .collection("events").document(idEvent).collection("participants");
            PagedList.Config config = new PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPrefetchDistance(10)
                    .setPageSize(20)
                    .build();
            FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                    .setLifecycleOwner(this)
                    .setQuery(query, config, UserEntity.class)
                    .build();
            ParticipantListAdapter adapter = new ParticipantListAdapter(options, this, mSwipeRefreshLayout);
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRecycler.setAdapter(adapter);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    adapter.refresh();
                }
            });
        }
    }

    @Override
    public void onFragmentInteractionOtherProfile() {
        onBackPressed();
    }
}
