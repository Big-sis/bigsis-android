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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
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
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.SplashTripCreatedActivity;
import fr.bigsis.android.activity.TripListActivity;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.TripEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.AddTripHelper;
import fr.bigsis.android.viewModel.ChooseParticipantViewModel;

public class AddTripFragment extends Fragment {

    ChooseParticipantFragment fragmentParticipantAdd = ChooseParticipantFragment.newInstance();
    private OnFragmentInteractionListener mListener;
    private EditText etAddFromDestination, etAddToDestination;
    private Button btCreate;
    private TextView tvDateTrip, tvGroupCampusTrip, tvTitleToolbar;
    private Date date;
    private Calendar dateCal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;
    private ChooseParticipantViewModel viewModel;
    private ConstraintLayout transitionContainer;
    private ImageButton imbtSearch, imBtAdd, imgBtBack, imBtCancel, imgBtDelete;

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
        viewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
        etAddFromDestination = view.findViewById(R.id.etAddFromDestination);
        etAddToDestination = view.findViewById(R.id.etAddToDestination);
        tvGroupCampusTrip = view.findViewById(R.id.tvGroupCampusTrip);
        tvDateTrip = view.findViewById(R.id.tvDateTripAdd);
        btCreate = view.findViewById(R.id.btCreateTrip);
        imgBtDelete = view.findViewById(R.id.imgBtDelete);

        transitionContainer = getActivity().findViewById(R.id.toolbarLayout);
        imbtSearch = transitionContainer.findViewById(R.id.imBt_search_frag);
        imBtAdd = transitionContainer.findViewById(R.id.imBt_add_frag);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);

        userId = mAuth.getCurrentUser().getUid();

        String id = getArguments().getString("ID_TRIP");
        String updateTo = getArguments().getString("TO");
        String updateCampus = getArguments().getString("CAMPUS");
        String updateFrom = getArguments().getString("FROM");
        String updateDate = getArguments().getString("DATE");
        String organism_trip = getArguments().getString("ORGANISM_TRIP");

        imBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addFrag = manager.findFragmentByTag("ADD_MENU_FRAGMENT");
                ft.remove(addFrag).commitAllowingStateLoss();
                imbtSearch.setVisibility(View.VISIBLE);
                imBtAdd.setVisibility(View.VISIBLE);
                imBtCancel.setVisibility(View.GONE);
                tvTitleToolbar.setText(R.string.trips);
            }
        });
        tvDateTrip.setText(getDateTimeFromTimeStamp(System.currentTimeMillis(), "E dd MMM, HH:mm"));
        tvDateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker();
            }
        });

        tvGroupCampusTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentAddParticipantEvent();
            }
        });
        viewModel.getParticipant().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!viewModel.getParticipant().getValue().equals("")) {
                    tvGroupCampusTrip.setText(viewModel.getParticipant().getValue());
                }
            }
        });

        if (updateFrom != null) {
            imBtAdd.setVisibility(View.GONE);
            imBtCancel.setVisibility(View.VISIBLE);
            imbtSearch.setVisibility(View.GONE);
            etAddFromDestination.setText(updateFrom);
            etAddToDestination.setText(updateTo);
            tvDateTrip.setText(updateDate);
            tvGroupCampusTrip.setText(updateCampus);
            btCreate.setText(R.string.modify);
            tvTitleToolbar.setText(R.string.edit_my_trip);
            imgBtDelete.setVisibility(View.VISIBLE);
        }

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateFrom == null) {
                    createTrip();
                } else {
                    updateTrip(organism_trip, updateCampus, updateCampus, id);
                }
            }
        });

        imgBtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFordelete(organism_trip, id, userId);
            }
        });
        return view;
    }
