package fr.bigsis.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;

public class RequestListAdapter extends FirestorePagingAdapter<UserEntity, RequestListAdapter.RequestViewHolder> {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;

    public RequestListAdapter(@NonNull FirestorePagingOptions<UserEntity> options, Context context, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull UserEntity item) {
        holder.bind(item);

        item.setUserId(this.getItem(position).getId());
        String idContact = item.getUserId();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        // OtherUserProfileFragment fragmentProfile = OtherUserProfileFragment.newInstance(idContact);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO go to profile

              /*  FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
                transaction.addToBackStack(null);
                transaction.add(R.id.fragment_container_contact, fragmentProfile, "PROFILE_OTHER_USER_FRAGMENT")
                        .commit();*/
            }
        });

// ACCEPT REQUEST
        holder.btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("users").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        String firstname = documentSnapshot.getString("firstname");
                        String lastname = documentSnapshot.getString("lastname");
                        UserEntity userEntity = new UserEntity(username, imageProfileUrl, firstname, lastname);
                        mFirestore.collection("users")
                                .document(idContact)
                                .collection("Friends")
                                .document(mCurrentUserId)
                                .set(userEntity, SetOptions.merge());
                    }
                });

                mFirestore.collection("users").document(idContact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        String firstname = documentSnapshot.getString("firstname");
                        String lastname = documentSnapshot.getString("lastname");
                        UserEntity userEntity = new UserEntity(username, imageProfileUrl, firstname, lastname);
                        mFirestore.collection("users")
                                .document(mCurrentUserId)
                                .collection("Friends")
                                .document(idContact)
                                .set(userEntity, SetOptions.merge());
                    }
                });

                mFirestore.collection("users")
                        .document(mCurrentUserId)
                        .collection("Request sent")
                        .document(idContact)
                        .delete();

                mFirestore.collection("users")
                        .document(idContact)
                        .collection("Request received")
                        .document(mCurrentUserId)
                        .delete();
            }
        });

        // REFUSE REQUEST
        holder.imgBtRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("users")
                        .document(mCurrentUserId)
                        .collection("Request sent")
                        .document(idContact)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //TODO snackbar for invitation refusée
                    }
                });

                mFirestore.collection("users")
                        .document(idContact)
                        .collection("Request received")
                        .document(mCurrentUserId)
                        .delete();
            }
        });
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_list_item, parent, false);
        return new RequestViewHolder(view);
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
                showToast("reached_end_data");
                break;
            case ERROR:
                showToast("error_ocurred");
                retry();
                break;
        }
    }

    private void showToast(@NonNull String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(@NonNull Exception e) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameContactRequest)
        TextView mTextName;
        @BindView(R.id.tvUserNameContactRequest)
        TextView mTextUserName;
        @BindView(R.id.image_profile_contact_request)
        CircleImageView mImageProfile;
        @BindView(R.id.btAccept)
        Button btAccept;
        @BindView(R.id.imgBtRefuse)
        ImageButton imgBtRefuse;
        private View mView;
        private Context context;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull UserEntity item) {
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
