package fr.bigsis.android.adapter;

import android.content.Context;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.ParticipantsListActivity;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.FirestoreHelper;
import fr.bigsis.android.helpers.UploadImageHelper;
import fr.bigsis.android.view.SeeMoreText;

public class EventListAdapter extends FirestoreRecyclerAdapter<EventEntity, EventListAdapter.EventHolder> {
    private OnItemClickListener listener;
    private OnLongClickListener mListener;
    private FirebaseFirestore mFirestore;
    private Context mContext;
    int i = 0;

    public EventListAdapter(@NonNull FirestoreRecyclerOptions<EventEntity> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull EventEntity model) {
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mCurrentUserId = mAuth.getCurrentUser().getUid();
        DocumentSnapshot r = getSnapshots().getSnapshot(position);
        String idEvent = r.getId();
        holder.textViewTitle.setText(model.getTitleEvent());

        DocumentReference documentReference = mFirestore.collection("events").document(idEvent)
                .collection("participants")
                .document(mCurrentUserId);

        FirestoreHelper.update("events", idEvent, "participants","imageProfileUrl");
        FirestoreHelper.update("events", idEvent, "staffMembers", "imageProfileUrl");
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
        holder.textViewDateEvent.setText(format.format(model.getDateStart()));
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        i = 1;
                        holder.btParticipateEvent.setSelected(true);
                        holder.btParticipateEvent.setText("Ne plus participer");
                    } else if(!document.exists()){
                         holder.btParticipateEvent.setSelected(false);
                        i=0;
                    }
                }
            }
        });
        holder.tvDesctiptionEvent.setText(model.getDescription());
        if (!model.getImage().equals("")) {
            FirestoreHelper.getStorage(model.getImage(), holder.eventImage.getContext(), holder.eventImage );
        }
        FirestoreHelper.getCountOfParticipants("events", idEvent, "participants",
                holder.tvMore_event, holder.profile_image_one);
        FirestoreHelper.getImageProfile("events", idEvent, "participants", false,
                "imageProfileUrl", holder.profile_image_one.getContext(),
                holder.profile_image_one);
        FirestoreHelper.getImageProfile("events", idEvent, "participants", true,
                "imageProfileUrl", holder.profile_image_two.getContext(),
                holder.profile_image_two);
        mFirestore.collection("events").document(idEvent).collection("creator").document(mCurrentUserId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            holder.imageButtonImageEventItem.setVisibility(View.VISIBLE);
                            holder.btParticipateEvent.setText(R.string.modify);
                        }
                    }
                });
        holder.infoEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.relativeLayoutEventText.setVisibility(View.VISIBLE);
                    SeeMoreText.makeTextViewResizable(holder.tvDesctiptionEvent, 4, mContext.getString(R.string.see_more), true);
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
                v.setFocusable(false);
                if (i == 0) {
                   holder.btParticipateEvent.setSelected(true);
                   holder.btParticipateEvent.setText("Ne plus participer");

                    mFirestore.collection("users").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String username = documentSnapshot.getString("username");
                            String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                            String firstname = documentSnapshot.getString("firstname");
                            String lastname = documentSnapshot.getString("lastname");
                            String descripition = documentSnapshot.getString("description");
                            Boolean isAdmin = documentSnapshot.getBoolean("admin");
                            UserEntity userEntity = new UserEntity(username, descripition, imageProfileUrl, firstname, lastname, false, isAdmin);
                            FirestoreHelper.setData("events", idEvent, "participants", mCurrentUserId, userEntity);
                            FirestoreHelper.setData("GroupChat", idEvent, "participants", mCurrentUserId, userEntity);

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
                            FirestoreHelper.setData("users", mCurrentUserId, "participateToEvent", idEvent, eventEntity);
                            GroupChatEntity groupChatEntity = new GroupChatEntity(model.getTitleEvent(), model.getImage(), model.getDateStart());
                            FirestoreHelper.setData("users", mCurrentUserId, "groupChat", idEvent, groupChatEntity);
                        }
                    });
                    mFirestore.collection("events").document(idEvent).collection("creator").document(mCurrentUserId).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        holder.btParticipateEvent.setText(R.string.modify);
                                        holder.btParticipateEvent.setSelected(false);
                                        holder.btParticipateEvent.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //TODO MODIFY
                                            }
                                        });
                                    }
                                }
                            });
                i++;
                } else if (i == 1) {
                    holder.btParticipateEvent.setSelected(false);
                    holder.btParticipateEvent.setText(R.string.participer);
                    FirestoreHelper.deleteFromdb("users", mCurrentUserId, "participateTo", idEvent);
                    FirestoreHelper.deleteFromdb("events", idEvent, "participants", mCurrentUserId);
                    FirestoreHelper.deleteFromdb("users", mCurrentUserId, "groupChat",idEvent);
                    mFirestore.collection("events").document(idEvent).collection("creator").document(mCurrentUserId).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        holder.btParticipateEvent.setText(R.string.modify);
                                        holder.btParticipateEvent.setSelected(false);
                                        holder.btParticipateEvent.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //TODO MODIFY
                                            }
                                        });
                                    }
                                }
                            });
                    i = 0;
                }
            }
        });

