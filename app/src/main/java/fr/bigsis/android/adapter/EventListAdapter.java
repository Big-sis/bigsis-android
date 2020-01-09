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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.activity.ParticipantsListActivity;
import fr.bigsis.android.activity.UploadImageActivity;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.AddEventFragment;
import fr.bigsis.android.helpers.FirestoreDBHelper;
import fr.bigsis.android.helpers.FirestoreHelper;

import static fr.bigsis.android.helpers.FirestoreDBHelper.deleteParticipantFromCampus;
import static fr.bigsis.android.helpers.FirestoreDBHelper.setParticipantToCampus;

public class EventListAdapter  extends FirestorePagingAdapter<EventEntity, EventListAdapter.EventHolder> {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private int i = 0;
    private String nameCampus;
    private String organism;
    AddEventFragment fragmentAdd;
    private Locale current;
    boolean isParticipating ;

    public EventListAdapter(@NonNull FirestorePagingOptions<EventEntity> options, Context context, SwipeRefreshLayout swipeRefreshLayout,
                           String nameCampus, String organism, AddEventFragment fragmentAdd) {
        super(options);
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;
        this.nameCampus = nameCampus;
        this.organism = organism;
        this.fragmentAdd = fragmentAdd;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull EventEntity item) {
        holder.bind(item);
        item.setEventId(this.getItem(position).getId());
        String idEvent = item.getEventId();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
isParticipating = false;
        //Check if creator or not and modify button
        mFirestore.collection(organism).document("AllCampus").collection("AllEvents")
                .document(idEvent).collection("Creator").document(mCurrentUserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                holder.imageButtonImageEventItem.setVisibility(View.VISIBLE);
                                holder.btParticipateEvent.setSelected(true);
                                holder.btParticipateEvent.setText(R.string.modify);
                               holder.btParticipateEvent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ID_EVENT", idEvent);
                                        bundle.putString("TITLE", item.getTitleEvent());
                                        bundle.putString("ADRESS", item.getAddressEvent());
                                        bundle.putString("CAMPUS_EVENT", item.getSharedIn());
                                        bundle.putString("ORGANISM_EVENT", item.getOrganism());
                                        bundle.putString("DESCRIPTION", item.getDescription());
                                        bundle.putString("DATE_START", format.format(item.getDateStart().getTime()));
                                        bundle.putString("DATE_END", format.format(item.getDateEnd().getTime()));
                                        bundle.putString("CREATED_BY", item.getCreatedBy());

                                        fragmentAdd = new AddEventFragment();
                                        fragmentAdd.setArguments(bundle);

                                        FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
                                        transaction.addToBackStack(null);
                                        transaction.add(R.id.fragment_container_event, fragmentAdd, "ADD_MENU_FRAGMENT")
                                                .commit();
                                    }

                                });
                            }
                        }
                    }
                });

        //Check if user is participating to event or not , and keep the button in the right color
        DocumentReference documentReference = mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                .document(nameCampus).collection("Events")
                .document(idEvent)
                .collection("Participants")
                .document(mCurrentUserId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Boolean creator = document.getBoolean("creator");
                    if (document.exists() && !creator) {

                        holder.btParticipateEvent.setVisibility(View.GONE);
                        holder.btUnparticipateEvent.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.infoEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.relativeLayoutEventText.setVisibility(View.VISIBLE);
//                SeeMoreText.makeTextViewResizable(holder.tvDesctiptionEvent, 4, mContext.getString(R.string.see_more), true);
            }
        });
        holder.imgBtCancelInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.relativeLayoutEventText.setVisibility(View.GONE);
            }
        });
        holder.btParticipateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //partcipate

                String title = item.getTitleEvent();
                String description = item.getDescription();
                String adress = item.getAddressEvent();
                Date dateStart = item.getDateStart();
                Date dateEnd = item.getDateEnd();
                String imageEvent = item.getImage();
                String createdBy = item.getCreatedBy();
                String sharedIn = item.getSharedIn();
                String organismEvent = item.getOrganism();
                double lat = item.getLatDestination();
                double lng = item.getLngDestination();
                boolean alertAvailable = item.isAlertAvailable();

                    mFirestore.collection("USERS").document(mCurrentUserId)
                            .collection("ParticipateToEvents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if(  task.getResult().size() == 0) {
                                holder.btUnparticipateEvent.setVisibility(View.VISIBLE);
                                holder.btParticipateEvent.setVisibility(View.GONE);
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
                                                firstname, lastname, false, isAdmin, nameCampus, organism);
                                        FirestoreDBHelper.setParticipantTo(organism, "AllEvents", idEvent, mCurrentUserId, userEntity);
                                        FirestoreDBHelper.setParticipantTo(organism, "AllChatGroups", idEvent, mCurrentUserId, userEntity);

                                        EventEntity eventEntity = new EventEntity(dateStart, dateEnd, title, adress, imageEvent,
                                                description, createdBy, sharedIn, organismEvent, lat, lng, alertAvailable);
                                        setParticipantToCampus(organism, sharedIn, "Events", idEvent, mCurrentUserId, userEntity);
                                        GroupChatEntity groupChatEntity = new GroupChatEntity(title, null, dateStart, null, organism, sharedIn);
                                        FirestoreDBHelper.setData("USERS", mCurrentUserId, "ChatGroup", idEvent, groupChatEntity);
                                        mFirestore.collection("USERS").document(mCurrentUserId).collection("ParticipateToEvents")
                                                .document(idEvent).set(eventEntity);
                                    }
                                });
                            }
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId();
                                    mFirestore.collection("USERS").document(mCurrentUserId)
                                            .collection("ParticipateToEvents").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                           Date dateStartParticipating = documentSnapshot.getDate("dateStart");
                                            if(dateStartParticipating.before(dateEnd) || dateStartParticipating.after(dateStart)) {
                                                isParticipating = true;
                                                Toast.makeText(mContext, "Vous participez déjà à un évènement à cette date", Toast.LENGTH_SHORT).show();
                                            }
                                            if(isParticipating == false ) {
                                                holder.btUnparticipateEvent.setVisibility(View.VISIBLE);
                                                holder.btParticipateEvent.setVisibility(View.GONE);
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
                                                                firstname, lastname, false, isAdmin, nameCampus, organism);
                                                        FirestoreDBHelper.setParticipantTo(organism, "AllEvents", idEvent, mCurrentUserId, userEntity);
                                                        FirestoreDBHelper.setParticipantTo(organism, "AllChatGroups", idEvent, mCurrentUserId, userEntity);

                                                        EventEntity eventEntity = new EventEntity(dateStart, dateEnd, title, adress, imageEvent,
                                                                description, createdBy, sharedIn, organismEvent, lat, lng, alertAvailable);
                                                        setParticipantToCampus(organism, sharedIn, "Events", idEvent, mCurrentUserId, userEntity);
                                                        GroupChatEntity groupChatEntity = new GroupChatEntity(title, null, dateStart, null, organism, sharedIn);
                                                        FirestoreDBHelper.setData("USERS", mCurrentUserId, "ChatGroup", idEvent, groupChatEntity);
                                                        mFirestore.collection("USERS").document(mCurrentUserId).collection("ParticipateToEvents")
                                                                .document(idEvent).set(eventEntity);
                                                    }
                                                });
                                                //unparticipate
                                            } else {
                                                Toast.makeText(mContext, "hey", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                }
                        }

                    });
            }
        });

        holder.btUnparticipateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btParticipateEvent.setVisibility(View.VISIBLE);
                holder.btUnparticipateEvent.setVisibility(View.GONE);
                String sharedIn = item.getSharedIn();

                FirestoreDBHelper.deleteParticipantFromDatab(organism, "AllEvents", idEvent, mCurrentUserId);
                FirestoreDBHelper.deleteParticipantFromDatab(organism, "AllChatGroups", idEvent, mCurrentUserId);
                FirestoreDBHelper.deleteFromdb("USERS", mCurrentUserId, "ChatGroup", idEvent);
                deleteParticipantFromCampus(organism, sharedIn, "Events", idEvent, mCurrentUserId);
                mFirestore.collection("USERS").document(mCurrentUserId).collection("ParticipateToEvents")
                        .document(idEvent).delete();
            }
        });

        if(item.isAlertAvailable()) {
            holder.tvShowStaff.setVisibility(View.VISIBLE);
        }
        holder.tvShowStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ParticipantsListActivity.class);
                intent.putExtra("STAFF", idEvent);
                mContext.startActivity(intent);
            }
        });

        //Edit event's photo
        holder.imageButtonImageEventItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent intent = new Intent(mContext, UploadImageActivity.class);
                intent.putExtra("ID_EVENT_PHOTO", idEvent);
                intent.putExtra("EVENT_PHOTO", item.getImage());
                intent.putExtra("campusName", item.getSharedIn());
                intent.putExtra("organism", item.getOrganism());
                activity.startActivity(intent);
            }
        });

        FirestoreHelper.getImageProfile(organism, idEvent, holder.profile_image_one.getContext(), holder.profile_image_one);

        mFirestore.collection(organism).document("AllCampus").collection("AllEvents")
                .document(idEvent).collection("Creator")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageProfileUrl = document.getString("imageProfileUrl");
                        if(imageProfileUrl != null) {
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageProfileUrl);
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
        FirestoreHelper.getCountOfParticipants(organism, idEvent, holder.tvMore_event, holder.profile_image_one);

        holder.profile_image_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParticipantActivity(idEvent);
            }
        });
        holder.profile_image_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParticipantActivity(idEvent);
            }
        });
        holder.profile_image_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParticipantActivity(idEvent);
            }
        });
    }

    private void goToParticipantActivity(String idTrip) {
        Intent intent = new Intent(mContext, ParticipantsListActivity.class);
        intent.putExtra("ID_EVENT", idTrip);
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
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_list, parent, false);
        return new EventHolder(view);
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

    class EventHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleEventItem)
        TextView textViewTitle;
        @BindView(R.id.textViewDateEvent)
        TextView textViewDateEvent;
        @BindView(R.id.tvShowStaff)
        TextView tvShowStaff;
        @BindView(R.id.tvMore_event)
        TextView tvMore_event;
        @BindView(R.id.tvDesctiptionEvent)
        TextView tvDesctiptionEvent;
        @BindView(R.id.tvInformations)
        TextView tvInformations;
        @BindView(R.id.btParticipateEvent)
        Button btParticipateEvent;
        @BindView(R.id.btUnparticipateEvent)
        Button btUnparticipateEvent;
        @BindView(R.id.profile_image_one_event)
        CircleImageView profile_image_one;
        @BindView(R.id.profile_image_two_event)
        CircleImageView profile_image_two;
        @BindView(R.id.profile_image_three_event)
        CircleImageView profile_image_three;
        @BindView(R.id.eventImage)
        ImageView eventImage;
        @BindView(R.id.imageButtonImageEventItem)
        ImageButton imageButtonImageEventItem;
        @BindView(R.id.imgBtCancelInfo)
        ImageButton imgBtCancelInfo;
        @BindView(R.id.infoEventButton)
        FloatingActionButton infoEventButton;
        @BindView(R.id.relativeLayoutEventText)
        RelativeLayout relativeLayoutEventText;
        private View mView;

        public EventHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull EventEntity item) {
            textViewTitle.setText(item.getTitleEvent());
            current = mContext.getResources().getConfiguration().locale;
            Calendar calendar = Calendar.getInstance();
            //TODO set date in the right form at
            Date today = calendar.getTime();
            if(item.getDateStart().equals(today)) {
                textViewDateEvent.setText(R.string.today);
            }
            if (current.getLanguage().equals("fr")) {
                SimpleDateFormat format = new SimpleDateFormat(" dd/MM/yy HH:mm", Locale.FRENCH);
                textViewDateEvent.setText(format.format(item.getDateStart().getTime()));
            } else if (current.getLanguage().equals("en")) {
                SimpleDateFormat format = new SimpleDateFormat(" dd/MM/yy HH:mm", Locale.ENGLISH);
                textViewDateEvent.setText(format.format(item.getDateStart().getTime()));
            }

            tvDesctiptionEvent.setText(item.getDescription());
            if(item.getImage() != null) {
                StorageReference storageRef = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(item.getImage());
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        String urlImage = downloadUrl.toString();
                        Glide.with(eventImage)
                                .load(urlImage)
                                .apply(new RequestOptions().override(500, 500))
                                .into(eventImage);
                    }
                });
            }
        }
    }
}
