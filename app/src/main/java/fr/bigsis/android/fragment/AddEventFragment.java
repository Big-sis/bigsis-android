package fr.bigsis.android.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.SplashTripCreatedActivity;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.entity.UserEntity;

public class AddEventFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private EditText etTitleEventFragment;
    private EditText etAddressEventFragment;
    private EditText descriptionEventFragment;
    private TextView tvDateEventFragmentStart;
    private TextView tvDateEventFragmentEnd;
    private TextView tvParticipantFragment;
    private TextView tvMembersFragment;
    private Button btCreateEvent;
    private Date date;
    private Calendar dateCal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;

    public AddEventFragment() {
        // Required empty public constructor
    }

    public static AddEventFragment newInstance() {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        etTitleEventFragment = view.findViewById(R.id.etTitleEventFragment);
        etAddressEventFragment = view.findViewById(R.id.etAddressEventFragment);
        descriptionEventFragment = view.findViewById(R.id.descriptionEventFragment);
        tvDateEventFragmentStart = view.findViewById(R.id.tvDateEventFragmentStart);
        tvDateEventFragmentEnd = view.findViewById(R.id.tvDateEventFragmentEnd);
        tvParticipantFragment = view.findViewById(R.id.tvParticipantFragment);
        tvMembersFragment = view.findViewById(R.id.tvMembersFragment);
        btCreateEvent = view.findViewById(R.id.btCreateEvent);
        tvDateEventFragmentStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(tvDateEventFragmentStart);
            }
        });
        tvDateEventFragmentEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(tvDateEventFragmentEnd);
            }
        });
        btCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });

        return view;
    }
    private void showDateTimePicker(TextView tvDate) {
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
                        tvDate.setText(format.format(date));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }



    private void createEvent() {
        userId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
                .document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        String firstname = documentSnapshot.getString("firstname");
                        String lastname = documentSnapshot.getString("lastname");
                        String description = documentSnapshot.getString("description");
                        Boolean isAdmin = documentSnapshot.getBoolean("admin");
                        String title = etTitleEventFragment.getText().toString();
                        String address = etAddressEventFragment.getText().toString();
                        String descriptionEvent = descriptionEventFragment.getText().toString();
                        String token = FirebaseInstanceId.getInstance().getToken();

                        UserEntity userEntity = new UserEntity(username, description, imageProfileUrl,
                                firstname, lastname, true, isAdmin, false, token);

                        if (title.trim().isEmpty() || address.trim().isEmpty()) {
                            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (date == null) {
                            Toast.makeText(getActivity(), "Veuillez indiquer la date et l'heure de l'évènement", Toast.LENGTH_LONG).show();
                            return;
                        }
                        CollectionReference eventRef = FirebaseFirestore.getInstance()
                                .collection("events");
                        CollectionReference userListsRef = mFirestore.collection("users").document(userId).collection("eventList");
                        EventEntity eventEntity = new EventEntity();
                        tripReference.document(addFrom + "to" + toFrom).set(tripEntity).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getActivity(), SplashTripCreatedActivity.class));
                                String idtrip = addFrom + "to" + toFrom;
                                userListsRef.document(idtrip).set(tripEntity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFirestore.collection("trips")
                                                .document(idtrip)
                                                .collection("participants")
                                                .document(userId)
                                                .set(userEntity);
                                        String titleTrip = addFrom + " ... " + toFrom;
                                        String idGroup = idtrip+"chatGroup";
                                        GroupChatEntity groupChatEntity = new GroupChatEntity(titleTrip,  url, date);
                                        CollectionReference groupChatRef = mFirestore.collection("GroupChat");
                                        groupChatRef.document(idGroup)
                                                .set(groupChatEntity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                groupChatRef.document(idGroup).collection("participants")
                                                        .document(userId)
                                                        .set(userEntity);

                                                groupChatRef.document(idGroup).collection("trip")
                                                        .document(idtrip)
                                                        .set(tripEntity);

                                                mFirestore.collection("users")
                                                        .document(userId)
                                                        .collection("groupChat")
                                                        .document(idGroup)
                                                        .set(groupChatEntity);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
    }



    private static String getDateTimeFromTimeStamp(Long time, String mDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat, Locale.FRANCE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date dateTime = new Date(time);
        return dateFormat.format(dateTime);
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionAddEvent();
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
        void onFragmentInteractionAddEvent();
    }
}
