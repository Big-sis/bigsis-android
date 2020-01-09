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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.AlertEntity;
import fr.bigsis.android.viewModel.AlertLocateViewModel;

public class AlertFragment extends Fragment {


    private final int REQUEST_PHONE_CALL = 1;
    Date dateToday;
    Date dateAlert;
    private OnFragmentInteractionListener mListener;
    private Button btCallPolice;
    private Button btCallFireFighter;
    private Button btCallEmergency;
    private Button btAlertStaff;
    private ImageButton imgBtCancel;
    private String callPolice = "tel:17";
    private String callFireFighter = "tel:18";
    private String callEmergency = "tel:112";
    private AlertLocateViewModel alertLocateViewModel;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

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
        btCallFireFighter = view.findViewById(R.id.btCallFireFighter);
        btCallEmergency = view.findViewById(R.id.btCallEmergency);
        btCallPolice = view.findViewById(R.id.btCallPolice);
        imgBtCancel = view.findViewById(R.id.imgBtCancelAlert);
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

        btAlertStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogForStaff();
            }
        });
        verifyIfAlert();
        userParticipateToEvent();
        return view;
    }


    private void userParticipateToEvent () {
        CollectionReference collectionReference = mFirestore.collection("USERS").document(mCurrentUserId).collection("ParticipateToEvents");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String eventId = document.getId();
                        collectionReference.document(eventId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                boolean alertAvailable = documentSnapshot.getBoolean("alertAvailable");
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
                                /*if(alertAvailable && today.before(addedHoursToDateEnd) && today.after(addedHoursToDateStart)) {
                                    btAlertStaff.setVisibility(View.VISIBLE);
                                }*/
                            }
                        });

                    }
                    }
            }
        });
    }
    private void showAlertDialogForStaff() {
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
                                String groupCampus = documentSnapshot.getString("groupCampus");
                                String lastname = documentSnapshot.getString("lastname");
                                String firstname = documentSnapshot.getString("firstname");
                                String imageProfile = documentSnapshot.getString("imageProfileUrl");
                                Calendar calendar = Calendar.getInstance();
                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.add(Calendar.MINUTE, 30);
                                Date dateAlert = calendar.getTime();
                                Date dateEndAlert = calendar1.getTime();

                                AlertEntity alertEntity = new AlertEntity(latitudeAlert, longitude, imageProfile, lastname, firstname, dateAlert, dateEndAlert);
                                mFirestore.collection(organism).document("AllCampus")
                                        .collection("AllCampus").document(groupCampus)
                                        .collection("Alert").document(mCurrentUserId).set(alertEntity);
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
        dialogBuilder.show();
    }

    private void verifyIfAlert() {

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
