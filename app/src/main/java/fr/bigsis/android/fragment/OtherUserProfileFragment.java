package fr.bigsis.android.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;

public class OtherUserProfileFragment extends Fragment {

    private static final String ID_USER = "idUSer";
    private OnFragmentInteractionListenerProfile mListener;
    private TextView tvUserDescription, tvUserName;
    private String mCurrentUserId;
    private String firstname, lastname, descriptionUser, imageProfileUrl;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private Uri imageProfileUri;
    private CircleImageView circleImageView;
    private StorageReference mStroageReference;
    private ImageButton imBt_add_friend;

    public OtherUserProfileFragment() {
    }

    public static OtherUserProfileFragment newInstance(String idUserProfile) {
        OtherUserProfileFragment fragment = new OtherUserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ID_USER, idUserProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_user_profile, container, false);
        tvUserName = view.findViewById(R.id.tvOtherUserNameFragment);
        tvUserDescription = view.findViewById(R.id.tvOtherUserDescriptionFragment);
        circleImageView = view.findViewById(R.id.profile_image);
        imBt_add_friend = view.findViewById(R.id.imBt_add_friend);
        mStroageReference = FirebaseStorage.getInstance().getReference("images");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mFirebaseAuth.getCurrentUser().getUid();
        String idContact = getArguments().getString("idString");
        imBt_add_friend.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View v) {
                    if (i == 0) {

                        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String username = documentSnapshot.getString("username");
                                String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                                String description = documentSnapshot.getString("description");
                                String firstname = documentSnapshot.getString("firstname");
                                String lastname = documentSnapshot.getString("lastname");
                                String nameCampus = documentSnapshot.getString("groupCampus");
                                String organism = documentSnapshot.getString("organism");
                                Boolean admin = documentSnapshot.getBoolean("admin");
                                String lastnameAndFirstname = lastname + " " + firstname;

                                UserEntity userEntity = new UserEntity(username, description, imageProfileUrl, firstname, lastname, admin, nameCampus, organism, lastnameAndFirstname);
                                mFirestore.collection("USERS")
                                        .document(idContact)
                                        .collection("RequestReceived")
                                        .document(mCurrentUserId)
                                        .set(userEntity, SetOptions.merge());
                            }
                        });
                        mFirestore.collection("USERS").document(idContact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String username = documentSnapshot.getString("username");
                                String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                                String description = documentSnapshot.getString("description");
                                String firstname = documentSnapshot.getString("firstname");
                                String lastname = documentSnapshot.getString("lastname");
                                String nameCampus = documentSnapshot.getString("groupCampus");
                                String organism = documentSnapshot.getString("organism");
                                Boolean admin = documentSnapshot.getBoolean("admin");
                                String lastnameAndFirstname = lastname + " " + firstname;
                                UserEntity userEntity = new UserEntity(username, description, imageProfileUrl, firstname, lastname, admin, nameCampus, organism, lastnameAndFirstname);
                                mFirestore.collection("USERS")
                                        .document(mCurrentUserId)
                                        .collection("RequestSent")
                                        .document(idContact)
                                        .set(userEntity, SetOptions.merge());
                            }
                        });
                        i++;
                        //unrequest
                    } else if (i == 1) {

                        mFirestore.collection("USERS")
                                .document(idContact)
                                .collection("RequestReceived")
                                .document(mCurrentUserId)
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //TODO EXTRACT STRING
                                Snackbar.make(v, "Invitation annulée", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });

                        mFirestore.collection("USERS")
                                .document(mCurrentUserId)
                                .collection("RequestSent")
                                .document(idContact)
                                .delete();
                        i = 0;
                    }
            }
        });
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS")
                .document(idContact)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        firstname = documentSnapshot.getString("firstname");
                        lastname = documentSnapshot.getString("lastname");
                        descriptionUser = documentSnapshot.getString("description");
                        imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        tvUserName.setText(firstname + " " + lastname);
                        tvUserDescription.setText(descriptionUser);
                        if(imageProfileUrl != null) {
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageProfileUrl);
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    String urlImage = downloadUrl.toString();
                                    Glide.with(getActivity())
                                            .load(urlImage)
                                            .into(circleImageView);
                                }
                            });
                        }
                    }
                });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionOtherProfile();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerProfile) {
            mListener = (OnFragmentInteractionListenerProfile) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListenerProfile {
        void onFragmentInteractionOtherProfile();
    }
}
