package fr.bigsis.android.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.OtherUserProfileFragment;
import fr.bigsis.android.viewModel.ChooseUsersViewModel;

import static android.content.Context.MODE_PRIVATE;

public class ChooseUsersAdapter extends FirestorePagingAdapter<UserEntity, ChooseUsersAdapter.ChooseUserViewHolder> {
    FirebaseStorage storage;
    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private ChooseUsersViewModel chooseUsersViewModel;
    private FirebaseAuth mAuth;

    public ChooseUsersAdapter(@NonNull FirestorePagingOptions<UserEntity> options, Context context) {
        super(options);
        mContext = context;
        chooseUsersViewModel = ViewModelProviders.of((FragmentActivity) context).get(ChooseUsersViewModel.class);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChooseUserViewHolder holder, int position, @NonNull UserEntity item) {
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

      holder.btSelect.setOnClickListener(new View.OnClickListener() {
          int i = 0;
          @Override
          public void onClick(View v) {
              if (i == 0) {
                  holder.btSelect.setSelected(true);
                  holder.btSelect.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));

                  mFirestore.collection("users")
                          .document(idContact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                      @Override
                      public void onSuccess(DocumentSnapshot documentSnapshot) {
                          String username = documentSnapshot.getString("username");
                          String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                          String firstname = documentSnapshot.getString("firstname");
                          String lastname = documentSnapshot.getString("lastname");
                          String description = documentSnapshot.getString("description");
                          Boolean isAdmin = documentSnapshot.getBoolean("admin");
                          UserEntity userEntity = new UserEntity(username, description, imageProfileUrl,
                                  firstname, lastname, false, isAdmin, false, idContact);
                              chooseUsersViewModel.addParticipant(userEntity);
                      }
                  });
                  i++;
                  //unrequest
              } else if (i == 1) {
                  holder.btSelect.setSelected(false);
                  holder.btSelect.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                  mFirestore.collection("users")
                          .document(idContact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                      @Override
                      public void onSuccess(DocumentSnapshot documentSnapshot) {
                          String username = documentSnapshot.getString("username");
                          String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                          String firstname = documentSnapshot.getString("firstname");
                          String lastname = documentSnapshot.getString("lastname");
                          String description = documentSnapshot.getString("description");
                          Boolean isAdmin = documentSnapshot.getBoolean("admin");
                          UserEntity userEntity = new UserEntity(username, description, imageProfileUrl,
                                  firstname, lastname, false, isAdmin, false, idContact);
                          chooseUsersViewModel.removeParticipant(userEntity);
                      }
                  });
                  i = 0;
              }

          }
      });
    }
            @NonNull
            @Override
            public ChooseUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contact_list_item, parent, false);
                return new ChooseUserViewHolder(view);
            }

             class ChooseUserViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.tvNameContact)
                TextView mTextName;
                @BindView(R.id.tvUserNameContact)
                TextView mTextUserName;
                @BindView(R.id.image_profile_contact)
                CircleImageView mImageProfile;
                @BindView(R.id.btAdd)
                Button btSelect;
                private View mView;

                public ChooseUserViewHolder(@NonNull View itemView) {
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

