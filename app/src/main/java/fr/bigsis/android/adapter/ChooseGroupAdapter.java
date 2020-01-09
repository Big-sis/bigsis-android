package fr.bigsis.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.activity.MapsActivity;
import fr.bigsis.android.activity.UserProfileActivity;
import fr.bigsis.android.entity.OrganismEntity;
import fr.bigsis.android.fragment.ProfileFragment;
import fr.bigsis.android.helpers.FirestoreHelper;
import fr.bigsis.android.viewModel.ChooseParticipantViewModel;

public class ChooseGroupAdapter extends FirestorePagingAdapter<OrganismEntity, ChooseGroupAdapter.ChooseGroupViewHolder> {
    String selectedGroup;
    Button finish;
    String organism;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;

    public ChooseGroupAdapter(@NonNull FirestorePagingOptions<OrganismEntity> options, Context context, Button finish, String organism) {
        super(options);
        mContext = context;
        this.finish = finish;
        this.organism = organism;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChooseGroupViewHolder holder, int position, @NonNull OrganismEntity item) {
        holder.bind(item);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        item.setIdGroup(this.getItem(position).getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish.setVisibility(View.VISIBLE);
                selectedGroup = item.getIdGroup();
                notifyDataSetChanged();
            }
        });
        if (item.getIdGroup() == selectedGroup) {
            holder.mTextName.setSelected(true);
            clickFinish(organism, mCurrentUserId, item.getGroupName());
        } else {
            holder.mTextName.setSelected(false);
        }
    }

    @NonNull
    @Override
    public ChooseGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_choose_group, parent, false);
        return new ChooseGroupViewHolder(view);
    }

    public void clickFinish(String organism, String idUser, String groupName) {
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("USERS").document(idUser).update("groupCampus", groupName);
                FirestoreHelper.setDataUserInCampus(idUser);

                mFirestore.collection(organism).document("AllCampus").collection("AllUsers")
                        .document(idUser).update("groupCampus", groupName).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mContext.startActivity(new Intent(mContext, UserProfileActivity.class));
                    }
                });
            }
        });
    }

    class ChooseGroupViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameGroup)
        TextView mTextName;
        private View mView;
        public ChooseGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            ButterKnife.bind(this, itemView);
        }
        public void bind(@NonNull OrganismEntity item) {
            mTextName.setText(item.getGroupName());
        }
    }
}
