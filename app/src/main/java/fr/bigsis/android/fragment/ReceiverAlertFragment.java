package fr.bigsis.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.activity.AlertChatActivity;
import fr.bigsis.android.activity.ChatActivity;
import fr.bigsis.android.activity.MapsActivity;
import fr.bigsis.android.entity.AlertEntity;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.viewModel.AlertLocateViewModel;


public class ReceiverAlertFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageButton closereceiverALert;
    private CircleImageView imageProfileUser;
    private Button btImGoing;
    private TextView nbrPeople, dateTimeOfAlert, usernameAlert;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    String userId = mFirebaseAuth.getCurrentUser().getUid();
    private AlertLocateViewModel alertLocateViewModel;
    private RelativeLayout rvProgressBar;

    public ReceiverAlertFragment() {
        // Required empty public constructor
    }

    public static ReceiverAlertFragment newInstance() {
        ReceiverAlertFragment fragment = new ReceiverAlertFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receiver_alert, container, false);

        closereceiverALert = view.findViewById(R.id.closereceiverALert);
        imageProfileUser = view.findViewById(R.id.imageProfileUser);
        btImGoing = view.findViewById(R.id.btImGoing);
        nbrPeople = view.findViewById(R.id.nbrPeople);
        dateTimeOfAlert = view.findViewById(R.id.dateTimeOfAlert);
        usernameAlert = view.findViewById(R.id.usernameAlert);
        rvProgressBar = view.findViewById(R.id.rvProgressBar);

        closereceiverALert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment alert_fragment = manager.findFragmentByTag("RECEIVER_ALERT_FRAGMENT");
                if (alert_fragment != null) {
                    ft.remove(alert_fragment).commitAllowingStateLoss();
                }
                getActivity().onBackPressed();
            }
        });

        btImGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToAlert();
            }
        });
        String idUserAlert = getArguments().getString("idUserAlert");

        if(idUserAlert != null) {
            getInformations();
        }
        return view;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    private void goToAlert() {
        String idUserAlert = getArguments().getString("idUserAlert");
        String organism = getArguments().getString("organism");
        String eventId = getArguments().getString("eventId");
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String firstname = documentSnapshot.getString("firstname");
                String lastname = documentSnapshot.getString("lastname");


                mFirestore.collection(organism)
                        .document("AllCampus").collection("AllEvents")
                        .document(eventId).collection("Alert").document(idUserAlert).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        double longitudeAlert = documentSnapshot.getDouble("longitudeAlert");
                        double latitudeAlert = documentSnapshot.getDouble("latitudeAlert");
                        alertLocateViewModel = ViewModelProviders.of(getActivity()).get(AlertLocateViewModel.class);
                        double longReceiver = alertLocateViewModel.getLongitudeAlert().getValue();
                        double latReceiver = alertLocateViewModel.getLatitudeAlert().getValue();

                        AlertEntity alertEntity = new AlertEntity(latitudeAlert, longitudeAlert, lastname, firstname, latReceiver, longReceiver);

                        mFirestore.collection(organism)
                                .document("AllCampus").collection("AllEvents")
                                .document(eventId).collection("Alert").document(idUserAlert).collection("StaffOnGoing").document(userId)
                        .set(alertEntity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        String idGroupChat = "Alert" + eventId;
                        Calendar calendar = Calendar.getInstance();
                        Date date = calendar.getTime();
                        GroupChatEntity groupChatEntity = new GroupChatEntity("Alerte", null, date , organism);
                        mFirestore.collection(organism).document("AllCampus").collection("AllChatGroups")
                                .document(idGroupChat).set(groupChatEntity);

                        mFirestore.collection("USERS").document(idUserAlert).collection("ChatGroup")
                                .document(idGroupChat).set(groupChatEntity);

                        mFirestore.collection("USERS").document(userId).collection("ChatGroup")
                                .document(idGroupChat).set(groupChatEntity);

                        Intent intent = new Intent(getActivity(), AlertChatActivity.class);
                        intent.putExtra("alert", "alert");
                        intent.putExtra("organismAlert", organism);
                        intent.putExtra("userIdAlert", idUserAlert);
                        intent.putExtra("idGroupChat", idGroupChat);
                        startActivity(intent);

                    }
                });
                    }
                });
            }
        });
    }

    private void getInformations() {
        String idUserAlert = getArguments().getString("idUserAlert");
        String organism = getArguments().getString("organism");
        String eventId = getArguments().getString("eventId");
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            rvProgressBar.setVisibility(View.VISIBLE);


            mFirestore.collection("USERS").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String organismName = documentSnapshot.getString("organism");
                    mFirestore.collection(organism)
                            .document("AllCampus").collection("AllEvents")
                            .document(eventId).collection("StaffMembers").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                mFirestore.collection(organism)
                                        .document("AllCampus").collection("AllEvents")
                                        .document(eventId).collection("Alert").document(idUserAlert).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String username = documentSnapshot.getString("username");
                                        String imageProfile = documentSnapshot.getString("imageProfile");
                                        Date dateAlert = documentSnapshot.getDate("dateAlert");
                                        Calendar calendar = Calendar.getInstance();
                                        Date dateNow = calendar.getTime();


                                        long diff = dateNow.getTime() - dateAlert.getTime();

                                        long diffMinutes = diff / (60 * 1000) % 60;
                                        dateTimeOfAlert.setText(getString(R.string.at) + " " + diffMinutes + " " + getString(R.string.minutes));


                                        if (imageProfile != null) {
                                            StorageReference storageRef = FirebaseStorage.getInstance()
                                                    .getReferenceFromUrl(imageProfile);
                                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Uri downloadUrl = uri;
                                                    String urlImage = downloadUrl.toString();
                                                    Glide.with(getActivity())
                                                            .load(urlImage)
                                                            .into(imageProfileUser);
                                                }
                                            });
                                        }
                                        usernameAlert.setText(username);

                                        mFirestore.collection(organism)
                                                .document("AllCampus").collection("AllEvents")
                                                .document(eventId).collection("Alert").document(idUserAlert)
                                                .collection("StaffOnGoing").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int count = 0;
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        count++;
                                                    }
                                                    if (count == 0) {
                                                        nbrPeople.setText(R.string.nobody);
                                                    } else {
                                                        String countString = String.valueOf(count);
                                                        nbrPeople.setText(countString + " " + getString(R.string.people));
                                                    }
                                                    rvProgressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
