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
import fr.bigsis.android.entity.OrganismEntity;
import fr.bigsis.android.helpers.FirestoreHelper;
import fr.bigsis.android.viewModel.ChooseParticipantViewModel;

public class ChooseParticipantAdapter extends FirestorePagingAdapter<OrganismEntity, ChooseParticipantAdapter.ChooseParticipantHolder> {
    String selectedGroup;
    String organism;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private ChooseParticipantViewModel viewModel;

    public ChooseParticipantAdapter(@NonNull FirestorePagingOptions<OrganismEntity> options, Context context, String organism) {
        super(options);
        mContext = context;
        this.organism = organism;
        viewModel = ViewModelProviders.of((FragmentActivity) mContext).get(ChooseParticipantViewModel.class);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChooseParticipantHolder holder, int position, @NonNull OrganismEntity item) {
        holder.bind(item);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        item.setIdGroup(this.getItem(position).getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setParticipant(item.getGroupName());
                selectedGroup = item.getIdGroup();
                notifyDataSetChanged();
            }
        });
        if (item.getIdGroup() == selectedGroup) {
            holder.mTextName.setSelected(true);

        } else {
            holder.mTextName.setSelected(false);
        }
    }

    @NonNull
    @Override
    public ChooseParticipantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_choose_group, parent, false);
        return new ChooseParticipantHolder(view);
    }


    class ChooseParticipantHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameGroup)
        TextView mTextName;
        private View mView;
        public ChooseParticipantHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            ButterKnife.bind(this, itemView);
        }
        public void bind(@NonNull OrganismEntity item) {
            mTextName.setText(item.getGroupName());
            viewModel.getParticipant().observe((FragmentActivity)mContext, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if (viewModel.getParticipant().getValue().equals("Tous les campus")) {
                        mTextName.setSelected(false);
                    }
                }
            });
        }
    }
}
