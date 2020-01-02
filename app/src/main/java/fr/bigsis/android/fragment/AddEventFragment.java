package fr.bigsis.android.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.EventListActivity;
import fr.bigsis.android.activity.SplashTripCreatedActivity;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.helpers.AddTripHelper;
import fr.bigsis.android.helpers.FirestoreDBHelper;
import fr.bigsis.android.helpers.FirestoreHelper;
import fr.bigsis.android.helpers.UpdateHelper;
import fr.bigsis.android.viewModel.ChooseParticipantViewModel;
import fr.bigsis.android.viewModel.ChooseUsersViewModel;

import static android.content.Context.MODE_PRIVATE;
import static fr.bigsis.android.constant.Constant.REQUEST_CODE_ADRESS_AUTOCOMPLETE;
import static fr.bigsis.android.constant.Constant.REQUEST_CODE_FROM_AUTOCOMPLETE;
import static fr.bigsis.android.constant.Constant.REQUEST_CODE_TO_AUTOCOMPLETE;
import static fr.bigsis.android.helpers.FirestoreHelper.setEvent;
import static fr.bigsis.android.helpers.FirestoreHelper.updateData;

public class AddEventFragment extends Fragment {


    ChooseParticipantFragment fragmentParticipantAdd = ChooseParticipantFragment.newInstance();
    ChooseStaffFragment fragmentStaffAdd = ChooseStaffFragment.newInstance();
    private OnFragmentInteractionListener mListener;
    private EditText etTitleEventFragment;
    private EditText descriptionEventFragment;
    private TextView tvDateEventFragmentStart, etAddressEventFragment;
    private TextView tvDateEventFragmentEnd;
    private TextView tvParticipantFragment;
    private TextView tvMembersFragment;
    private Button btCreateEvent;
    private Date dateStart;
    private Date dateEnd;
    private Calendar dateCal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;
    private ChooseUsersViewModel viewModel;
    private ChooseParticipantViewModel chooseParticipantViewModel;
    private TextView tvTitleToolbar;
    private ConstraintLayout transitionContainer;
    private ImageButton imbtSearch, imBtAdd, imgBtBack, imBtCancel, imgBtDeleteEvent;

    private String createdOrUpdated;
    double latDestination;
    double lngDestination;

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
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        chooseParticipantViewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
        viewModel = ViewModelProviders.of(getActivity()).get(ChooseUsersViewModel.class);

        etTitleEventFragment = view.findViewById(R.id.etTitleEventFragment);
        etAddressEventFragment = view.findViewById(R.id.etAddressEventFragment);
        descriptionEventFragment = view.findViewById(R.id.descriptionEventFragment);
        tvDateEventFragmentStart = view.findViewById(R.id.tvDateEventFragmentStart);
        tvDateEventFragmentEnd = view.findViewById(R.id.tvDateEventFragmentEnd);
        tvParticipantFragment = view.findViewById(R.id.tvParticipantFragment);
        tvMembersFragment = view.findViewById(R.id.tvMembersFragment);
        btCreateEvent = view.findViewById(R.id.btCreateEvent);
        imgBtDeleteEvent = view.findViewById(R.id.imgBtDeleteEvent);

