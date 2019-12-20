package fr.bigsis.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.activity.ParticipantsListActivity;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.FirestoreDBHelper;
import fr.bigsis.android.helpers.FirestoreHelper;

public class TripListAdapter extends FirestorePagingAdapter<TripEntity, TripListAdapter.TripViewHolder> {

    FirebaseStorage storage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    String TAG = "triplist";
    int i = 0;

    public TripListAdapter(@NonNull FirestorePagingOptions<TripEntity> options, Context context, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull TripViewHolder holder, int position, @NonNull TripEntity item) {
        holder.bind(item);
        item.setTripId(this.getItem(position).getId());
        String idTrip = item.getTripId();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String nameCampus = documentSnapshot.getString("groupCampus");
                String organism = documentSnapshot.getString("organism");

                DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                        .document(nameCampus).collection("Trips")
                        .document(idTrip)
                        .collection("Participants")
                        .document(mCurrentUserId);
                //Check if user is participating to a trip or not , and keep the button in the right color
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                i = 1;
                                holder.btParticipate.setSelected(true);
                                holder.btParticipate.setText("Ne plus participer");
                            }
                        } else {
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
       // FirestoreHelper.update("trips", idTrip, "participants", "imageProfileUrl");


      //  FirestoreHelper.updateUserProfile(mCurrentUserId, "trips", idTrip, "participants", mCurrentUserId);

        holder.btParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //partcipate to a trip
                if (i == 0 ) {
                    holder.btParticipate.setSelected(true);
                    holder.btParticipate.setText("Ne plus participer");

                    mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String username = documentSnapshot.getString("username");
                            String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                            String firstname = documentSnapshot.getString("firstname");
                            String lastname = documentSnapshot.getString("lastname");
                            String descripition = documentSnapshot.getString("description");
                            Boolean isAdmin = documentSnapshot.getBoolean("admin");
                            String nameCampus = documentSnapshot.getString("groupCampus");
                            String organism = documentSnapshot.getString("organism");
                            UserEntity userEntity = new UserEntity(username, descripition, imageProfileUrl,
                                    firstname, lastname, isAdmin, false, nameCampus, organism);

                            FirestoreDBHelper.setParticipantOrCreatorToCollectionInOneCampus(organism, nameCampus, "Trips", idTrip, "Participants", mCurrentUserId, userEntity);
                            FirestoreDBHelper.setParticipantOrCreatorToCollectionInOneCampus(organism, nameCampus, "Trips", idTrip, "Creator", mCurrentUserId, userEntity);
                           // FirestoreHelper.setData("trips", idTrip, "participants", mCurrentUserId, userEntity);
                            FirestoreDBHelper.setParticipantOrCreatorToCollectionInOneCampus(organism, nameCampus, "GroupChat", idTrip, "Participants", mCurrentUserId, userEntity);
                            String from = item.getFrom();
                            String to = item.getTo();
                            Date date = item.getDate();
                            String image = item.getImage();
                            String createdBy = item.getCreatedBy();
                            TripEntity tripEntity = new TripEntity(from, to, date, image, createdBy);
                            mFirestore.collection("USERS").document(mCurrentUserId).collection("ParticipateTo").document(idTrip).set(tripEntity);
                            //FirestoreHelper.setData("users", mCurrentUserId, "participateTo", idTrip, tripEntity);
                            String titleTrip = from + " ... " + to;
                            GroupChatEntity groupChatEntity = new GroupChatEntity(titleTrip, image, date);
                            FirestoreDBHelper.setData("USERS", mCurrentUserId, "GroupChat", idTrip, groupChatEntity);

                            //  FirestoreHelper.setData("GroupChat", idGroup, "participants", mCurrentUserId, userEntity);
                        }
                    });

                    i++;
                    //unparticipate
                } else if (i == 1 || holder.btParticipate.isSelected()) {
                    mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String nameCampus = documentSnapshot.getString("groupCampus");
                            String organism = documentSnapshot.getString("organism");

                            holder.btParticipate.setSelected(false);
                            holder.btParticipate.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                            holder.btParticipate.setText("Participer");

                            FirestoreDBHelper.deleteParticipantOrCreatorInOneCampus(organism, nameCampus, "Trips", idTrip,"Participants", mCurrentUserId);
                            FirestoreDBHelper.deleteParticipantOrCreatorInOneCampus(organism, nameCampus, "GroupChat", idTrip,"Participants", mCurrentUserId);
                            FirestoreDBHelper.deleteFromdb( "USERS", mCurrentUserId, "ParticipateTo", idTrip);
                            FirestoreDBHelper.deleteFromdb( "USERS", mCurrentUserId, "GroupChat", idTrip);
                            i = 0;
                        }
                    });

                }
            }
        });
      /*  holder.mImvTripImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("trips").document(idTrip).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String to = documentSnapshot.getString("to");
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + to + "France");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(mapIntent);
                    }
                });
            }
        });*/
        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nameCampus = documentSnapshot.getString("groupCampus");
                String organism = documentSnapshot.getString("organism");

                mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                        .document(nameCampus).collection("Trips").document(idTrip).collection("Participants")
                        .whereEqualTo("creator", false).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String imageProfileUrl = document.getData().get("imageProfileUrl").toString();
                                            storage = FirebaseStorage.getInstance();
                                            StorageReference storageRef = storage.getReferenceFromUrl(imageProfileUrl);
                                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Uri downloadUrl = uri;
                                                    String urlImage = downloadUrl.toString();
                                                    Glide.with(holder.profile_image_one.getContext())
                                                            .load(urlImage)
                                                            .into(holder.profile_image_one);
                                                }
                                            });
                                        }
                                    }
                            }
                        });
                mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                        .document(nameCampus).collection("Trips").document(idTrip).collection("Participants")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int count = 0;
                                    for (DocumentSnapshot document : task.getResult()) {
                                        count++;
                                    }
                                    holder.mtvMore.setText(String.valueOf("+" + (count - 2)));
                                    if (count < 2) {
                                        holder.profile_image_one.setVisibility(View.GONE);
                                        holder.mtvMore.setText(String.valueOf("..."));
                                    }
                                }
                            }
                        });
                mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                        .document(nameCampus).collection("Trips").document(idTrip).collection("Participants")
                        .whereEqualTo("Creator", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageProfileUrl = document.getData().get("imageProfileUrl").toString();
                                storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl(imageProfileUrl);
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUrl = uri;
                                        String urlImage = downloadUrl.toString();
                                        Glide.with(holder.profile_image_two.getContext())
                                                .load(urlImage)
                                                .into(holder.profile_image_two);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        holder.profile_image_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParticipantActivity(idTrip);
            }
        });
        holder.profile_image_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParticipantActivity(idTrip);
            }
        });
        holder.profile_image_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParticipantActivity(idTrip);
            }
        });
    }

    private void goToParticipantActivity(String idTrip) {
        Intent intent = new Intent(mContext, ParticipantsListActivity.class);
        intent.putExtra("ID_TRIP", idTrip);
        mContext.startActivity(intent);
    }
    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_list_item, parent, false);
        return new TripViewHolder(view);
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

    class TripViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTripsFrom)
        TextView mTextFrom;
        @BindView(R.id.tvTripsTo)
        TextView mTextTo;
        @BindView(R.id.tvMore)
        TextView mtvMore;
       @BindView(R.id.ivTripImage)
        ImageView imgview;
        @BindView(R.id.tvDateTrip)
        TextView mTextDate;
        @BindView(R.id.btParticipate)
        Button btParticipate;
        @BindView(R.id.profile_image_one)
        ImageView profile_image_one;
        @BindView(R.id.profile_image_two)
        ImageView profile_image_two;
        @BindView(R.id.profile_image_three)
        ImageView profile_image_three;
        private View mView;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull TripEntity item) {
            mTextFrom.setText(item.getFrom());
            mTextTo.setText(item.getTo());
            SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);

            mTextDate.setText(format.format(item.getDate().getTime()));

        }
    }
}
