package fr.bigsis.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.OtherUserProfileFragment;
import fr.bigsis.android.helpers.FirestoreHelper;

public class RequestListAdapter extends FirestorePagingAdapter<UserEntity, RequestListAdapter.RequestViewHolder> {
    FirebaseStorage storage;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("idString", idContact);
                OtherUserProfileFragment myFragment = new OtherUserProfileFragment();
                myFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top)
                        .addToBackStack(null)
                        .add(R.id.fragment_container_contact, myFragment, "PROFILE_OTHER_USER_FRAGMENT")
                        .commit();
            }
        });
       // FirestoreHelper.updateUserProfile(idContact, "users", mCurrentUserId, "Request received", idContact);

       // FirestoreHelper.update("users", mCurrentUserId, "Request received", "imageProfileUrl");

        //  FirestoreHelper.updateUserProfile(mCurrentUserId, "trips", idTrip, "participants", mCurrentUserId);


// ACCEPT REQUEST
        holder.btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btAccept.setSelected(true);
                holder.btAccept.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        String firstname = documentSnapshot.getString("firstname");
                        String lastname = documentSnapshot.getString("lastname");
                        String description = documentSnapshot.getString("description");
                        String organism = documentSnapshot.getString("organism");
                        String groupCampus = documentSnapshot.getString("groupCampus");
                        Boolean admin = documentSnapshot.getBoolean("admin");
                        UserEntity userEntity = new UserEntity(username,description, imageProfileUrl, firstname, lastname, admin,
                                groupCampus, organism);
                        mFirestore.collection("USERS")
                                .document(idContact)
                                .collection("Friends")
                                .document(mCurrentUserId)
                                .set(userEntity, SetOptions.merge());
                        Snackbar.make(v, mContext.getString(R.string.request_accept), Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                        String username = item.getUsername();
                        String imageProfileUrl = item.getImageProfileUrl();
                        String firstname = item.getFirstname();
                        String lastname = item.getLastname();
                        String organism = item.getOrganism();
                        String campusName = item.getGroupCampus();
                        String description = item.getDescription();
                        Boolean isAdmin = item.isAdmin();
                        UserEntity userEntity = new UserEntity(username,description, imageProfileUrl, firstname, lastname, isAdmin,
                                campusName, organism);
                        mFirestore.collection("USERS")
                                .document(mCurrentUserId)
                                .collection("Friends")
                                .document(idContact)
                                .set(userEntity, SetOptions.merge());

                mFirestore.collection("USERS")
                        .document(mCurrentUserId)
                        .collection("RequestSent")
                        .document(idContact)
                        .delete();

                mFirestore.collection("USERS")
                        .document(mCurrentUserId)
                        .collection("RequestReceived")
                        .document(idContact)
                        .delete();
            }
        });

        // REFUSE REQUEST
        holder.imgBtRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("USERS")
                        .document(mCurrentUserId)
                        .collection("RequestSent")
                        .document(idContact)
                        .delete();

                mFirestore.collection("USERS")
                        .document(idContact)
                        .collection("RequestReceived")
                        .document(mCurrentUserId)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(v, mContext.getString(R.string.request_refused), Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
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
                break;
            case ERROR:
                retry();
                break;
        }
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
            storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(item.getImageProfileUrl());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUrl = uri;
                    String urlImage = downloadUrl.toString();
                    Glide.with(mImageProfile.getContext())
                            .asBitmap()
                            .apply(myOptions)
                            .load(urlImage)
                            .into(mImageProfile);
                }
            });
        }
    }
}
