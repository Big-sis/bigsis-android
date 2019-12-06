package fr.bigsis.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.ChatEntity;

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatEntity, ChatAdapter.ChatHolder> {
    private static final int SENT = 0;
    private static final int RECEIVED = 1;
    private GroupConversationAdapter.OnItemClickListener listener;
    private GroupConversationAdapter.OnLongClickListener mListener;
    private FirebaseFirestore mFirestore;
    private Context mContext;
    private String userId;


    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatEntity> options, Context mContext, String userId) {
        super(options);
        this.mContext = mContext;
        this.userId = userId;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull ChatEntity model) {
        DocumentSnapshot r = getSnapshots().getSnapshot(position);
        String id = r.getId();
        String username = model.getUsername();
        mFirestore = FirebaseFirestore.getInstance();
       /* mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();*/
        holder.message.setText(model.getMessage());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.FRENCH);
        holder.tvDateMessage.setText(username + " " + format.format(model.getDate().getTime()));
    }

    @Override
    public int getItemViewType(int position) {
        ChatEntity message = getItem(position);
        if (message.getSenderId().contentEquals(userId)) {
            return SENT;
        } else {
            return RECEIVED;
        }
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_chat_sent,
                    parent,
                    false
            );
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_chat_received,
                    parent,
                    false);
        }
        return new ChatHolder(view);
    }


    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void setOnItemClickListener(GroupConversationAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public interface OnLongClickListener {
        void setOnLongClickListener(DocumentSnapshot documentSnapshot, int position);
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView tvDateMessage;

        ImageView imgViewUser;

        ChatHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
            tvDateMessage = itemView.findViewById(R.id.tvDateMessage);
            imgViewUser = itemView.findViewById(R.id.imgViewUser);

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
