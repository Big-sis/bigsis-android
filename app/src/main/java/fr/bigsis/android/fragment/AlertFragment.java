package fr.bigsis.android.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.AlertSplashScreen;
import fr.bigsis.android.activity.SplashScreenActivity;
import fr.bigsis.android.entity.AlertEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.viewModel.AlertLocateViewModel;

public class AlertFragment extends Fragment {


    private final int REQUEST_PHONE_CALL = 1;
    Date dateToday;
    Date dateAlert;
    private OnFragmentInteractionListener mListener;
    private Button btCallPolice;
    private Button btCallFireFighter;
    private Button btCallEmergency;
    private Button btAlertStaff, btDesableAlert;
    private ImageButton imgBtCancel;
    private String callPolice = "tel:17";
    private String callFireFighter = "tel:18";
    private String callEmergency = "tel:112";
    private AlertLocateViewModel alertLocateViewModel;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private TextView tvAlertOnGoing;
    private View viewAlert;
    private RelativeLayout rvProgressBarALert;

    public AlertFragment() {
    }

    public static AlertFragment newInstance() {
        AlertFragment fragment = new AlertFragment();
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

        View view = inflater.inflate(R.layout.fragment_alert, container, false);
        btAlertStaff = view.findViewById(R.id.btAlert);
        btDesableAlert = view.findViewById(R.id.btDesableAlert);
        btCallFireFighter = view.findViewById(R.id.btCallFireFighter);
        btCallEmergency = view.findViewById(R.id.btCallEmergency);
        btCallPolice = view.findViewById(R.id.btCallPolice);
        imgBtCancel = view.findViewById(R.id.imgBtCancelAlert);
        tvAlertOnGoing = view.findViewById(R.id.tvAlertOnGoing);
        rvProgressBarALert = view.findViewById(R.id.rvProgressBarALert);
        viewAlert = view.findViewById(R.id.view3);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        imgBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });
        btCallPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(callPolice);
            }
        });
        btCallFireFighter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(callFireFighter);
            }
        });

        btCallEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(callEmergency);
            }
        });

        userParticipateToEvent();
        checkIfAlertOnGoing();
        return view;
    }

    private void userParticipateToEvent () {
        DocumentReference documentReference = mFirestore.collection("USERS").document(mCurrentUserId);
                documentReference.collection("ParticipateToEvents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    rvProgressBarALert.setVisibility(View.VISIBLE);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String eventId = document.getId();
                        String organism = document.getString("organism");
                        documentReference.collection("ParticipateToEvents").document(eventId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                boolean alertAvailable = documentSnapshot.getBoolean("alertAvailable");

                                String idAlertEvent = documentSnapshot.getId();
                                Date dateStart = documentSnapshot.getDate("dateStart");
                                Date dateEnd = documentSnapshot.getDate("dateEnd");

                                Calendar calendarEndDate = Calendar.getInstance();
                                calendarEndDate.setTime(dateEnd);
                                calendarEndDate.add(Calendar.HOUR_OF_DAY, 3);
                                Date addedHoursToDateEnd = calendarEndDate.getTime();

                                Calendar calendarStartDate = Calendar.getInstance();
                                calendarStartDate.setTime(dateStart);
                                calendarStartDate.add(Calendar.HOUR, -1);
                                Date addedHoursToDateStart = calendarStartDate.getTime();

                                Calendar calendar = Calendar.getInstance();
                                Date today = calendar.getTime();

                                if(alertAvailable  && today.after(addedHoursToDateStart) && today.before(addedHoursToDateEnd) ) {
                                    btAlertStaff.setVisibility(View.VISIBLE);

                                }
                                mFirestore.collection(organism).document("AllCampus")
                                        .collection("AllEvents").document(eventId).collection("StaffMembers").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            btAlertStaff.setVisibility(View.GONE);
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        rvProgressBarALert.setVisibility(View.GONE);
                                    }
                                });
                                btAlertStaff.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
                                        LayoutInflater inflater = LayoutInflater.from(getContext());
                                        View dialogView = inflater.inflate(R.layout.style_dialog_call_alert, null);
                                        Button btNo = dialogView.findViewById(R.id.btCancelAlert);
                                        Button btAlert = dialogView.findViewById(R.id.btActivateAlert);
                                        btAlert.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                mFirestore.collection("USERS").document(mCurrentUserId).get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                alertLocateViewModel = ViewModelProviders.of(getActivity()).get(AlertLocateViewModel.class);
                                                                double latitudeAlert = alertLocateViewModel.getLatitudeAlert().getValue();
                                                                double longitude = alertLocateViewModel.getLongitudeAlert().getValue();
                                                                String organism = documentSnapshot.getString("organism");
                                                                String lastname = documentSnapshot.getString("lastname");
                                                                String username = documentSnapshot.getString("username");
                                                                String imageProfile = documentSnapshot.getString("imageProfileUrl");
                                                                Calendar calendar = Calendar.getInstance();
                                                                Calendar calendar1 = Calendar.getInstance();
                                                                calendar1.add(Calendar.MINUTE, 30);
                                                                Date dateAlert = calendar.getTime();
                                                                Date dateEndAlert = calendar1.getTime();

                                                                AlertEntity alertEntity = new AlertEntity(latitudeAlert, longitude, imageProfile, lastname, username, dateAlert, dateEndAlert);
                                                                mFirestore.collection(organism).document("AllCampus")
                                                                        .collection("AllEvents").document(idAlertEvent)
                                                                        .collection("Alert").document(mCurrentUserId).set(alertEntity);
                                                                Intent intent = new Intent(getActivity(), AlertSplashScreen.class);
                                                                intent.putExtra("ALERT_DESABLE", "");
                                                                startActivity(intent);
                                                            }
                                                        });
                                                dialogBuilder.dismiss();
                                            }
                                        });
                                        btNo.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogBuilder.dismiss();
                                            }
                                        });
                                        dialogBuilder.setView(dialogView);
                                        dialogBuilder.show();                                    }
                                });

                            }
                        });

                    }
                    }
            }
        });

    }
    private void showAlertDialogForStaff() {

    }

    private void checkIfAlertOnGoing() {
        rvProgressBarALert.setVisibility(View.VISIBLE);

        mFirestore.collection("USERS").document(mCurrentUserId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String organism = documentSnapshot.getString("organism");
                        rvProgressBarALert.setVisibility(View.GONE);

                        mFirestore.collection(organism).document("AllCampus").collection("AllEvents")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        rvProgressBarALert.setVisibility(View.VISIBLE);

                                        String id = document.getId();
                                        mFirestore.collection(organism).document("AllCampus")
                                                .collection("AllEvents").document(id).collection("Alert")
                                               .document(mCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    rvProgressBarALert.setVisibility(View.GONE);
                                                    DocumentSnapshot document = task.getResult();
                                                    if(document.exists()) {
                                                        btDesableAlert.setVisibility(View.VISIBLE);
                                                        tvAlertOnGoing.setVisibility(View.VISIBLE);
                                                        viewAlert.setVisibility(View.VISIBLE);
                                                        btAlertStaff.setVisibility(View.GONE);

                                                        btDesableAlert.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                mFirestore.collection(organism).document("AllCampus")
                                                                        .collection("AllEvents").document(id).collection("Alert")
                                                                        .document(mCurrentUserId).collection("StaffOnGoing").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                String idDoc = document.getId();
                                                                                mFirestore.collection(organism).document("AllCampus")
                                                                                        .collection("AllEvents").document(id).collection("Alert")
                                                                                        .document(mCurrentUserId).collection("StaffOnGoing").document(idDoc).delete();
                                                                            }
                                                                            }
                                                                    }
                                                                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                mFirestore.collection(organism).document("AllCampus")
                                                                        .collection("AllEvents").document(id).collection("Alert")
                                                                        .document(mCurrentUserId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Intent intent = new Intent(getActivity(), AlertSplashScreen.class);
                                                                        intent.putExtra("ALERT_DESABLE", "alert désactivée");
                                                                        startActivity(intent);
                                                                        btDesableAlert.setVisibility(View.GONE);
                                                                        btAlertStaff.setVisibility(View.VISIBLE);
                                                                        FragmentManager manager = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                                                                        FragmentTransaction ft = manager.beginTransaction();
                                                                        Fragment alert_fragment = manager.findFragmentByTag("MENU_ALERT_FRAGMENT");
                                                                        if (alert_fragment != null) {
                                                                            ft.remove(alert_fragment).commitAllowingStateLoss();
                                                                        }
                                                                        String idAlerte = "Alert" + id;
                                                                        mFirestore.collection("USERS").document(mCurrentUserId).collection("ChatGroup")
                                                                                .document(idAlerte).delete();

                                                                        mFirestore.collection("USERS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                        String idUsers = document.getId();
                                                                                        mFirestore.collection("USERS").document(idUsers).collection("ChatGroup")
                                                                                                .document(idAlerte).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                            @Override
                                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                if(documentSnapshot.exists()){
                                                                                                    mFirestore.collection("USERS").document(idUsers).collection("ChatGroup")
                                                                                                            .document(idAlerte).delete();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                    }
                                                                            }
                                                                        });
                                                                        mFirestore.collection(organism).document("AllCampus").collection("AllChatGroups")
                                                                                .document(idAlerte).collection("Chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                        String idDoc = document.getId();
                                                                                        mFirestore.collection(organism).document("AllCampus").collection("AllChatGroups")
                                                                                                .document(idAlerte).collection("Chats").document(idDoc).delete();
                                                                                    }
                                                                                    mFirestore.collection(organism).document("AllCampus").collection("AllChatGroups")
                                                                                            .document(idAlerte).delete();
                                                                                    }
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    }
                            }
                        });

                    }
                });


    }



    private void showAlertDialog(String number) {
        AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.style_dialog_call_alert, null);
        Button btNo = dialogView.findViewById(R.id.btCancelAlert);
        Button btAlert = dialogView.findViewById(R.id.btActivateAlert);
        btAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(number));
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    startActivity(callIntent);
                }
                dialogBuilder.dismiss();
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
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
