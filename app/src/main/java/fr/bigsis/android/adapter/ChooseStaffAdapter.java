package fr.bigsis.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.OtherUserProfileFragment;
import fr.bigsis.android.viewModel.ChooseUsersViewModel;

public class ChooseStaffAdapter extends FirestorePagingAdapter<UserEntity, ChooseStaffAdapter.ChooseStaffViewHolder> {
    FirebaseStorage storage;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private ChooseUsersViewModel chooseUsersViewModel;
    private FirebaseAuth mAuth;
    private String nameCampus;
    private String organism;
    private String id;

    public ChooseStaffAdapter(@NonNull FirestorePagingOptions<UserEntity> options, Context context, String nameCampus, String organism, String id) {
        super(options);
        mContext = context;
        this.nameCampus = nameCampus;
        this.organism = organism;
        this.id = id;
        chooseUsersViewModel = ViewModelProviders.of((FragmentActivity) context).get(ChooseUsersViewModel.class);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChooseStaffViewHolder holder, int position, @NonNull UserEntity item) {
        holder.bind(item);
        item.setUserId(this.getItem(position).getId());
        String idContact = item.getUserId();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        if (mCurrentUserId.equals(idContact)) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            param.height = 0;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            holder.itemView.setVisibility(View.VISIBLE);
        }
        /*
        //GO TO PROFILE
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("idString", idContact);
                OtherUserProfileFragment myFragment = new OtherUserProfileFragment();
                myFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top)
                        .addToBackStack(null)
                        .add(R.id.fragment_container_contact, myFragment, "PROFILE_OTHER_USER_FRAGMENT")
                        .commit();

            }
        });*/
      if(organism != null) {
          CollectionReference documentReference = mFirestore.collection(organism).document("AllCampus").collection("AllEvents")
                  .document(id)
                  .collection("StaffMembers");
          documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      for (QueryDocumentSnapshot document : task.getResult()) {
                          String idDoc = document.getId();
                          if (idContact.equals(idDoc)) {
                              holder.btSelect.setSelected(true);
                          }
                      }
                  }
              }
          });
      }

        holder.btSelect.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    holder.btSelect.setSelected(true);
                    String username = item.getUsername();
                    String imageProfileUrl = item.getImageProfileUrl();
                    String firstname = item.getFirstname();
                    String lastname = item.getLastname();
                    String description = item.getDescription();
                    Boolean isAdmin = item.isAdmin();
                    String groupNameUser = item.getGroupCampus();
                    String organism = item.getOrganism();
                    UserEntity userEntity = new UserEntity(username, description,
                            imageProfileUrl, firstname, lastname, groupNameUser, organism, idContact, isAdmin);
                    chooseUsersViewModel.addStaffMember(userEntity);

                    i++;
                    //unrequest
                } else if (i == 1) {
                    holder.btSelect.setSelected(false);
                    String username = item.getUsername();
                    String imageProfileUrl = item.getImageProfileUrl();
                    String firstname = item.getFirstname();
                    String lastname = item.getLastname();
                    String description = item.getDescription();
                    Boolean isAdmin = item.isAdmin();
                    String groupNameUser = item.getGroupCampus();
                    String organism = item.getOrganism();
                    UserEntity userEntity = new UserEntity(username, description,
                            imageProfileUrl, firstname, lastname, groupNameUser, organism, idContact, isAdmin);
                    chooseUsersViewModel.removeStaffMember(userEntity);

                    i = 0;
                }
            }
        });
    }

    @NonNull
    @Override
    public ChooseStaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ChooseStaffViewHolder(view);
    }

    class ChooseStaffViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNameContact)
        TextView mTextName;
        @BindView(R.id.tvUserNameContact)
        TextView mTextUserName;
        @BindView(R.id.image_profile_contact)
        CircleImageView mImageProfile;
        @BindView(R.id.btAdd)
        Button btSelect;
        private View mView;

        public ChooseStaffViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull UserEntity item) {
            mTextName.setText(item.getFirstname() + " " + item.getLastname());
            mTextUserName.setText(item.getUsername());
            btSelect.setText("Sélectionner");
            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(250, 250);
            if (item.getImageProfileUrl() != null) {
                storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl(item.getImageProfileUrl());
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        String urlImage = downloadUrl.toString();
                        Glide.with(mImageProfile.getContext())
                                .asBitmap()
                                .apply(myOptions)
                                .load(urlImage)
                                .into(mImageProfile);
                    }
                });
            }
        }
    }
}
