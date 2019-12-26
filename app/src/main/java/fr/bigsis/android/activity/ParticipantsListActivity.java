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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    String idTrip;
    String idGroup;
    String idEvent;
    String idStaff;
    FloatingActionButton fbtGoToMapParticipant;
    private String mCurrentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();
        ButterKnife.bind(this);
        setToolBar();
        fbtGoToMapParticipant = findViewById(R.id.fbtGoToMapParticipant);
        fbtGoToMapParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParticipantsListActivity.this, MapsActivity.class));
            }
        });

        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        idTrip = extras.getString("ID_TRIP");
        idGroup = extras.getString("ID_GROUP");
        idEvent = extras.getString("ID_EVENT");
        idStaff = extras.getString("STAFF");

        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        curvedBottomNavigationView.setItemIconTintList(null);

        if (idTrip != null) {
            curvedBottomNavigationView.setSelectedItemId(R.id.action_trip);
            MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_trip);
            item.setIcon(R.drawable.ic_trip_selected);
            setUpAdapterForTrips();
        }

        if (idGroup != null) {
            curvedBottomNavigationView.setSelectedItemId(R.id.action_message);
            MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_message);
            item.setIcon(R.drawable.ic_dialog_selected);
            setUpAdapterForGroupChat();
        }

        if (idEvent != null) {
            curvedBottomNavigationView.setSelectedItemId(R.id.action_message);
            MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_message);
            item.setIcon(R.drawable.ic_event_selected);
            setUpAdapterForEvent();
        }

        if (idStaff != null) {
            tvTitle.setText("Membres du staff");
            curvedBottomNavigationView.setSelectedItemId(R.id.action_message);
            MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_message);
            item.setIcon(R.drawable.ic_event_selected);
            setUPAdapterForStaff();
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
    }

    private void setToolBar() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        transitionContainer = findViewById(R.id.toolbarLayout);
        tvTitle = transitionContainer.findViewById(R.id.tvTitleToolbar);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        tvTitle.setText(getString(R.string.participants));

        imgBtBack.setVisibility(View.VISIBLE);

        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void setUpAdapterForTrips() {
        FirebaseFirestore.getInstance().collection("USERS").document(mCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String organism = documentSnapshot.getString("organism");

                Query query = FirebaseFirestore.getInstance().collection(organism)
                        .document("AllCampus").collection("AllTrips")
                 .document(idTrip).collection("Participants");
                PagedList.Config config = new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPrefetchDistance(10)
                        .setPageSize(20)
                        .build();
                FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                        .setLifecycleOwner(ParticipantsListActivity.this)
                        .setQuery(query, config, UserEntity.class)
                        .build();
                ParticipantListAdapter adapter = new ParticipantListAdapter(options, ParticipantsListActivity.this, mSwipeRefreshLayout);
                mRecycler.setLayoutManager(new LinearLayoutManager(ParticipantsListActivity.this));
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

    private void setUpAdapterForGroupChat() {
        FirebaseFirestore.getInstance().collection("USERS").document(mCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String organism = documentSnapshot.getString("organism");
                Query query = FirebaseFirestore.getInstance().collection(organism).document("AllCampus").collection("AllChatGroups")
                        .document(idGroup).collection("Participants");
                PagedList.Config config = new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPrefetchDistance(10)
                        .setPageSize(20)
                        .build();
                FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                        .setLifecycleOwner(ParticipantsListActivity.this)
                        .setQuery(query, config, UserEntity.class)
                        .build();
                ParticipantListAdapter adapter = new ParticipantListAdapter(options, ParticipantsListActivity.this, mSwipeRefreshLayout);
                mRecycler.setLayoutManager(new LinearLayoutManager(ParticipantsListActivity.this));
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

    private void setUpAdapterForEvent() {
        Query query = FirebaseFirestore.getInstance()
                .collection("events").document(idEvent)
                .collection("participants");
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

    private void setUPAdapterForStaff() {
        Query query = FirebaseFirestore.getInstance()
                .collection("events").document(idStaff)
                .collection("staffMembers");
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

    @Override
    public void onFragmentInteractionOtherProfile() {
        onBackPressed();
    }
}