        transitionContainer = getActivity().findViewById(R.id.toolbarLayout);
        imbtSearch = transitionContainer.findViewById(R.id.imBt_search_frag);
        imBtAdd = transitionContainer.findViewById(R.id.imBt_add_frag);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);

        String id = getArguments().getString("ID_EVENT");
        String title = getArguments().getString("TITLE");
        String adress = getArguments().getString("ADRESS");
        String campus = getArguments().getString("CAMPUS_EVENT");
        String updateDateStart = getArguments().getString("DATE_START");
        String updateDateEnd = getArguments().getString("DATE_END");
        String organism = getArguments().getString("ORGANISM_EVENT");
        String description = getArguments().getString("DESCRIPTION");
        if (title != null) {
            imBtAdd.setVisibility(View.GONE);
            imBtCancel.setVisibility(View.VISIBLE);
            imbtSearch.setVisibility(View.GONE);
            etTitleEventFragment.setText(title);
            etAddressEventFragment.setText(adress);
            tvDateEventFragmentStart.setText(updateDateStart);
            tvDateEventFragmentEnd.setText(updateDateEnd);
            tvParticipantFragment.setText(campus);
            descriptionEventFragment.setText(description);
            btCreateEvent.setText(R.string.modify);
            tvTitleToolbar.setText(R.string.modify_event);
            imgBtDeleteEvent.setVisibility(View.VISIBLE);
            createdOrUpdated = getString(R.string.updated);
            tvParticipantFragment.setVisibility(View.GONE);
        }
        imBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addFrag = manager.findFragmentByTag("ADD_EVENT_FRAGMENT");
                if(addFrag != null) {
                    ft.remove(addFrag).commitAllowingStateLoss();
                }
                imBtAdd.setVisibility(View.VISIBLE);
                imBtCancel.setVisibility(View.GONE);
                tvTitleToolbar.setText(R.string.events);
                getActivity().onBackPressed();
            }
        });

        etAddressEventFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestPlaces(REQUEST_CODE_FROM_AUTOCOMPLETE);
            }
        });

        tvParticipantFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentAddParticipantEvent();
            }
        });
        chooseParticipantViewModel.getParticipant().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!chooseParticipantViewModel.getParticipant().getValue().equals("")) {
                    tvParticipantFragment.setText(chooseParticipantViewModel.getParticipant().getValue());
                }
            }
        });

        tvMembersFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("ID", id);
                bundle.putString("campus", campus);
                bundle.putString("organism", organism);
                fragmentStaffAdd.setArguments(bundle);
                openFragmentAddStaffEvent();
            }
        });

        tvDateEventFragmentStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePickerStart();
            }
        });
        tvDateEventFragmentEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePickerEnd();
            }
        });
        btCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title == null) {
                    createdOrUpdated = getString(R.string.added);
                    createEvent();
                } else {
                    updateEvent(organism, campus, campus, id);
                }
            }
        });

        imgBtDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFordelete(organism, id, userId);
            }
        });


        return view;
    }
    public void suggestPlaces(int code){
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Constant.MAPBOX_ACCESS_TOKEN)
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .toolbarColor(getResources().getColor(R.color.colorAccent))
                        .limit(10)
                        .country(Locale.FRANCE)
                        .geocodingTypes()
                        .build(PlaceOptions.MODE_CARDS))
                .build(getActivity());
        startActivityForResult(intent, code);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ADRESS_AUTOCOMPLETE) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            etAddressEventFragment.setText(feature.text());
            latDestination = ((Point) feature.geometry()).latitude();
            lngDestination = ((Point) feature.geometry()).longitude();
        }
    }
    private void showDateTimePickerStart() {
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
                        dateStart = dateCal.getTime();
                        tvDateEventFragmentStart.setText(format.format(dateStart));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void showDateTimePickerEnd() {
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
                        dateEnd = dateCal.getTime();
                        tvDateEventFragmentEnd.setText(format.format(dateEnd));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void createEvent() {
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
                        String title = etTitleEventFragment.getText().toString();
                        String address = etAddressEventFragment.getText().toString();
                        String descriptionEvent = descriptionEventFragment.getText().toString();
                        chooseParticipantViewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
                        viewModel = ViewModelProviders.of(getActivity()).get(ChooseUsersViewModel.class);
                        String groupCampusName = chooseParticipantViewModel.getParticipant().getValue();
                        UserEntity userEntity = new UserEntity(username, description,
                                imageProfileUrl, firstname, lastname, true, isAdmin, groupNameUser, organism);
                        if (viewModel.getStaffList().getValue() == null){
                            Toast.makeText(getActivity(), "Veuillez sélectionner les staff", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (title.trim().isEmpty() || address.trim().isEmpty()) {
                            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (chooseParticipantViewModel.getParticipant().getValue().equals("")) {
                            Toast.makeText(getActivity(), "Veuillez sélectionner les groupes", Toast.LENGTH_LONG).show();
                            return;
                        }

                        EventEntity eventEntity = new EventEntity(dateStart, dateEnd, title, address, null, descriptionEvent, username,
                                groupCampusName,  organism, latDestination, lngDestination);

                        GroupChatEntity groupChatEntity = new GroupChatEntity(title, null, dateStart, null, organism, groupCampusName);
                        setData(organism, eventEntity, groupChatEntity, userEntity, groupCampusName);

                    }
                });
    }
    private void setData(String organism, Object object, Object objectGroup, UserEntity objectUser, String groupCampusName) {
        CollectionReference eventRef = mFirestore
                .collection(organism).document("AllCampus").collection("AllEvents");

        DocumentReference userDocumentRef = mFirestore.collection("USERS").document(userId);
        eventRef.add(object).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String eventId = documentReference.getId();
                setEvent(eventRef, eventId,"Participants", userId, objectUser);
                        eventRef.document(eventId).collection("Creator").document(userId).set(objectUser);
                setEvent(eventRef, eventId,"StaffMembers", userId, objectUser);
                mFirestore.collection(organism).document("AllCampus")
                        .collection("AllChatGroups").document(eventId).set(objectGroup);
                chooseParticipantViewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
                String groupCampusNameVM = chooseParticipantViewModel.getParticipant().getValue();
                 CollectionReference groupChatRef = mFirestore.collection(organism).document("AllCampus")
                        .collection("AllChatGroups");
                setEvent(groupChatRef, eventId,"Participants", userId, objectUser);
                setEvent(groupChatRef, eventId,"StaffMembers", userId, objectUser);
                setEvent(groupChatRef, eventId,"Creator", userId, objectUser);
                userDocumentRef.collection("ChatGroup")
                        .document(eventId)
                        .set(objectGroup);

                FirestoreDBHelper.setData("USERS", userId, "ChatGroup", eventId, objectGroup);
                viewModel = ViewModelProviders.of(getActivity()).get(ChooseUsersViewModel.class);
                viewModel.getStaffList().observe(getActivity(), new Observer<List<UserEntity>>() {
                    @Override
                    public void onChanged(List<UserEntity> userEntities) {
                        for (UserEntity user : userEntities) {
                            eventRef.document(eventId).collection("StaffMembers").document(user.getUserId()).set(user, SetOptions.merge());
                            eventRef.document(eventId).collection("Participants").document(user.getUserId()).set(user, SetOptions.merge());
                            DocumentReference userListsRef = mFirestore.collection("USERS").document(user.getUserId());
                            userListsRef.collection("ChatGroup").document(eventId).set(objectGroup);
                            setEvent(groupChatRef, eventId,"Participants", user.getUserId(), user);
                            setEvent(groupChatRef, eventId,"StaffMembers", user.getUserId(), user);
                        }
                    }
                });
                String title = getArguments().getString("TITLE");

                viewModel.resetStaffMember();
                if (groupCampusNameVM.equals("Tous les campus")) {
                    mFirestore.collection(organism).document("AllCampus").collection("AllCampus").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String groupName = document.getData().get("groupName").toString();
                                            AddTripHelper.setDataEventInCampus(organism, groupName, object, objectGroup, objectUser, eventId, userId);
                                        }
                                    }
                                }
                            });
                } else {
                    AddTripHelper.setDataEventInCampus(organism, groupCampusName, object, objectGroup, objectUser, eventId, userId);
                }

                Intent intent = new Intent(getActivity(), SplashTripCreatedActivity.class);
                intent.putExtra("Event", createdOrUpdated);
                startActivity(intent);
            }
        });
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
    private void updateEvent(String organism_event, String groupCampusName, String sharedIn, String id_event) {

        String organism = getArguments().getString("ORGANISM_EVENT");
        String sharedInNotUpdated = getArguments().getString("CAMPUS_EVENT");
        String created_by = getArguments().getString("CREATED_BY");
        String description = getArguments().getString("CREATED_BY");
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(organism_event).document("AllCampus").collection("AllEvents").document(id_event)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Date dateStartNotUpdated = documentSnapshot.getDate("dateStart");
                Date dateEndNotUpdated = documentSnapshot.getDate("dateEnd");
                String titleEvent = etTitleEventFragment.getText().toString();
                String adressEvent = etAddressEventFragment.getText().toString();
                String descriptionEvent = descriptionEventFragment.getText().toString();

                Map<String, Object> hashMapEvent = new HashMap<>();
                if (dateStart != null) {
                    hashMapEvent.put("dateStart", dateStart);
                } else {
                    hashMapEvent.put("dateStart", dateStartNotUpdated);
                }
                if (dateEnd != null) {
                    hashMapEvent.put("dateEnd", dateEnd);
                } else {
                    hashMapEvent.put("dateEnd", dateEndNotUpdated);
                }
                hashMapEvent.put("titleEvent", titleEvent);
                hashMapEvent.put("addressEvent", adressEvent);
                hashMapEvent.put("description", descriptionEvent);
                hashMapEvent.put("organism", organism);
                hashMapEvent.put("createdBy", created_by);

                if (!chooseParticipantViewModel.getParticipant().getValue().equals("")) {
                    hashMapEvent.put("sharedIn", chooseParticipantViewModel.getParticipant().getValue());
                } else {
                    hashMapEvent.put("sharedIn", sharedInNotUpdated);
                }

                Map<String, Object> hashMapGroup = new HashMap<>();
                hashMapGroup.put("titleEvent", titleEvent);
                if (dateStart != null) {
                    hashMapGroup.put("dateStart", dateStart);
                } else {
                    hashMapGroup.put("dateStart", dateStartNotUpdated);
                }
                if (dateEnd != null) {
                    hashMapGroup.put("dateEnd", dateEnd);
                } else {
                    hashMapGroup.put("dateEnd", dateEndNotUpdated);
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
                                String title = etTitleEventFragment.getText().toString();
                                String adress = etAddressEventFragment.getText().toString();
                                chooseParticipantViewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
                                String groupCampusNameVM = chooseParticipantViewModel.getParticipant().getValue();
                                if (title.trim().isEmpty() || adress.trim().isEmpty()) {
                                    Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (!groupCampusNameVM.equals("") && !groupCampusNameVM.equals(sharedIn)) {
                                    setData(organism, hashMapEvent, hashMapGroup, userEntity, groupCampusNameVM);
                                    AddTripHelper.deleteEventFromDB(organism, id_event, userId);
                                } else {
                                    updateDataEvent(organism, groupCampusName, hashMapEvent, hashMapGroup, id_event);
                                }
                            }
                        });
            }
        });
    }


    private void updateDataEvent(String organism, String groupCampusName, Map<String, Object> hashMapEvent, Map<String, Object> hashMapGroup, String eventId) {
        DocumentReference eventRef = mFirestore
                .collection(organism).document("AllCampus").collection("AllEvents")
                .document(eventId);

        DocumentReference userDocumentRef = mFirestore.collection("USERS").document(userId);
        eventRef.update(hashMapEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirestore.collection(organism).document("AllCampus")
                        .collection("AllChatGroups").document(eventId).update(hashMapEvent);
                chooseParticipantViewModel = ViewModelProviders.of(getActivity()).get(ChooseParticipantViewModel.class);
                String groupCampusNameVM = chooseParticipantViewModel.getParticipant().getValue();
                CollectionReference groupChatRef = mFirestore.collection(organism).document("AllCampus")
                        .collection("AllChatGroups");
                userDocumentRef.collection("ChatGroup")
                        .document(eventId)
                        .update(hashMapGroup);

                if (groupCampusNameVM.equals("Tous les campus") || groupCampusName.equals("Tous les campus")) {
                    mFirestore.collection(organism).document("AllCampus").collection("AllCampus").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String groupName = document.getData().get("groupName").toString();
                                            AddTripHelper.updateEventInCampus(organism, groupName, eventId, hashMapEvent, hashMapGroup, userId);
                                        }
                                    }
                                }
                            });
                } else {
                    AddTripHelper.updateEventInCampus(organism, groupCampusName, eventId, hashMapEvent, hashMapGroup, userId);
                }
            }
        });
        Intent intent = new Intent(getActivity(), SplashTripCreatedActivity.class);
        intent.putExtra("Event", createdOrUpdated);
        startActivity(intent);
    }

    private void showDialogFordelete(String organism_trip, String id, String userId) {

        AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.style_alert_dialog, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitleDialog);
        tvTitle.setText(R.string.delete_event);
        Button btNo = dialogView.findViewById(R.id.btNo);
        Button btYes = dialogView.findViewById(R.id.btDeleteFriend);
        btYes.setText(R.string.delete);
        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTripHelper.deleteEventFromDB(organism_trip, id, userId);
                dialogBuilder.dismiss();
                startActivity(new Intent(getActivity(), EventListActivity.class));
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
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void openFragmentAddParticipantEvent() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_event, fragmentParticipantAdd, "CHOOSE")
                .commit();
    }
    private void openFragmentAddStaffEvent() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_event, fragmentStaffAdd, "CHOOSE_STAFF")
                .commit();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteractionAddEvent();
    }
}
