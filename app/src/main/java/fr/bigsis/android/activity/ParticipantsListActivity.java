package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class ParticipantsListActivity extends BigsisActivity {


    private static final String TAG = "ParticipantActivity";
    FloatingActionButton fbTrip;
    FirestorePagingAdapter<UserEntity, ParticipantsListActivity.ParticipantViewHolder> adapter;
    CollectionReference mItemsCollection;
    @BindView(R.id.rvPartcipantList)
    RecyclerView mRecycler;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    UserEntity user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("users");
    private FirebaseFirestore mFirestore;
    private String mCurrentUser;
    private FirebaseAuth mAuth;
    ImageButton imgBtBack;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();

        ButterKnife.bind(this);
        setToolBar();
        setUpAdapter();

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
                startActivity(new Intent(ParticipantsListActivity.this, TripListActivity.class));
            }
        });
    }

    private void setUpAdapter() {
        TripEntity trip = new TripEntity();
        String idTrip;
        Intent iin= getIntent();
        Bundle extras =iin.getExtras();
        idTrip = extras.getString("ID_TRIP");
        Query query = FirebaseFirestore.getInstance()
                .collection("trips").document(idTrip).collection("participants");
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();
        FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, snapshot -> {
                    UserEntity user = snapshot.toObject(UserEntity.class);
                    user.setUserId(snapshot.getId());
                    return user;
                })
                .build();

        adapter = new FirestorePagingAdapter<UserEntity, ParticipantsListActivity.ParticipantViewHolder>(options) {
            @NonNull
            @Override
            public ParticipantsListActivity.ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                            int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contact_list_item, parent, false);
                return new ParticipantsListActivity.ParticipantViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ParticipantsListActivity.ParticipantViewHolder holder,
                                            int position,
                                            @NonNull UserEntity model) {
                holder.bind(model);
                String idUserContact = model.getUserId();
                mFirestore = FirebaseFirestore.getInstance();

                //Check if user sent request not , and keep the button in the right color
                mFirestore.collection("users")
                        .document(idUserContact)
                        .collection("Request received")
                        .document(mCurrentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                holder.btRequestFriend.setSelected(true);
                                holder.btRequestFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                            }
                        } else {
                            Toast.makeText(ParticipantsListActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                holder.btRequestFriend.setOnClickListener(new View.OnClickListener() {
                    int i = 0;
                    @Override
                    public void onClick(View v) {
                        if (i == 0) {
                            mFirestore.collection("users").document(idUserContact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    // request friend
                                    holder.btRequestFriend.setSelected(true);
                                    holder.btRequestFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                                    String username = documentSnapshot.getString("username");
                                    String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                                    String firstname = documentSnapshot.getString("firstname");
                                    String lastname = documentSnapshot.getString("lastname");
                                    UserEntity userEntity = new UserEntity(username, imageProfileUrl, firstname, lastname);
                                    mFirestore.collection("users")
                                            .document(mCurrentUser)
                                            .collection("Request sent")
                                            .document(idUserContact)
                                            .set(userEntity, SetOptions.merge());
                                }
                            });
                            mFirestore.collection("users").document(mCurrentUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String username = documentSnapshot.getString("username");
                                    String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                                    String firstname = documentSnapshot.getString("firstname");
                                    String lastname = documentSnapshot.getString("lastname");
                                    UserEntity userEntity = new UserEntity(username, imageProfileUrl, firstname, lastname);
                                    mFirestore.collection("users")
                                            .document(idUserContact)
                                            .collection("Request received")
                                            .document(mCurrentUser)
                                            .set(userEntity, SetOptions.merge());
                                }
                            });
                            i++;
                        } else if (i == 1) {
                            //UNREQUEST
                            holder.btRequestFriend.setSelected(false);
                            holder.btRequestFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                            mFirestore.collection("users")
                                    .document(mCurrentUser)
                                    .collection("Request sent")
                                    .document(idUserContact)
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: Removed list item");
                                }
                            });

                            mFirestore.collection("users")
                                    .document(idUserContact)
                                    .collection("Request received")
                                    .document(mCurrentUser)
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
    private void showToast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvNameContact)
        TextView mTextName;

        @BindView(R.id.tvUserNameContact)
        TextView mTextUserName;

        @BindView(R.id.image_profile_contact)
        CircleImageView mImageProfile;

        //TODO replace button delete
        @BindView(R.id.btDelete)
        Button btRequestFriend;

        private ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(@NonNull UserEntity item) {
            mTextName.setText(item.getFirstname() + " " + item.getLastname());
            mTextUserName.setText(item.getUsername());

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
}
