package fr.bigsis.android.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.ChatEntity;
import fr.bigsis.android.helpers.FirestoreHelper;
import fr.bigsis.android.viewModel.ChatViewModel;

public class AlertChatAdapter extends FirestoreRecyclerAdapter<ChatEntity, AlertChatAdapter.AlertChatHolder> {

    private static final int SENT = 0;
    private static final int RECEIVED = 1;
    private GroupConversationAdapter.OnItemClickListener listener;
    private GroupConversationAdapter.OnLongClickListener mListener;
    private FirebaseFirestore mFirestore;
    private Context mContext;
    private String userId;
    ChatViewModel viewModel;

    public AlertChatAdapter(@NonNull FirestoreRecyclerOptions<ChatEntity> options, Context mContext, String userId) {
        super(options);
        this.mContext = mContext;
        this.userId = userId;
        viewModel = ViewModelProviders.of((FragmentActivity) mContext).get(ChatViewModel.class);
    }

    @Override
    protected void onBindViewHolder(@NonNull AlertChatHolder holder, int position, @NonNull ChatEntity model) {
        String username = model.getUsername();
        String idGroup = model.getId();
        String organism = model.getOrganism();
        DocumentSnapshot r = getSnapshots().getSnapshot(position);
        String idMessage = r.getId();
        viewModel.setIdMessage(idMessage);
        mFirestore = FirebaseFirestore.getInstance();
        FirestoreHelper.compareForParticipants("AllChatGroups", "Participants");

        if(!model.getMessage().equals("")) {
            holder.message.setText(model.getMessage());
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.FRENCH);
        holder.tvDateMessage.setText(username + " " + format.format(model.getDate().getTime()));
        String imageProfileUrl = model.getImageUSer();
        if(imageProfileUrl != null) {
            StorageReference storageRefImageProfile = FirebaseStorage.getInstance().getReferenceFromUrl(imageProfileUrl);
            storageRefImageProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUrl = uri;
                    String urlImage = downloadUrl.toString();
                    Glide.with(holder.imgViewUser.getContext())
                            .load(urlImage)
                            .into(holder.imgViewUser);
                }
            });
        }



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
        //COPY TEXT
        holder.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) mContext
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("textCopied", holder.message.getText());
                clipboard.setPrimaryClip(clip);
                holder.linearLayout.setVisibility(View.GONE);
            }
        });
        //DELETE MESSAGE
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSnapshots().getSnapshot(position).getReference().delete();
                holder.linearLayout.setVisibility(View.GONE);

                if (model.isTagged()) {
                    mFirestore.collection(organism)
                            .document("AllCampus")
                            .collection("AllChatGroups")
                            .document(idGroup)
                            .collection("MessageTagged")
                            .document(idMessage)
                            .delete();
                }
                if(model.getImageMessage() != null) {
                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImageMessage());
                    photoRef.delete();
                }
            }
        });

        //SHOW IMAGE IN THE BUBBLE
        if(!model.getImageMessage().equals("") && model.getMessage().equals("")) {
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(model.getImageMessage());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUrl = uri;
                    String urlImage = downloadUrl.toString();
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.message.getLayoutParams();
                    params.width = 400;
                    params.height = 300;
                    Glide.with(holder.message.getContext())
                            .load(urlImage)
                            .into(new CustomTarget<Drawable>(300,200) {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                                {
                                    holder.message.setLayoutParams(params);
                                    holder.message.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
                                    holder.message.setGravity(Gravity.CENTER);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder)
                                {
                                    holder.message.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null);
                                }
                            });
                }
            });
        }

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
    public AlertChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
        }
        return new AlertChatHolder(view);
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

    public class AlertChatHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView tvDateMessage;
        ImageView imgViewUser;
        ImageView imageViewTag;
        LinearLayout linearLayout;
        TextView tvCopy;
        TextView tvTag;
        TextView tvDelete;

        AlertChatHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
            tvDateMessage = itemView.findViewById(R.id.tvDateMessage);
            tvCopy = itemView.findViewById(R.id.tvCopy);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            imgViewUser = itemView.findViewById(R.id.imgViewUser);
            imageViewTag = itemView.findViewById(R.id.imageViewTag);
            linearLayout = itemView.findViewById(R.id.linearLayoutContainerGroup);
            linearLayout.setVisibility(View.GONE);
            imageViewTag.setVisibility(View.GONE);

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
