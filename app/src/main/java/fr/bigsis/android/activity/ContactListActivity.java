package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.adapter.ContactListAdapter;
import fr.bigsis.android.adapter.RequestListAdapter;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.OtherUserProfileFragment;
import fr.bigsis.android.fragment.SearchContactFragment;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class ContactListActivity extends BigsisActivity implements SearchContactFragment.OnFragmentInteractionContact {

    private static final String TAG = "ContactActivity";
    FloatingActionButton fbTrip;
    ConstraintLayout transitionContainer;
    ImageButton imgBtBack, imBtSearch;
    TextView tvTitle;
    SearchContactFragment fragmentProfile = SearchContactFragment.newInstance();
    CollectionReference mItemsCollection;
    @BindView(R.id.rvContactList)
    RecyclerView mRecyclerContact;
    @BindView(R.id.rvContactListRequest)
    RecyclerView mRecyclerRequest;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    UserEntity user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("users");
    private FirebaseFirestore mFirestore;
    private String mCurrentUser;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        ButterKnife.bind(this);
        setToolBar();
        openFragment();
        setUpAdapterForContacts();
        setUpAdapterForRequests();

        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
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
            startActivity(new Intent(ContactListActivity.this, TripListActivity.class));
        });
    }

    private void setToolBar() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        imBtSearch = transitionContainer.findViewById(R.id.imBt_search_right_frag);
        tvTitle = transitionContainer.findViewById(R.id.tvTitleToolbar);
        imgBtBack.setVisibility(View.VISIBLE);
        imBtSearch.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.contacts);

        imBtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionContainer);
                imBtSearch.setVisibility(View.GONE);
            }
        });

        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactListActivity.this, UserProfileActivity.class));
            }
        });
    }

    public void openFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_contact, fragmentProfile, "PROFILE_USER_FRAGMENT")
                .commit();
    }

    private void setUpAdapterForContacts() {
        Query query = FirebaseFirestore.getInstance()
                .collection("users");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, UserEntity.class)
                .build();

        ContactListAdapter adapter = new ContactListAdapter(options, this, mSwipeRefreshLayout);

        mRecyclerContact.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerContact.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });
    }

    private void setUpAdapterForRequests() {

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = mFirestore.collection("users")
                .document(mCurrentUserId)
                .collection("Request received");

        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .document(mCurrentUserId)
                .collection("Request received");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, UserEntity.class)
                .build();

        RequestListAdapter adapterRequest = new RequestListAdapter(options, this, mSwipeRefreshLayout);

        mRecyclerRequest.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerRequest.setAdapter(adapterRequest);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapterRequest.refresh();
            }
        });
    }

    @Override
    public void onFragmentInteractionContact() {
        onBackPressed();
    }
}
