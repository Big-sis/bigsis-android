package fr.bigsis.android.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherUserProfileFragment extends Fragment {

    private static final String ID_USER = "idUSer" ;
    private String idUserProfile;

    private OnFragmentInteractionListenerProfile mListener;
    private TextView tvUserName;
    private TextView tvUserDescription;
    private String userId, user_name, description_user, imageProfileUrl;
    private String firstname, lastname;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUserProfile = getArguments().getString(ID_USER);
            mFirebaseAuth = FirebaseAuth.getInstance();
            userId = mFirebaseAuth.getCurrentUser().getUid();

            mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("users")
                    .document(idUserProfile).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            firstname = documentSnapshot.getString("firstname");
                            lastname = documentSnapshot.getString("lastname");
                            description_user = documentSnapshot.getString("description");
                            imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                            tvUserName.setText(firstname + " " + lastname);
                            tvUserDescription.setText(description_user);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUserName = view.findViewById(R.id.tvOtherUserNameFragment);
        tvUserDescription = view.findViewById(R.id.tvOtherUserDescriptionFragment);
        circleImageView = view.findViewById(R.id.profile_image);
        mStroageReference = FirebaseStorage.getInstance().getReference("images");
        return view;
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