private void showDialogFordelete(String organism_trip, String id, String userId) {

    AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
    LayoutInflater inflater = LayoutInflater.from(getContext());
    View dialogView = inflater.inflate(R.layout.style_alert_dialog, null);
    TextView tvTitle = dialogView.findViewById(R.id.tvTitleDialog);
    tvTitle.setText(R.string.delete_trip);
    Button btNo = dialogView.findViewById(R.id.btNo);
    Button btYes = dialogView.findViewById(R.id.btDeleteFriend);
    btYes.setText(R.string.delete);
    btYes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AddTripHelper.deleteFromDB(organism_trip, id, userId);
            dialogBuilder.dismiss();
           startActivity(new Intent(getActivity(), TripListActivity.class));
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
                        String groupNameUser = documentSnapshot.getString("groupCampus");
                        String organism = documentSnapshot.getString("organism");
                        String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                        String firstname = documentSnapshot.getString("firstname");
                        String lastname = documentSnapshot.getString("lastname");
                        String description = documentSnapshot.getString("description");
                        Boolean isAdmin = documentSnapshot.getBoolean("admin");
                        String addFrom = etAddFromDestination.getText().toString();
                        String toFrom = etAddToDestination.getText().toString();
                        viewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
                        String groupCampusName = viewModel.getParticipant().getValue();
                        UserEntity userEntity = new UserEntity(username, description,
                                imageProfileUrl, firstname, lastname, true, isAdmin, groupNameUser, organism);
                        if (addFrom.trim().isEmpty() || toFrom.trim().isEmpty()) {
                            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (date == null) {
                            Toast.makeText(getActivity(), "Veuillez indiquer la date et l'heure du trajet", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (viewModel.getParticipant().getValue().equals("")) {
                            Toast.makeText(getActivity(), "Veuillez sélectionner les groupes", Toast.LENGTH_LONG).show();
                            return;
                        }
                        TripEntity tripEntity = new TripEntity(addFrom, toFrom, date, username, groupCampusName, organism);
                        GroupChatEntity groupChatEntity = new GroupChatEntity(addFrom, Constant.URL_DEFAULT_TRIP, date);
                        setData(organism, tripEntity, groupChatEntity, userEntity, groupCampusName);
                    }
                });
    }

    private void updateTrip(String organism_trip, String groupCampusName, String sharedIn, String id_Trip) {

        String organism = getArguments().getString("ORGANISM_TRIP");
        String sharedInNotUpdated = getArguments().getString("CAMPUS");
        String created_by = getArguments().getString("CREATED_BY");
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(organism_trip).document("AllCampus").collection("AllTrips").document(id_Trip)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Date dateNotUpdated = documentSnapshot.getDate("date");
                String addFrom = etAddFromDestination.getText().toString();
                String toFrom = etAddToDestination.getText().toString();

                Map<String, Object> hashMapTrip = new HashMap<>();
                if (date != null) {
                    hashMapTrip.put("date", date);
                } else {
                    hashMapTrip.put("date", dateNotUpdated);
                }
                hashMapTrip.put("from", addFrom);
                hashMapTrip.put("to", toFrom);
                hashMapTrip.put("organism", organism);
                hashMapTrip.put("createdBy", created_by);

                if (!viewModel.getParticipant().getValue().equals("")) {
                    hashMapTrip.put("sharedIn", viewModel.getParticipant().getValue());
                } else {
                    hashMapTrip.put("sharedIn", sharedInNotUpdated);
                }

                Map<String, Object> hashMapGroup = new HashMap<>();
                hashMapGroup.put("title", addFrom);
                if (date != null) {
                    hashMapGroup.put("date", date);
                }else {
                    hashMapGroup.put("date", dateNotUpdated);
                }
                userId = mAuth.getCurrentUser().getUid();
                mFirestore = FirebaseFirestore.getInstance();
                mFirestore.collection("USERS")
                        .document(userId).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String organism = documentSnapshot.getString("organism");
                                String username = documentSnapshot.getString("username");
                                String groupNameUser = documentSnapshot.getString("groupCampus");
                                String imageProfileUrl = documentSnapshot.getString("imageProfileUrl");
                                String firstname = documentSnapshot.getString("firstname");
                                String lastname = documentSnapshot.getString("lastname");
                                String description = documentSnapshot.getString("description");
                                Boolean isAdmin = documentSnapshot.getBoolean("admin");
                                UserEntity userEntity = new UserEntity(username, description,
                                        imageProfileUrl, firstname, lastname, true, isAdmin, groupNameUser, organism);
                                String addFrom = etAddFromDestination.getText().toString();
                                String toFrom = etAddToDestination.getText().toString();
                                viewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
                                String groupCampusNameVM = viewModel.getParticipant().getValue();
                                if (addFrom.trim().isEmpty() || toFrom.trim().isEmpty()) {
                                    Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (!groupCampusNameVM.equals("") && !groupCampusNameVM.equals(sharedIn)) {
                                    AddTripHelper.deleteFromDB(organism, id_Trip, userId);
                                    setData(organism, hashMapTrip, hashMapGroup, userEntity, groupCampusNameVM);
                                } else {
                                    updateData(organism, groupCampusName, hashMapTrip, hashMapGroup, id_Trip);
                                }
                            }
                        });
            }
        });
    }

    private void setData(String organism, Object object, Object objectGroup, Object objectUser, String groupCampusName) {
        CollectionReference tripReference = mFirestore
                .collection(organism).document("AllCampus").collection("AllTrips");
        CollectionReference userListsRef = mFirestore.collection("USERS").document(userId)
                .collection("TripList");
        DocumentReference userDocumentRef = mFirestore.collection("USERS").document(userId);
        tripReference.add(object).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Intent intent = new Intent(getActivity(), SplashTripCreatedActivity.class);
                intent.putExtra("Trip", "event");
                startActivity(intent);
                String tripId = documentReference.getId();
                mFirestore.collection(organism).document("AllCampus")
                        .collection("AllChatGroups").document(tripId).set(objectGroup);
                viewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
                String groupCampusNameVM = viewModel.getParticipant().getValue();
                tripReference.document(tripId).collection("Creator").document(userId).set(objectUser, SetOptions.merge());
                tripReference.document(tripId).collection("Participants").document(userId).set(objectUser, SetOptions.merge());
                CollectionReference groupChatRef = mFirestore.collection(organism).document("AllCampus")
                        .collection("AllChatGroups");
                groupChatRef.document(tripId).collection("Participants")
                        .document(userId)
                        .set(objectUser);

                groupChatRef.document(tripId).collection("Creator")
                        .document(userId)
                        .set(objectUser);

                userDocumentRef.collection("ChatGroup")
                        .document(tripId)
                        .set(objectGroup);

                userDocumentRef.collection("TripList")
                        .document(tripId)
                        .set(object);
                if (groupCampusNameVM.equals("Tous les campus")) {
                    mFirestore.collection(organism).document("AllCampus").collection("AllCampus").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String groupName = document.getData().get("groupName").toString();
                                            AddTripHelper.setDataInCampus(organism, groupName, object, objectGroup, objectUser, tripId, userId);
                                        }
                                    }
                                }
                            });
                } else {
                    AddTripHelper.setDataInCampus(organism, groupCampusName, object, objectGroup, objectUser, tripId, userId);
                }
            }
        });
    }

    private void updateData(String organism, String groupCampusName, Map<String, Object> hashMapTrip, Map<String, Object> hashMapGroup, String tripId) {
        DocumentReference tripReference = mFirestore
                .collection(organism).document("AllCampus").collection("AllTrips")
                .document(tripId);
        CollectionReference userListsRef = mFirestore.collection("USERS").document(userId)
                .collection("TripList");
        DocumentReference userDocumentRef = mFirestore.collection("USERS").document(userId);
        tripReference.update(hashMapTrip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirestore.collection(organism).document("AllCampus")
                        .collection("AllChatGroups").document(tripId).update(hashMapTrip);
                viewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
                String groupCampusNameVM = viewModel.getParticipant().getValue();
                CollectionReference groupChatRef = mFirestore.collection(organism).document("AllCampus")
                        .collection("AllChatGroups");
                userDocumentRef.collection("ChatGroup")
                        .document(tripId)
                        .update(hashMapGroup);
                userDocumentRef.collection("TripList")
                        .document(tripId)
                        .update(hashMapTrip);
                if (groupCampusNameVM.equals("Tous les campus")) {
                    mFirestore.collection(organism).document("AllCampus").collection("AllCampus").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String groupName = document.getData().get("groupName").toString();
                                            AddTripHelper.updateDataInCampus(organism, groupName, tripId, hashMapTrip, hashMapGroup, userId);
                                        }
                                    }
                                }
                            });
                } else {
                    AddTripHelper.updateDataInCampus(organism, groupCampusName, tripId, hashMapTrip, hashMapGroup, userId);
                }
            }
        });
    }

    private void openFragmentAddParticipantEvent() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container, fragmentParticipantAdd, "CHOOSE")
                .commit();
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
