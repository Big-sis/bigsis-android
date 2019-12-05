package fr.bigsis.android.adapter;

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
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.GroupChatEntity;

public class GroupConversationAdapter extends FirestoreRecyclerAdapter<GroupChatEntity, GroupConversationAdapter.GroupChatHolder> {
    private OnItemClickListener listener;
    private OnLongClickListener mListener;
    private FirebaseFirestore mFirestore;

    public GroupConversationAdapter(@NonNull FirestoreRecyclerOptions<GroupChatEntity> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupChatHolder holder, int position, @NonNull GroupChatEntity model) {
        mFirestore = FirebaseFirestore.getInstance();
        String id = getSnapshots().getSnapshot(position).getReference().getId();
        holder.textViewTitle.setText(model.getTitle());
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
        holder.textViewDate.setText(format.format(model.getDate().getTime()));
        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .override(250, 250);
        Glide.with(holder.imgViewGroupe.getContext())
                .asBitmap()
                .apply(myOptions)
                .load(model.getImageGroup())
                .into(holder.imgViewGroupe);
        mFirestore.collection("GroupChat").document(id).collection("participants")
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
        mFirestore.collection("GroupChat").document(id).collection("participants")
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
                });
        mFirestore.collection("GroupChat").document(id).collection("participants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            holder.textViewCount.setText("+" + (count - 2));
                            if (count < 2) {
                                holder.profile_image_one.setVisibility(View.GONE);
                                holder.textViewCount.setText("...");
                            }
                        }
                    }
                });

        //SET BUTTONS(QUIT + NOTIFICATIONS) VISIBLE
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.linearLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }

    @NonNull
    @Override
    public GroupChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_conversation_item,
                parent, false);
        return new GroupChatHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public interface OnLongClickListener {
        void setOnLongClickListener(DocumentSnapshot documentSnapshot, int position);
    }

    public class GroupChatHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewLastMessage;
        TextView textViewDate;
        TextView textViewCount;
        ImageView imgViewGroupe;
        ImageView profile_image_one;
        ImageView profile_image_two;
        ImageView profile_image_three;
        LinearLayout linearLayout;

        GroupChatHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tvTitleGroupChat);
            textViewDate = itemView.findViewById(R.id.tvDateGroupChat);
            textViewCount = itemView.findViewById(R.id.textViewCountGroup);
            imgViewGroupe = itemView.findViewById(R.id.ivGroupChat);
            profile_image_one = itemView.findViewById(R.id.profile_image_one_group);
            profile_image_two = itemView.findViewById(R.id.profile_image_two_group);
            profile_image_three = itemView.findViewById(R.id.profile_image_three_group);
            linearLayout = itemView.findViewById(R.id.frameLayoutContainerGroup);

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
