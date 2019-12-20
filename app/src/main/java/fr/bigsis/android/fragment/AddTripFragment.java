package fr.bigsis.android.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.SplashTripCreatedActivity;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.FirestoreDBHelper;

public class AddTripFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText etAddFromDestination;
    private EditText etAddToDestination;
    private Button btCreate;
    private TextView tvDateTrip, tvGroupCampusTrip;
    private Date date;
    private Calendar dateCal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;

    public AddTripFragment() {
    }

    public static AddTripFragment newInstance() {
        AddTripFragment fragment = new AddTripFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private static String getDateTimeFromTimeStamp(Long time, String mDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat, Locale.FRANCE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date dateTime = new Date(time);
        return dateFormat.format(dateTime);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        etAddFromDestination = view.findViewById(R.id.etAddFromDestination);
        etAddToDestination = view.findViewById(R.id.etAddToDestination);
        tvGroupCampusTrip = view.findViewById(R.id.tvGroupCampusTrip);
        tvDateTrip = view.findViewById(R.id.tvDateTripAdd);
        btCreate = view.findViewById(R.id.btCreateTrip);
        tvDateTrip.setText(getDateTimeFromTimeStamp(System.currentTimeMillis(), "E dd MMM, HH:mm"));
        tvDateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker();
            }
        });
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTrip();
            }
        });

        return view;
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        dateCal = Calendar.getInstance();
        new DatePickerDialog(getActivity(), R.style.MyDatePickerDialogStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                dateCal.set(year, month, dayOfMonth);
                new TimePickerDialog(getActivity(), R.style.MyDatePickerDialogStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateCal.set(Calendar.MINUTE, minute);
                        SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
                        date = dateCal.getTime();
                        tvDateTrip.setText(format.format(date));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void createTrip() {
        userId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("USERS")
                .document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String groupCampus = documentSnapshot.getString("groupCampus");
                        String organism = documentSnapshot.getString("organism");
                        String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        String firstname = documentSnapshot.getString("firstname");
                        String lastname = documentSnapshot.getString("lastname");
                        String description = documentSnapshot.getString("description");
                        Boolean isAdmin = documentSnapshot.getBoolean("admin");
                        String addFrom = etAddFromDestination.getText().toString();
                        String toFrom = etAddToDestination.getText().toString();
                        String token = FirebaseInstanceId.getInstance().getToken();

                        UserEntity userEntity = new UserEntity(username, description,
                                imageProfileUrl, firstname, lastname, true, isAdmin, groupCampus, organism );

                        if (addFrom.trim().isEmpty() || toFrom.trim().isEmpty()) {
                            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (date == null) {
                            Toast.makeText(getActivity(), "Veuillez indiquer la date et l'heure du trajet", Toast.LENGTH_LONG).show();
                            return;
                        }
                        CollectionReference tripReference = mFirestore
                                .collection(organism).document("AllCampus").collection("AllCampus").document(groupCampus)
                                .collection("Trips");

                        CollectionReference userListsRef = mFirestore.collection("USERS").document(userId)
                                .collection("TripList");
                        DocumentReference userDocumentRef = mFirestore.collection("USERS").document(userId);

                        TripEntity tripEntity = new TripEntity(addFrom, toFrom, date, username);
                        tripReference.add(tripEntity).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Intent intent = new Intent(getActivity(), SplashTripCreatedActivity.class);
                                intent.putExtra("Trip", "event");
                                startActivity(intent);
                                String tripId = documentReference.getId();
                                FirestoreDBHelper.setDataInOneCampus(organism, groupCampus, "Trips", tripId, tripEntity);

                                GroupChatEntity groupChatEntity = new GroupChatEntity(addFrom, Constant.URL_DEFAULT_TRIP, date);

                                FirestoreDBHelper.setDataInOneCampus(organism, groupCampus, "ChatGroup", tripId, groupChatEntity);
                                tripReference.document(tripId).collection("Creator").document(userId).set(userEntity, SetOptions.merge());
                                tripReference.document(tripId).collection("Participants").document(userId).set(userEntity, SetOptions.merge());
                                CollectionReference groupChatRef =mFirestore.collection(organism).document("AllCampus").collection("AllCampus")
                                        .document(groupCampus).collection("ChatGroup");

                                groupChatRef.document(tripId).set(tripEntity);
                                        groupChatRef.document(tripId).collection("Participants")
                                        .document(userId)
                                        .set(userEntity);
                                groupChatRef.document(tripId).set(tripEntity);

                                groupChatRef.document(tripId).collection("Creator")
                                        .document(userId)
                                        .set(userEntity);

                                userDocumentRef.collection("ChatGroup")
                                        .document(tripId)
                                        .set(groupChatEntity);

                                userDocumentRef.collection("TripList")
                                        .document(tripId)
                                        .set(tripEntity);
                            }
                        });
                    }
                });
                }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionAdd();
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
        void onFragmentInteractionAdd();
    }
}
