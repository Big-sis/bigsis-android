package fr.bigsis.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        String username = model.getUsername();
        String idGroup = model.getId();
        mFirestore = FirebaseFirestore.getInstance();
        holder.message.setText(model.getMessage());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.FRENCH);
        holder.tvDateMessage.setText(username + " " + format.format(model.getDate().getTime()));
        String imageProfileUrl = model.getImageUSer();
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageProfileUrl);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Uri downloadUrl = uri;
                String urlImage = downloadUrl.toString();
                Glide.with(holder.imgViewUser.getContext())
                        .load(urlImage)
                        .into(holder.imgViewUser);
            }
        });

        //SHOW BUTTONS FOR ADMIN
        mFirestore.collection("GroupChat")
                .document(idGroup)
                .collection("participants")
                .document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Boolean isAdmin = documentSnapshot.getBoolean("admin");
                if(isAdmin == true){
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            holder.linearLayout.setVisibility(View.VISIBLE);
                            holder.tvDelete.setVisibility(View.VISIBLE);
                            holder.tvTag.setVisibility(View.VISIBLE);
                            return true;
                        }
                    });
                }
            }
        });

        //SHOW BUTTONS
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ChatEntity message = getItem(position);
                if (message.getSenderId().contentEquals(userId)) {
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    holder.tvDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.linearLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linearLayout.setVisibility(View.GONE);
            }
        });
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
                    R.layout.item_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
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
        LinearLayout linearLayout;
        TextView tvCopy;
        TextView tvTag;
        TextView tvDelete;

        ChatHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
            tvDateMessage = itemView.findViewById(R.id.tvDateMessage);
            tvCopy = itemView.findViewById(R.id.tvCopy);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            imgViewUser = itemView.findViewById(R.id.imgViewUser);
            linearLayout = itemView.findViewById(R.id.linearLayoutContainerGroup);

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
