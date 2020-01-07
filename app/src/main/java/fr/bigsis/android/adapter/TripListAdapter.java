package fr.bigsis.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.activity.ParticipantsListActivity;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.AddTripFragment;
import fr.bigsis.android.helpers.FirestoreDBHelper;
import fr.bigsis.android.helpers.FirestoreHelper;

public class TripListAdapter extends FirestorePagingAdapter<TripEntity, TripListAdapter.TripViewHolder> {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private int i = 0;
    private String nameCampus;
    private String organism;
    AddTripFragment fragmentAdd;
    private Locale current;

    public TripListAdapter(@NonNull FirestorePagingOptions<TripEntity> options, Context context, SwipeRefreshLayout swipeRefreshLayout,
                           String nameCampus, String organism, AddTripFragment fragmentAdd) {
        super(options);
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;
        this.nameCampus = nameCampus;
        this.organism = organism;
        this.fragmentAdd = fragmentAdd;
    }

    @Override
    protected void onBindViewHolder(@NonNull TripViewHolder holder, int position, @NonNull TripEntity item) {
        holder.bind(item);
        item.setTripId(this.getItem(position).getId());
        String idTrip = item.getTripId();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                .document(nameCampus).collection("Trips")
                .document(idTrip)
                .collection("Participants")
                .document(mCurrentUserId);
        //Check if creator or not and modify button
        mFirestore.collection(organism).document("AllCampus").collection("AllTrips")
                .document(idTrip).collection("Creator").document(mCurrentUserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.btParticipate.setSelected(true);
                        holder.btParticipate.setText(R.string.modify);
                        holder.btParticipate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
                                Bundle bundle = new Bundle();
                                bundle.putString("ID_TRIP", idTrip);
                                bundle.putString("FROM", item.getFrom());
                                bundle.putString("TO", item.getTo());
                                bundle.putString("CAMPUS", item.getSharedIn());
                                bundle.putString("ORGANISM_TRIP", item.getOrganism());
                                bundle.putString("DATE", format.format(item.getDate().getTime()));
                                bundle.putString("CREATED_BY", item.getCreatedBy());

                                fragmentAdd = new AddTripFragment();
                                fragmentAdd.setArguments(bundle);

                                FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
                                transaction.addToBackStack(null);
                                transaction.add(R.id.fragment_container, fragmentAdd, "ADD_MENU_FRAGMENT")
                                        .commit();
                                            }

                        });
                    }
                }
            }
        });
        //Check if user is participating to a trip or not , and keep the button in the right color
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Boolean creator = document.getBoolean("creator");
                    if (document.exists() && !creator) {
                        i = 1;
                        holder.btParticipate.setSelected(true);
                        holder.btParticipate.setText("Ne plus participer");
                    }
                } else {
                    Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //partcipate to a trip
                if (i == 0) {
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
                            FirestoreDBHelper.setParticipantTo(organism,  "AllTrips", idTrip, mCurrentUserId, userEntity);
                            FirestoreDBHelper.setParticipantTo(organism, "AllChatGroups", idTrip, mCurrentUserId, userEntity);
                            String from = item.getFrom();
                            String to = item.getTo();
                            Date date = item.getDate();
                            String createdBy = item.getCreatedBy();
                            String sharedIn = item.getSharedIn();
                            String organismTrip = item.getOrganism();
                            double lat = item.getLatDestination();
                            double lng = item.getLngDestination();
                            TripEntity tripEntity = new TripEntity(from, to, date, createdBy, sharedIn, organismTrip, lat, lng, date.toString());
                            String titleTrip = from + " ... " + to;
                            GroupChatEntity groupChatEntity = new GroupChatEntity(titleTrip, null, date, null, organism, sharedIn);
                            FirestoreDBHelper.setData("USERS", mCurrentUserId, "ChatGroup", idTrip, groupChatEntity);
                        }
                    });
                    i++;
                    //unparticipate
                } else if (i == 1 || holder.btParticipate.isSelected()) {
                    holder.btParticipate.setSelected(false);
                    holder.btParticipate.setText("Participer");
                    FirestoreDBHelper.deleteParticipantFromDatab(organism, "AllTrips", idTrip, mCurrentUserId);
                    FirestoreDBHelper.deleteParticipantFromDatab(organism, "AllChatGroups", idTrip, mCurrentUserId);
                    FirestoreDBHelper.deleteFromdb("USERS", mCurrentUserId, "ChatGroup", idTrip);
                    i = 0;
                }
            }
        });
       holder.imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = item.getLatDestination();
                double lng = item.getLngDestination();
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="  + String.valueOf(lat)
                                + "," + String.valueOf(lng));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(mapIntent);
                    }
                });

        mFirestore.collection(organism).document("AllCampus").collection("AllTrips")
               .document(idTrip).collection("Participants")
                .whereEqualTo("creator", false).limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageProfileUrl = document.getString("imageProfileUrl");
                                if(imageProfileUrl != null) {
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
                                }  else {
                                Glide.with(holder.profile_image_one.getContext())
                                        .load(R.drawable.ic_profile)
                                        .into(holder.profile_image_one);
                            }
                            }
                        }
                    }
                });

        mFirestore.collection(organism).document("AllCampus").collection("AllTrips")
                .document(idTrip).collection("Creator")
               .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageProfileUrl = document.getString("imageProfileUrl");
                        if(imageProfileUrl != null) {
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
                        } else {
                            Glide.with(holder.profile_image_two.getContext())
                                    .load(R.drawable.ic_profile)
                                    .into(holder.profile_image_two);
                        }
                    }
                }
            }
        });
        mFirestore.collection(organism).document("AllCampus").collection("AllTrips")
            .document(idTrip).collection("Participants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            holder.mtvMore.setText("+" + (count - 2));
                            if (count < 2) {
                                holder.profile_image_one.setVisibility(View.GONE);
                                holder.mtvMore.setText("...");
                            }
                        }
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
    public void openFragment() {

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
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
        CircleImageView imgview;
        @BindView(R.id.tvDateTrip)
        TextView mTextDate;
        @BindView(R.id.btParticipate)
        Button btParticipate;
        @BindView(R.id.profile_image_one)
        CircleImageView profile_image_one;
        @BindView(R.id.profile_image_two)
        CircleImageView profile_image_two;
        @BindView(R.id.profile_image_three)
        CircleImageView profile_image_three;
        private View mView;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull TripEntity item) {
            mTextFrom.setText(item.getFrom());
            mTextTo.setText(item.getTo());

            current = mContext.getResources().getConfiguration().locale;
            if (current.getLanguage().equals("fr")) {
                SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
                mTextDate.setText(format.format(item.getDate().getTime()));
            } else if (current.getLanguage().equals("en")) {
                SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.ENGLISH);
                mTextDate.setText(format.format(item.getDate().getTime()));
            }
            SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
            mTextDate.setText(format.format(item.getDate().getTime()));
            Glide.with(imgview)
                    .asBitmap()
                    .load(R.drawable.ic_confusing_directions_filled)
                    .into(imgview);
        }
    }


}
