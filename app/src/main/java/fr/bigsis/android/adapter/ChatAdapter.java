package fr.bigsis.android.adapter;

import android.app.Activity;
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
import android.widget.RelativeLayout;
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

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatEntity, ChatAdapter.ChatHolder> {

    private static final int SENT = 0;
    private static final int RECEIVED = 1;
    private GroupConversationAdapter.OnItemClickListener listener;
    private GroupConversationAdapter.OnLongClickListener mListener;
    private FirebaseFirestore mFirestore;
    private Context mContext;
    private String userId;
    ChatViewModel viewModel;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatEntity> options, Context mContext, String userId) {
        super(options);
        this.mContext = mContext;
        this.userId = userId;
        viewModel = ViewModelProviders.of((FragmentActivity) mContext).get(ChatViewModel.class);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull ChatEntity model) {
        String username = model.getUsername();
        String idGroup = model.getId();
        DocumentSnapshot r = getSnapshots().getSnapshot(position);
        String idMessage = r.getId();
        viewModel.setIdMessage(idMessage);
        mFirestore = FirebaseFirestore.getInstance();
        //FirestoreHelper.update("GroupChat", id, "participants");

        if(!model.getMessage().equals("")) {
            holder.message.setText(model.getMessage());
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.FRENCH);
        holder.tvDateMessage.setText(username + " " + format.format(model.getDate().getTime()));
        String imageProfileUrl = model.getImageUSer();
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

        //SHOW BUTTONS FOR ADMIN
        mFirestore.collection("GroupChat")
                .document(idGroup)
                .collection("participants")
                .document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Boolean isAdmin = documentSnapshot.getBoolean("admin");
                if (isAdmin == true) {
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

                if (model.isTagged() == true) {
                    mFirestore.collection("GroupChat")
                            .document(idGroup)
                            .collection("messageTagged")
                            .document(idMessage)
                            .delete();
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
        //TAG MESSAGE
        holder.tvTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("GroupChat")
                        .document(idGroup)
                        .collection("chat")
                        .document(idMessage)
                        .update("tagged", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirestore.collection("GroupChat")
                                .document(idGroup)
                                .collection("messageTagged")
                                .document(idMessage)
                                .set(model, SetOptions.merge());
                        holder.linearLayout.setVisibility(View.GONE);
                    }
                });
            }
        });
        mFirestore.collection("GroupChat")
                .document(idGroup)
                .collection("chat")
                .document(idMessage)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Boolean isTagged = documentSnapshot.getBoolean("tagged");
                if (isTagged == true) {
                            holder.imageViewTag.setVisibility(View.VISIBLE);
                }
            }
        });
        //UNTAG MESSAGE
        holder.imageViewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View dialogView = inflater.inflate(R.layout.style_alert_dialog, null);
                TextView tvTitle = dialogView.findViewById(R.id.tvTitleDialog);
                tvTitle.setText(R.string.untag);
                Button btNo = dialogView.findViewById(R.id.btNo);
                Button btYes = dialogView.findViewById(R.id.btDeleteFriend);
                btYes.setText(R.string.yes);
                btYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFirestore.collection("GroupChat")
                                .document(idGroup)
                                .collection("chat")
                                .document(idMessage)
                                .update("tagged", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFirestore.collection("GroupChat")
                                        .document(idGroup)
                                        .collection("messageTagged")
                                        .document(idMessage)
                                        .delete();
                            }
                        });
                        holder.imageViewTag.setVisibility(View.GONE);
                        dialogBuilder.dismiss();
                    }
                });
                btNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                        holder.linearLayout.setVisibility(View.GONE);
                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
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
        ImageView imageViewTag;
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
