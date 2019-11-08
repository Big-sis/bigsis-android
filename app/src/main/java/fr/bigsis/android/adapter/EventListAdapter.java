package fr.bigsis.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.activity.ParticipantsListActivity;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.UploadImageHelper;
import fr.bigsis.android.view.SeeMoreText;

public class EventListAdapter extends FirestorePagingAdapter<EventEntity, EventListAdapter.EventViewHolder> {

    FirebaseStorage storage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private StorageReference mStroageReference;

    public EventListAdapter(@NonNull FirestorePagingOptions<EventEntity> options, Context context, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull EventEntity item) {
        holder.bind(item);

        item.setEventId(this.getItem(position).getId());
        String idEvent = item.getEventId();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = mFirestore.collection("events")
                .document(idEvent)
                .collection("participants")
                .document(mCurrentUserId);

        //Check if user is participating to a trip or not , and keep the button in the right color
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.btParticipate.setSelected(true);
                        holder.btParticipate.setText("Ne Plus participer");
                        holder.btParticipate.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    }
                } else {
                    Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
                    holder.btParticipate.setText("Ne Plus participer");
                    holder.btParticipate.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    mFirestore.collection("users").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String username = documentSnapshot.getString("username");
                            String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                            String firstname = documentSnapshot.getString("firstname");
                            String lastname = documentSnapshot.getString("lastname");
                            String descripition = documentSnapshot.getString("description");
                            UserEntity userEntity = new UserEntity(username, imageProfileUrl, firstname, lastname, descripition, false);
                            mFirestore.collection("events")
                                    .document(idEvent)
                                    .collection("participants")
                                    .document(mCurrentUserId)
                                    .set(userEntity, SetOptions.merge());
                        }
                    });

                    mFirestore.collection("events").document(idEvent).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String titleEvent = documentSnapshot.getString("titleEvent");
                            String adressEvent = documentSnapshot.getString("adressEvent");
                            Date dateStartEvent = documentSnapshot.getDate("dateStartEvent");
                            Date dateEndEvent = documentSnapshot.getDate("dateEndEvent");
                            String imageEvent = documentSnapshot.getString("imageEvent");
                            String routeEventImage = documentSnapshot.getString("routeEventImage");
                            String descriptionEvent = documentSnapshot.getString("descriptionEvent");
                            EventEntity eventEntity = new EventEntity(dateStartEvent, dateEndEvent, titleEvent, descriptionEvent, imageEvent, routeEventImage, adressEvent);
                            mFirestore.collection("users")
                                    .document(mCurrentUserId)
                                    .collection("participateTo")
                                    .document(idEvent)
                                    .set(eventEntity, SetOptions.merge());
                        }
                    });
                    i++;
                    //unparticipate
                } else if (i == 1) {
                    holder.btParticipate.setSelected(false);
                    holder.btParticipate.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    mFirestore.collection("users")
                            .document(mCurrentUserId)
                            .collection("participateTo")
                            .document(idEvent)
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                    mFirestore.collection("events")
                            .document(idEvent)
                            .collection("participants")
                            .document(mCurrentUserId)
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                    i = 0;
                }
            }
        });
        holder.mImvRouteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("events").document(idEvent).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String adressEvent = documentSnapshot.getString("adressEvent");
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + adressEvent + "France");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(mapIntent);
                    }
                });
            }
        });
        mFirestore.collection("events").document(idEvent).collection("createdBy")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageProfileUrl = document.getData().get("imageProfileUrl").toString();
                                setImage(imageProfileUrl, holder.profile_image_two.getContext(), holder.profile_image_two);
                            }
                        }
                    }
                });

        mFirestore.collection("events").document(idEvent).collection("participants")
                .limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageProfileUrl = document.getData().get("imageProfileUrl").toString();
                                setImage(imageProfileUrl, holder.profile_image_one.getContext(), holder.profile_image_one);
                            }
                        }
                    }
                });

        mFirestore.collection("events").document(idEvent).collection("participants")
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
                Intent intent = new Intent(mContext, ParticipantsListActivity.class);
                intent.putExtra("ID_EVENT", idEvent);
                mContext.startActivity(intent);
            }
        });
        holder.profile_image_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ParticipantsListActivity.class);
                intent.putExtra("ID_EVENT", idEvent);
                mContext.startActivity(intent);
            }
        });
        holder.profile_image_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ParticipantsListActivity.class);
                intent.putExtra("ID_EVENT", idEvent);
                mContext.startActivity(intent);
            }
        });
        holder.imageButtonInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageButtonInformation.setVisibility(View.GONE);
                holder.imageButtonCancelInformation.setVisibility(View.VISIBLE);
                holder.tvDescriptionInfoEvent.setVisibility(View.VISIBLE);
                holder.mImvPhotoEvent.setVisibility(View.GONE);
                holder.tvTitleInformation.setVisibility(View.VISIBLE);
                SeeMoreText.makeTextViewResizable(holder.tvDescriptionInfoEvent, 4, mContext.getString(R.string.see_more), true);
            }
        });
        holder.imageButtonCancelInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageButtonInformation.setVisibility(View.VISIBLE);
                holder.imageButtonCancelInformation.setVisibility(View.GONE);
                holder.tvDescriptionInfoEvent.setVisibility(View.GONE);
                holder.mImvPhotoEvent.setVisibility(View.VISIBLE);
                holder.tvTitleInformation.setVisibility(View.GONE);
            }
        });
        //Edit event's photo
        holder.imgBtEditEventPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent intent = new Intent(mContext, UploadImageHelper.class);
                intent.putExtra("ID_EVENT_PHOTO", idEvent);
                activity.startActivity(intent);
            }
        });
        mStroageReference = FirebaseStorage.getInstance().getReference("images");
        /*holder.mStaffMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChooseUserActivity.class);
                intent.putExtra("ID_EVENT", idEvent);
                intent.putExtra("STAFF", "staff members");
                mContext.startActivity(intent);
            }
        });*/
    }

    private void setImage(String imageProfileUrl, Context context, ImageView image) {
        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .override(250, 250);
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(imageProfileUrl);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Uri downloadUrl = uri;
                String urlImage = downloadUrl.toString();
                Glide.with(context)
                        .asBitmap()
                        .apply(myOptions)
                        .load(urlImage)
                        .into(image);
            }
        });
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(view);
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

    class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitleEvent)
        TextView mTextTitle;
        @BindView(R.id.tvDateEvent)
        TextView mTextDate;
        @BindView(R.id.tvDescriptionEvent)
        TextView tvDescriptionInfoEvent;
        @BindView(R.id.tvTitileInformation)
        TextView tvTitleInformation;
        @BindView(R.id.tvMoreEvent)
        TextView mtvMore;
        @BindView(R.id.tvStaff)
        TextView mStaffMember;
        @BindView(R.id.ivRouteEvent)
        ImageView mImvRouteImage;
        @BindView(R.id.ivPhotoTitleEvent)
        ImageView mImvPhotoEvent;
        @BindView(R.id.btParticipateEvent)
        Button btParticipate;
        @BindView(R.id.imageButtonInformation)
        ImageButton imageButtonInformation;
        @BindView(R.id.imageButtonCancelInformation)
        ImageButton imageButtonCancelInformation;
        @BindView(R.id.imgBtEditEvent)
        ImageButton imgBtEditEventPhoto;
        @BindView(R.id.profile_image_one_event)
        ImageView profile_image_one;
        @BindView(R.id.profile_image_two_event)
        ImageView profile_image_two;
        @BindView(R.id.profile_image_three_event)
        ImageView profile_image_three;
        private View mView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull EventEntity item) {
            mTextTitle.setText(item.getTitleEvent());
            SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
            mTextDate.setText(format.format(item.getDateStartEvent().getTime()) + "\n " + format.format(item.getDateEndEvent().getTime()));
            tvDescriptionInfoEvent.setText(item.getDescriptionEvent());
            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(250, 250);

            Glide.with(mImvRouteImage.getContext())
                    .asBitmap()
                    .apply(myOptions)
                    .load(item.getRouteEventImage())
                    .into(mImvRouteImage);
            storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(item.getImageEvent());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUrl = uri;
                    String urlImage = downloadUrl.toString();
                    Glide.with(mImvPhotoEvent.getContext())
                            .asBitmap()
                            .apply(myOptions)
                            .load(urlImage)
                            .into(mImvPhotoEvent);

                }
            });
        }
    }
}
