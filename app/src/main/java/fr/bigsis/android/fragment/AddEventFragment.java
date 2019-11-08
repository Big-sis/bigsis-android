package fr.bigsis.android.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.ChooseUserActivity;
import fr.bigsis.android.activity.SplashTripCreatedActivity;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.entity.UserEntity;


public class AddEventFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText etAddAdress;
    private EditText etTitleEvent;
    private EditText etDescriptionEvent;
    private Button btCreateEvent;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvParticipantEvent;
    private TextView tvStaffMember;
    private Date dateStart;
    private Date dateEnd;
    private Date date;
    private Calendar dateCal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String mCurrentUserId;
    private String eventIdTitle;

    public AddEventFragment() {
    }

    public static AddEventFragment newInstance() {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        etAddAdress = view.findViewById(R.id.etAddAdressEvent);
        etTitleEvent = view.findViewById(R.id.etTitleEvent);
        etDescriptionEvent = view.findViewById(R.id.etDescriptionEvent);
        tvStartDate = view.findViewById(R.id.tvDateStartEventAdd);
        tvEndDate = view.findViewById(R.id.tvDateEndEventAdd);
        btCreateEvent = view.findViewById(R.id.btCreateEvent);
        tvStaffMember = view.findViewById(R.id.tvStaffEvent);
        tvParticipantEvent = view.findViewById(R.id.tvParticipantEvent);
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(tvStartDate);
            }
        });
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(tvEndDate);
            }
        });
        tvStaffMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
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

    private void showDateTimePicker(TextView tv) {
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
                        tv.setText(format.format(date));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void createEvent() {
        eventIdTitle = ("ID" + etTitleEvent.getText().toString());
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
                .document(mCurrentUserId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        String firstname = documentSnapshot.getString("firstname");
                        String lastname = documentSnapshot.getString("lastname");
                        String description = documentSnapshot.getString("description");
                        String titleEvent = etTitleEvent.getText().toString();
                        String adressEvent = etAddAdress.getText().toString();
                        String descriptionEvent = etDescriptionEvent.getText().toString();
                        String dateStartS = tvStartDate.getText().toString();
                        String dateEndS = tvEndDate.getText().toString();
                        //TODO change MAP
                        SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM, HH:mm",
                                Locale.FRENCH);
                        try {
                            dateStart = sdf.parse(dateStartS);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            dateEnd = sdf.parse(dateEndS);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String KEY = "eCinHruQlvOrt7tG4MbkaVIvuiyeYzir";
                        String urlImageRoute = "https://open.mapquestapi.com/staticmap/v4/getmap?key=" + KEY + "&size=600,400&type=map&imagetype=png&declutter=false&shapeformat=cmp&shape=uajsFvh}qMlJsK??zKfQ??tk@urAbaEyiC??y]{|AaPsoDa~@wjEhUwaDaM{y@??t~@yY??DX&scenter=40.0337,-76.5047&ecenter=39.9978,-76.3545";

                        if (titleEvent.trim().isEmpty() || adressEvent.trim().isEmpty() || descriptionEvent.trim().isEmpty()) {
                            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (dateStart == null || dateEnd == null) {
                            Toast.makeText(getActivity(), "Veuillez indiquer la date et l'heure de l'évnènement", Toast.LENGTH_LONG).show();
                            return;
                        }


                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bigsis-777.appspot.com/imagesEvent/tbs.png");
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                String imageEventUrl = downloadUrl.toString();

                                CollectionReference eventReference = mFirestore
                                        .collection("events");
                                CollectionReference userListsRef = mFirestore
                                        .collection("users")
                                        .document(mCurrentUserId)
                                        .collection("eventList");

                                EventEntity eventEntity = new EventEntity(dateStart, dateEnd, titleEvent, descriptionEvent, imageEventUrl, urlImageRoute, adressEvent);
                                UserEntity userEntity = new UserEntity(username, description, imageProfileUrl, firstname, lastname);
                                eventReference.document(eventIdTitle).set(eventEntity, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(getActivity(), SplashTripCreatedActivity.class));

                                        eventReference.document(eventIdTitle)
                                                .collection("createdBy")
                                                .document(mCurrentUserId)
                                                .set(userEntity);
                                        eventReference.document(eventIdTitle)
                                                .collection("participants")
                                                .document(mCurrentUserId)
                                                .set(userEntity);
                                        userListsRef.document(eventIdTitle).set(eventEntity);
                                    }
                                });
                                        /*startActivity(new Intent(getActivity(), SplashTripCreatedActivity.class));
                                        String idEvent = documentReference.getId();
                                        eventReference.document(idEvent)
                                                .collection("createdBy")
                                                .document(mCurrentUserId)
                                                .set(userEntity);
                                        eventReference.document(idEvent)
                                                .collection("participants")
                                                .document(mCurrentUserId)
                                                .set(userEntity);
                                        userListsRef.document(idEvent).set(eventEntity);
                                    }
                                });*/
                            }
                        });
                    }
                });
    }

    private void sendData() {
        Intent i = new Intent(getContext(), ChooseUserActivity.class);
        i.putExtra("ID_EVENT", eventIdTitle);
        getActivity().startActivity(i);
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionEvent();
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
        void onFragmentInteractionEvent();
    }
}
