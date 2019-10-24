package fr.bigsis.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.AddTripFragment;
import fr.bigsis.android.fragment.SearchMenuFragment;
import fr.bigsis.android.fragment.ToolBarFragment;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;
import fr.bigsis.android.viewModel.SearchMenuViewModel;


public class TripListActivity extends AppCompatActivity implements SearchMenuFragment.OnFragmentInteractionListener, AddTripFragment.OnFragmentInteractionListener, ToolBarFragment.OnFragmentInteractionListener {

    private static final String TAG = "TripListActivity";
    SearchMenuFragment fragmentOpen = SearchMenuFragment.newInstance();
    AddTripFragment fragmentAdd = AddTripFragment.newInstance();
    FirestorePagingAdapter<TripEntity, TripListViewHolder> adapter;
    ConstraintLayout transitionContainer;
    ImageButton imbtSearch, imBtCancel, imBtAdd;
    TextView tvTitleToolbar;
    @BindView(R.id.paging_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout frameLayout;
    private SearchMenuViewModel viewModel;
    private CollectionReference mItemsCollection;
    private FirebaseFirestore mFirestore;
    private String userId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        viewModel = ViewModelProviders.of(this).get(SearchMenuViewModel.class);

        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();
        mItemsCollection = mFirestore.collection("trips");

        setUpAdapter();
        setToolBar();

        frameLayout = findViewById(R.id.fragment_container);

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

    private void setToolBar() {
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imbtSearch = transitionContainer.findViewById(R.id.imBt_search_frag);
        imBtAdd = transitionContainer.findViewById(R.id.imBt_add_frag);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(R.string.trips);
        imbtSearch.setVisibility(View.VISIBLE);
        imBtAdd.setVisibility(View.VISIBLE);

        imBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTrip();
                imBtAdd.setVisibility(View.GONE);
                imBtCancel.setVisibility(View.VISIBLE);
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

                if (fragmentOpen.isAdded()) {
                    onFragmentInteraction();
                }
                if (fragmentAdd.isAdded()) {
                    onFragmentInteractionAdd();
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

        Query query = FirebaseFirestore.getInstance().collection("trips");
        FirestorePagingOptions<TripEntity> options = new FirestorePagingOptions.Builder<TripEntity>()
                .setQuery(query, config, snapshot -> {
                    TripEntity trip = snapshot.toObject(TripEntity.class);
                    trip.setTripId(snapshot.getId());
                    return trip;
                })
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestorePagingAdapter<TripEntity, TripListViewHolder>(options) {
            @NonNull
            @Override
            public TripListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trip_list_item, parent, false);
                return new TripListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TripListViewHolder holder,
                                            int position,
                                            @NonNull TripEntity model) {
                holder.bind(model);
                String idtrip = model.getTripId();
                mAuth = FirebaseAuth.getInstance();
                userId = mAuth.getCurrentUser().getUid();
                mFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReferencePartcipants = mFirestore.collection("trips")
                        .document(idtrip)
                        .collection("participants")
                        .document(userId);

                //Check if user is participating to a trip or not , and keep the button in the right color
                documentReferencePartcipants.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (task.isSuccessful()) {
                           DocumentSnapshot document = task.getResult();
                           if (document.exists()) {
                               holder.btParticipate.setSelected(true);
                               holder.btParticipate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                           }
                       } else {
                           Toast.makeText(TripListActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                       }
                   }
                       });

                holder.btParticipate.setOnClickListener(new View.OnClickListener() {
                    int i = 0;
                    @Override
                    public void onClick(View v) {
                        //partcipate to a trip
                        if (i == 0) {
                            holder.btParticipate.setSelected(true);
                            holder.btParticipate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                            mFirestore.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String username = documentSnapshot.getString("username");
                                    String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                                    String firstname = documentSnapshot.getString("firstname");
                                    String lastname = documentSnapshot.getString("lastname");
                                    UserEntity userEntity = new UserEntity(username, imageProfileUrl, firstname, lastname);
                                    mFirestore.collection("trips")
                                            .document(idtrip)
                                            .collection("participants")
                                            .document(userId)
                                            .set(userEntity, SetOptions.merge());
                                }
                            });

                            mFirestore.collection("trips").document(idtrip).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String from = documentSnapshot.getString("from");
                                    String to = documentSnapshot.getString("to");
                                    Date date = documentSnapshot.getDate("date");
                                    String image = documentSnapshot.getString("image");
                                    String createdBy = documentSnapshot.getString("createdBy");
                                    TripEntity tripEntity = new TripEntity(from, to, date, image, createdBy);
                                    mFirestore.collection("users")
                                            .document(userId)
                                            .collection("participateTo")
                                            .document(idtrip)
                                            .set(tripEntity, SetOptions.merge());
                                }
                            });
                            i++;
                            //unparticipate

                        } else if (i == 1) {

                            holder.btParticipate.setSelected(false);
                            holder.btParticipate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                            mFirestore.collection("users")
                                    .document(userId)
                                    .collection("participateTo")
                                    .document(idtrip)
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: Removed list item");
                                }
                            });

                            mFirestore.collection("trips")
                                    .document(idtrip)
                                    .collection("participants")
                                    .document(userId)
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: Removed list item");
                                }
                            });

                        i = 0;
                    }
                    }
                });

                holder.mImvTripImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirestore.collection("trips").document(idtrip).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String to = documentSnapshot.getString("to");
                                Uri gmmIntentUri = Uri.parse("google.navigation:q="+ to +"France");
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        });
                    }
                });

                holder.profile_image_one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        documentReferencePartcipants.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        startActivity(new Intent(TripListActivity.this, ParticipantsListActivity.class));
                                    }
                                } else {
                                    Toast.makeText(TripListActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
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
                startActivity(new Intent(TripListActivity.this, UserProfileActivity.class));
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
                return super.onOptionsItemSelected(item);
    }

    private void showToast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public class TripListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTripsFrom)
        TextView mTextFrom;

        @BindView(R.id.tvTripsTo)
        TextView mTextTo;

        @BindView(R.id.ivTripImage)
        ImageView mImvTripImage;

        @BindView(R.id.tvDateTrip)
        TextView mTextDate;

        @BindView(R.id.btParticipate)
        Button btParticipate;

        @BindView(R.id.profile_image_one)
        ImageView profile_image_one;

        private TripListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(@NonNull TripEntity item) {
            mTextFrom.setText(item.getFrom());
            mTextTo.setText(item.getTo());
            SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
            mTextDate.setText(format.format(item.getDate().getTime()));

            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(250, 250);

            Glide.with(mImvTripImage.getContext())
                    .asBitmap()
                    .apply(myOptions)
                    .load(item.getImage())
                    .into(mImvTripImage);
        }
    }
}
