package fr.bigsis.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.ParticipantsListActivity;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.helpers.FirestoreHelper;

public class EventddListAdapter extends FirestoreRecyclerAdapter<EventEntity, EventddListAdapter.EventListViewHolder> {
    private OnItemClickListener listener;
    private OnLongClickListener mListener;
    private FirebaseFirestore mFirestore;
    private Context mContext;

    public EventddListAdapter(@NonNull FirestoreRecyclerOptions<EventEntity> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventListViewHolder holder, int position, @NonNull EventEntity model) {
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mCurrentUserId = mAuth.getCurrentUser().getUid();
        DocumentSnapshot r = getSnapshots().getSnapshot(position);
        String idEvent = r.getId();
        String name = r.getString("title");
        holder.textViewTitleEvent.setText(model.getTitleEvent());
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
        holder.textViewDateEvent.setText(format.format(model.getDateStart().getTime()));
      /*  if(!model.getImage().equals("")) {
            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(250, 250);
            Glide.with(holder.eventImage.getContext())
                    .asBitmap()
                    .apply(myOptions)
                    .load(model.getImage())
                    .into(holder.eventImage);
        }*/

        mFirestore.collection("events").document(idEvent).collection("participants")
                .whereEqualTo("creator", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageProfileUrl = document.getData().get("imageProfileUrl").toString();
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
                            }
                        }
                    }
                });

        FirestoreHelper.getImageProfile("events", idEvent,"participants", false,
                "imageProfileUrl", holder.profile_image_one.getContext(), holder.profile_image_one);


       /* mFirestore.collection("events").document(idEvent).collection("participants")
                .limit(1).whereEqualTo("creator", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageProfileUrl = document.getData().get("imageProfileUrl").toString();
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageProfileUrl);
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
                });*/

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
                            holder.tvMore_event.setText("+" + (count - 2));
                            if (count < 2) {
                                holder.profile_image_one.setVisibility(View.GONE);
                                holder.tvMore_event.setText("...");
                            }
                        }
                    }
                });

        //SET BUTTONS(QUIT + NOTIFICATIONS) VISIBLE
        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            int i = 0;
            @Override
            public boolean onLongClick(View v) {
                if (i == 0) {
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    i++;
                } else if (i == 1) {
                    holder.linearLayout.setVisibility(View.GONE);
                    i = 0;
                }
                return true;
            }
        });*/

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

    private void goToParticipantActivity(String id) {
        Intent intent = new Intent(mContext, ParticipantsListActivity.class);
        intent.putExtra("ID_EVENT", id);
        mContext.startActivity(intent);
    }
    @NonNull
    @Override
    public EventListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list,
                parent, false);
        return new EventListViewHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnLongClickListener {
        void setOnLongClickListener(DocumentSnapshot documentSnapshot, int position);
    }

    public class EventListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitleEvent;
        TextView textViewDateEvent;
        TextView tvShowStaff;
        TextView tvMore_event;
        ImageView profile_image_one;
        ImageView profile_image_two;
        ImageView profile_image_three;
        ImageView eventImage;
        Button btParticipateEvent;

          EventListViewHolder(View itemView) {
            super(itemView);
            textViewTitleEvent = itemView.findViewById(R.id.titleEventItem);
            textViewDateEvent = itemView.findViewById(R.id.textViewDateEvent);
            eventImage = itemView.findViewById(R.id.eventImage);
            tvShowStaff = itemView.findViewById(R.id.tvShowStaff);
            tvMore_event = itemView.findViewById(R.id.tvMore_event);
            profile_image_one = itemView.findViewById(R.id.profile_image_one_event);
            profile_image_two = itemView.findViewById(R.id.profile_image_two_event);
            profile_image_three = itemView.findViewById(R.id.profile_image_three_event);
            btParticipateEvent = itemView.findViewById(R.id.btParticipateEvent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                        Toast.makeText(v.getContext(),
                                "Position: " + position, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}



