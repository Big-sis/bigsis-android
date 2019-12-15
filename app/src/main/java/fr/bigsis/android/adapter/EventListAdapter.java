package fr.bigsis.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.ParticipantsListActivity;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.FirestoreHelper;

public class EventListAdapter extends FirestoreRecyclerAdapter<EventEntity, EventListAdapter.EventHolder> {
    private OnItemClickListener listener;
    private OnLongClickListener mListener;
    private FirebaseFirestore mFirestore;
    private Context mContext;

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
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
        holder.textViewDateEvent.setText(format.format(model.getDateStart()));

        if(!model.getImage().equals("")) {
            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(250, 250);
            Glide.with(holder.eventImage.getContext())
                    .asBitmap()
                    .apply(myOptions)
                    .load(model.getImage())
                    .into(holder.eventImage);
        }
        FirestoreHelper.getCountOfParticipants("events", idEvent, "participants",
                holder.tvMore_event, holder.profile_image_one);

        FirestoreHelper.getImageProfile("events", idEvent,"participants", false,
                "imageProfileUrl", holder.profile_image_one.getContext(),
                holder.profile_image_one);

        FirestoreHelper.getImageProfile("events", idEvent,"participants", true,
                "imageProfileUrl", holder.profile_image_two.getContext(),
                holder.profile_image_two);

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
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list,
                parent, false);
        return new EventHolder(v);
    }
    /*public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }*/


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnLongClickListener {
        void setOnLongClickListener(DocumentSnapshot documentSnapshot, int position);
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDateEvent;
        TextView tvShowStaff;
        TextView tvMore_event;
        ImageView profile_image_one;
        ImageView profile_image_two;
        ImageView profile_image_three;
        ImageView eventImage;
        Button btParticipateEvent;

        EventHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleEventItem);
            textViewDateEvent = itemView.findViewById(R.id.textViewDateEvent);
            eventImage = itemView.findViewById(R.id.eventImage);
            tvShowStaff = itemView.findViewById(R.id.tvShowStaff);
            tvMore_event = itemView.findViewById(R.id.tvMore_event);
            profile_image_one = itemView.findViewById(R.id.profile_image_one_event);
            profile_image_two = itemView.findViewById(R.id.profile_image_two_event);
            profile_image_three = itemView.findViewById(R.id.profile_image_three_event);
            btParticipateEvent = itemView.findViewById(R.id.btParticipateEvent);
        }
    }}

