package fr.bigsis.android.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.helpers.FirestoreHelper;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private OnFragmentInteractionListenerProfile mListener;
    private TextView tvUserNameFragment;
    private TextView tvUserDescFragment;
    private FloatingActionButton fbEdit;
    private String userId, user_name, description_user, imageProfileUrl;
    private String firstname, lastname;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private Uri imageProfileUri;
    private CircleImageView circleImageView;
    private StorageReference mStroageReference;
    private int STORAGE_PERMISSION_CODE = 1;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        userId = mFirebaseAuth.getCurrentUser().getUid();

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
                .document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        firstname = documentSnapshot.getString("firstname");
                        lastname = documentSnapshot.getString("lastname");
                        description_user = documentSnapshot.getString("description");
                        imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        tvUserNameFragment.setText(firstname + " " + lastname);
                        tvUserDescFragment.setText(description_user);
                        StorageReference storageRef = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageProfileUrl);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUserNameFragment = view.findViewById(R.id.tvUserNameFragment);
        tvUserDescFragment = view.findViewById(R.id.tvUserDescriptionFragment);
        fbEdit = view.findViewById(R.id.fbEditPicture);
        circleImageView = view.findViewById(R.id.profile_image);
        fbEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        });
        mStroageReference = FirebaseStorage.getInstance().getReference("images");
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             setImageUser();
            }
        }
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionProfile();
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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void setImageUser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageProfileUri = data.getData();
            Glide.with(this)
                    .load(imageProfileUri)
                    .into(circleImageView);

            final StorageReference imgReference = mStroageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageProfileUri));

            String link = imgReference.toString();
            if (imageProfileUri != null) {
                imgReference.putFile(imageProfileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        String user_id = mFirebaseAuth.getCurrentUser().getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document(user_id)
                                .update("imageProfileUrl", link).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Votre image a bien été modifiée",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        FirestoreHelper.updateUserProfile(user_id, "users", user_id, "Friends", user_id,"imageProfileUrl");
                    }
                });
            }
        }
    }

    public interface OnFragmentInteractionListenerProfile {
        void onFragmentInteractionProfile();
    }
}
