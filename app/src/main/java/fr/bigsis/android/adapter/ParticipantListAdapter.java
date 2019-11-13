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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

public class ParticipantListAdapter extends FirestorePagingAdapter<UserEntity, ParticipantListAdapter.ParticipantViewHolder> {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;

    public ParticipantListAdapter(@NonNull FirestorePagingOptions<UserEntity> options, Context context, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull ParticipantViewHolder holder,
                                    int position,
                                    @NonNull UserEntity item) {
        holder.bind(item);
        item.setUserId(this.getItem(position).getId());
        String idContact = item.getUserId();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReferenceRequestSent = mFirestore.collection("users")
                .document(mCurrentUserId)
                .collection("Request sent")
                .document(idContact);

        // Check if request was sent or not , and keep the button in the right color
        documentReferenceRequestSent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.btRequestFriend.setSelected(true);
                        holder.btRequestFriend.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    }
                }
            }
        });

        mFirestore.collection("users")
                .document(mCurrentUserId)
                .collection("Friends")
                .document(idContact).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        holder.btDeleteFriend.setVisibility(View.GONE);
                        holder.btRequestFriend.setVisibility(View.VISIBLE);
                    } else if (document.exists()) {
                        holder.btDeleteFriend.setVisibility(View.VISIBLE);
                        holder.btRequestFriend.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFirestore.collection("users")
                .document(mCurrentUserId)
                .collection("Request sent")
                .document(idContact).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.btRequestFriend.setSelected(true);
                        holder.btRequestFriend.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    }
                } else {
                    Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btRequestFriend.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    holder.btRequestFriend.setSelected(true);
                    holder.btRequestFriend.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

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
                                    .collection("Request received")
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
                                    .collection("Request sent")
                                    .document(idContact)
                                    .set(userEntity, SetOptions.merge());
                        }
                    });
                    i++;
                    //unrequest
                } else if (i == 1) {

                    holder.btRequestFriend.setSelected(false);
                    holder.btRequestFriend.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    mFirestore.collection("users")
                            .document(idContact)
                            .collection("Request received")
                            .document(mCurrentUserId)
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //TODO EXTRACT STRING
                            Snackbar.make(v, "Invitation annulée", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });

                    mFirestore.collection("users")
                            .document(mCurrentUserId)
                            .collection("Request sent")
                            .document(idContact)
                            .delete();
                    i = 0;
                }
            }
        });

        holder.btDeleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View dialogView = inflater.inflate(R.layout.style_alert_dialog, null);
                Button btNo = dialogView.findViewById(R.id.btNo);
                Button btDelete = dialogView.findViewById(R.id.btDeleteFriend);

                btDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFirestore.collection("users")
                                .document(mCurrentUserId)
                                .collection("Friends")
                                .document(idContact)
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(v, "Contact supprimé avec succès", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });
                        mFirestore.collection("users")
                                .document(idContact)
                                .collection("Friends")
                                .document(mCurrentUserId)
                                .delete();
                        dialogBuilder.dismiss();

                    }
                });
                btNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });
        //GO TO PROFILE
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
                        .add(R.id.fragment_container_profile_participant, myFragment, "OTHER_USER_PROFILE")
                        .commit();
            }
        });
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ParticipantViewHolder(view);
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

    class ParticipantViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameContact)
        TextView mTextName;
        @BindView(R.id.tvUserNameContact)
        TextView mTextUserName;
        @BindView(R.id.image_profile_contact)
        CircleImageView mImageProfile;
        @BindView(R.id.btDelete)
        Button btDeleteFriend;
        @BindView(R.id.btAdd)
        Button btRequestFriend;
        private View mView;

        public ParticipantViewHolder(@NonNull View itemView) {
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