//Edit event's photo
        holder.imageButtonImageEventItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent intent = new Intent(mContext, UploadImageHelper.class);
                intent.putExtra("ID_EVENT_PHOTO", idEvent);
                activity.startActivity(intent);
            }
        });

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

       /* holder.tvShowStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParticipantActivity(idEvent);
            }
        });*/
       holder.tvShowStaff.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(mContext, ParticipantsListActivity.class);
               intent.putExtra("STAFF", idEvent);
               mContext.startActivity(intent);
           }
       });
    }

    private void goToParticipantActivity(String id) {
        Intent intent = new Intent(mContext, ParticipantsListActivity.class);
        intent.putExtra("ID_EVENT", id);
        mContext.startActivity(intent);
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list,
                parent, false);
        return new EventHolder(v);
    }
    /*public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }*/

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public interface OnLongClickListener {
        void setOnLongClickListener(DocumentSnapshot documentSnapshot, int position);
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDateEvent;
        TextView tvShowStaff;
        TextView tvMore_event;
        TextView tvDesctiptionEvent;
        TextView tvInformations;
        ImageView profile_image_one;
        ImageView profile_image_two;
        ImageView profile_image_three;
        ImageView eventImage;
        Button btParticipateEvent;
        ImageButton imageButtonImageEventItem;
        ImageButton imgBtCancelInfo;
        FloatingActionButton infoEventButton;
        RelativeLayout relativeLayoutEventText;

        EventHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleEventItem);
            textViewDateEvent = itemView.findViewById(R.id.textViewDateEvent);
            tvDesctiptionEvent = itemView.findViewById(R.id.tvDesctiptionEvent);
            eventImage = itemView.findViewById(R.id.eventImage);
            tvShowStaff = itemView.findViewById(R.id.tvShowStaff);
            tvInformations = itemView.findViewById(R.id.tvInformations);
            tvMore_event = itemView.findViewById(R.id.tvMore_event);
            profile_image_one = itemView.findViewById(R.id.profile_image_one_event);
            profile_image_two = itemView.findViewById(R.id.profile_image_two_event);
            profile_image_three = itemView.findViewById(R.id.profile_image_three_event);
            btParticipateEvent = itemView.findViewById(R.id.btParticipateEvent);
            imageButtonImageEventItem = itemView.findViewById(R.id.imageButtonImageEventItem);
            imgBtCancelInfo = itemView.findViewById(R.id.imgBtCancelInfo);
            infoEventButton = itemView.findViewById(R.id.infoEventButton);
            relativeLayoutEventText = itemView.findViewById(R.id.relativeLayoutEventText);
        }
    }
}

