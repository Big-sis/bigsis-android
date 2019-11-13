package fr.bigsis.android.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;

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
        mStroageReference = FirebaseStorage.getInstance().getReference("images");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mFirebaseAuth.getCurrentUser().getUid();
        String idContact = getArguments().getString("idString");

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
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
