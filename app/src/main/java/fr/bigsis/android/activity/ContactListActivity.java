package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.SearchContactFragment;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class ContactListActivity extends AppCompatActivity implements SearchContactFragment.OnFragmentInteractionContact {

    private static final String TAG = "ContactActivity";
    FloatingActionButton fbTrip;
    ConstraintLayout transitionContainer;
    ImageButton imgBtBack, imBtSearch;
    TextView tvTitle;
    SearchContactFragment fragmentProfile = SearchContactFragment.newInstance();
    FirestorePagingAdapter<UserEntity, ContactViewHolder> adapter;
    private FirebaseFirestore mFirestore;
    CollectionReference mItemsCollection;


    @BindView(R.id.rvContactList)
    RecyclerView mRecycler;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        ButterKnife.bind(this);
        setToolBar();
        openFragment();
        setUpAdapter();

        mFirestore = FirebaseFirestore.getInstance();
        mItemsCollection = mFirestore.collection("users");
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
        fbTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactListActivity.this, TripListActivity.class));
            }
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

            }
        });
    }

    private boolean selectItem(@NonNull MenuItem item, CurvedBottomNavigationView curvedBottomNavigationView) {
        switch (item.getItemId()) {
            case R.id.action_user_profile:
                startActivity(new Intent(ContactListActivity.this, UserProfileActivity.class));

                return true;
            case R.id.action_message:
                Toast.makeText(ContactListActivity.this, "ddd", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_events:
                Toast.makeText(ContactListActivity.this, "ii", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_route:
                Toast.makeText(ContactListActivity.this, "hh", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    public void openFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_contact, fragmentProfile, "PROFILE_USER_FRAGMENT")
                .commit();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUsernameContact)
        TextView mTextUsername;

        @BindView(R.id.tvPseudoContact)
        TextView mTextPseudo;

        @BindView(R.id.image_profile_contact)
        CircleImageView mImageProfile;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull UserEntity item) {
            mTextUsername.setText(item.getUsername());
            mTextPseudo.setText(item.getPseudonyme());

            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(250, 250);

            Glide.with(mImageProfile.getContext())
                    .asBitmap()
                    .apply(myOptions)
                    .load(item.getImageProfileUrl())
                    .into(mImageProfile);
        }
    }

    private void setUpAdapter() {
        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .orderBy("username");
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, UserEntity.class)
                .build();

        adapter = new FirestorePagingAdapter<UserEntity, ContactViewHolder>(options) {
            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contact_list_item, parent, false);
                return new ContactViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactViewHolder holder,
                                            int position,
                                            @NonNull UserEntity model) {
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

    @Override
    public void onFragmentInteractionContact() {
        onBackPressed();
    }
    private void showToast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
