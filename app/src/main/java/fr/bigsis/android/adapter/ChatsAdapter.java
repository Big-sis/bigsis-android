package fr.bigsis.android.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.ChatEntity;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {
    private static final int SENT = 0;
    private static final int RECEIVED = 1;
    private String userId;
    private List<ChatEntity> chats;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    public ChatsAdapter(List<ChatEntity> chats, String userId) {
        this.chats = chats;
        this.userId = userId;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.bind(chats.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (chats.get(position).getSenderId().contentEquals(userId)) {
            return SENT;
        } else {
            return RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ImageView imgViewUser;

        public ChatViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
            imgViewUser = itemView.findViewById(R.id.imgViewUser);
        }

        public void bind(ChatEntity chat) {
            mFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mCurrentUserId = mAuth.getCurrentUser().getUid();

            message.setText(chat.getMessage());

            mFirestore.collection("users").document(mCurrentUserId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageProfileUrl);
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    String urlImage = downloadUrl.toString();
                                    Glide.with(imgViewUser.getContext())
                                            .load(urlImage)
                                            .into(imgViewUser);
                                }
                            });
                        }
                    });
        }
        }
    }


