package fr.bigsis.android.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.bigsis.android.R;
import fr.bigsis.android.adapter.AlertChatAdapter;
import fr.bigsis.android.entity.ChatEntity;
import fr.bigsis.android.helpers.FirestoreHelper;

public class AlertChatActivity extends BigsisActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Uri imageProfileUri;
    private CircleImageView circleImageView;
    private StorageReference mStroageReference;
    private int STORAGE_PERMISSION_CODE = 2;
    private String alert, organismAlert, userIdAlert, idGroupChat, chatgroup;
    private RecyclerView chats;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private FirebaseFirestore mFirestore;
    private EditText message;
    private ImageButton send, addImage, imBt_participant;
    private LinearLayout frameLayoutContainerAlert;
    private AlertChatAdapter adapter;
    private ImageButton imBt_ic_validate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_chat);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        alert = extras.getString("alert");
        chatgroup = extras.getString("ID_GROUP");
        organismAlert = extras.getString("organismAlert");
        userIdAlert = extras.getString("userIdAlert");
        idGroupChat = extras.getString("idGroupChat");

        message = findViewById(R.id.message_textAlert);
        send = findViewById(R.id.send_messageAlert);
        chats = findViewById(R.id.chatAlert);
        addImage = findViewById(R.id.add_imageAlert);
        frameLayoutContainerAlert = findViewById(R.id.frameLayoutContainerAlert);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        });
        mStroageReference = FirebaseStorage.getInstance().getReference("images");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chats.setLayoutManager(manager);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().isEmpty()) {
                    Toast.makeText(AlertChatActivity.this, "erreur", Toast.LENGTH_SHORT).show();
                } else {
                    if (alert != null) {
                        addMessageToChatRoomFromAlert();
                    } else if (chatgroup != null) {
                        addMessage();
                    }
                }
            }
        });
        if (alert != null) {
            showMessageFromALert();
        } else if (chatgroup != null) {
            showMessageFromGroup();
        }
        setToolBar();
        checkIfStaffMember();
    }

    private void addMessage() {
        mFirestore.collection("USERS").document(mCurrentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String avatar = documentSnapshot.getString("imageProfileUrl");
                        String chatMessage = message.getText().toString();

                        Intent iin = getIntent();
                        Bundle extras = iin.getExtras();
                        String idGroup = extras.getString("ID_GROUP");
                        String organism = extras.getString("organism");
                        String chatid = extras.getString("chatid");
                        String nameGroup = extras.getString("NAME_GROUP");
                        String userID = extras.getString("userID");


                        //    String token = documentSnapshot.getString("token");
                        message.setText("");
                        String chatId = "Chat" + idGroupChat;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRANCE);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                        Date dateTime = new Date(System.currentTimeMillis());
                        ChatEntity chat = new ChatEntity(idGroupChat, chatId, mCurrentUserId, username, chatMessage,
                                avatar, dateTime, "", organism, false);


                        mFirestore.collection(organism)
                                .document("AllCampus")
                                .collection("AllChatGroups").document(idGroup)
                                .collection("Chats")
                                .add(chat);

                    }

                });
    }

    private void addMessageToChatRoomFromAlert() {
        mFirestore.collection("USERS").document(mCurrentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String avatar = documentSnapshot.getString("imageProfileUrl");
                        String organism = documentSnapshot.getString("organism");
                        String chatMessage = message.getText().toString();

                        Intent iin = getIntent();
                        Bundle extras = iin.getExtras();
                        alert = extras.getString("alert");
                        organismAlert = extras.getString("organismAlert");
                        userIdAlert = extras.getString("userIdAlert");
                        idGroupChat = extras.getString("idGroupChat");


                        //    String token = documentSnapshot.getString("token");
                        message.setText("");
                        String chatId = "Chat" + idGroupChat;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRANCE);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                        Date dateTime = new Date(System.currentTimeMillis());
                        ChatEntity chat = new ChatEntity(idGroupChat, chatId, mCurrentUserId, username, chatMessage,
                                avatar, dateTime, "", organism, false);


                        mFirestore.collection(organism)
                                .document("AllCampus")
                                .collection("AllChatGroups").document(idGroupChat)
                                .collection("Chats")
                                .add(chat);

                    }

                });
    }


    private void showMessageFromGroup() {
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        String idGroup = extras.getString("ID_GROUP");
        String organism = extras.getString("organism");
        String chatid = extras.getString("chatid");
        String nameGroup = extras.getString("NAME_GROUP");
        String userID = extras.getString("userID");

        Query query = db.collection(organism)
                .document("AllCampus")
                .collection("AllChatGroups")
                .document(idGroup)
                .collection("Chats")
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatEntity> options = new FirestoreRecyclerOptions.Builder<ChatEntity>()
                .setQuery(query, ChatEntity.class)
                .build();
        adapter = new AlertChatAdapter(options, AlertChatActivity.this, mCurrentUserId);
        RecyclerView recyclerView = findViewById(R.id.chatAlert);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int numberOfMessages = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (numberOfMessages - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
        transitionContainer = findViewById(R.id.toolbarLayoutChatRoom);
        imBt_ic_validate = transitionContainer.findViewById(R.id.imBt_ic_validate);

        imBt_ic_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlertFromGroup(organismAlert, idGroup);
            }
        });

    }

    private void showMessageFromALert() {
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        alert = extras.getString("alert");
        organismAlert = extras.getString("organismAlert");
        userIdAlert = extras.getString("userIdAlert");
        idGroupChat = extras.getString("idGroupChat");

        Query query = db.collection(organismAlert)
                .document("AllCampus")
                .collection("AllChatGroups")
                .document(idGroupChat)
                .collection("Chats")
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatEntity> options = new FirestoreRecyclerOptions.Builder<ChatEntity>()
                .setQuery(query, ChatEntity.class)
                .build();
        adapter = new AlertChatAdapter(options, AlertChatActivity.this, mCurrentUserId);
        RecyclerView recyclerView = findViewById(R.id.chatAlert);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int numberOfMessages = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (numberOfMessages - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
        transitionContainer = findViewById(R.id.toolbarLayoutChatRoom);
        imBt_ic_validate = transitionContainer.findViewById(R.id.imBt_ic_validate);

        imBt_ic_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlert(organismAlert, userIdAlert);
            }
        });
    }

    private void setToolBar() {
        transitionContainer = findViewById(R.id.toolbarLayoutChatRoom);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient_red));
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(R.string.Alert);
        imgBtBack.setVisibility(View.VISIBLE);
        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AlertChatActivity.this, GroupConversationActivity.class));
            }
        });
    }

    private void checkIfStaffMember() {
        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String organism = documentSnapshot.getString("organism");
                CollectionReference collectionReference = mFirestore.collection(organism).document("AllCampus").collection("AllEvents");
                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String eventId = document.getId();
                                CollectionReference collectionReferenceAlert = collectionReference.document(eventId).collection("Alert");
                                collectionReferenceAlert.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String idALert = document.getId();
                                                collectionReferenceAlert.document(idALert).collection("StaffOnGoing")
                                                        .document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if(documentSnapshot.exists()) {
                                                            transitionContainer = findViewById(R.id.toolbarLayoutChatRoom);
                                                            imBt_ic_validate = transitionContainer.findViewById(R.id.imBt_ic_validate);
                                                            imBt_ic_validate.setVisibility(View.VISIBLE);
                                                        }
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

    private void deleteAlert(String organism, String idUser) {

                                mFirestore.collection(organism).document("AllCampus")
                                        .collection("AllEvents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                            String idEvent = document.getId();
                                                mFirestore.collection(organism).document("AllCampus")
                                                        .collection("AllEvents").document(idEvent).collection("Alert").document(idUser)
                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if(documentSnapshot.exists()) {

                                                mFirestore.collection(organism).document("AllCampus")
                                                        .collection("AllEvents").document(idEvent).collection("Alert").document(idUser)
                                                        .collection("StaffOnGoing").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String idDoc = document.getId();
                                                                mFirestore.collection(organism).document("AllCampus")
                                                                        .collection("AllEvents").document(idEvent)
                                                                        .collection("Alert").document(idUser)
                                                                        .collection("StaffOnGoing").document(idDoc).delete();
                                                                mFirestore.collection(organism).document("AllCampus")
                                                                        .collection("AllEvents").document(idEvent)
                                                                        .collection("Alert").document(idUser).delete();
                                                            }
                                                        }
                                                    }
                                                });
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    }
                                });


    }

    private void deleteAlertFromGroup(String organism, String groupId) {
        mFirestore.collection(organism).document("AllCampus").collection("AllChatGroups")
                .document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String idUser = documentSnapshot.getString("idGroup");
                deleteAlert(organism, idUser);
            }
        });

    }
    private void openChatAlert() {
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        alert = extras.getString("alert");
        organismAlert = extras.getString("organismAlert");
        userIdAlert = extras.getString("userIdAlert");
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        //  FirestoreHelper.setStatusUser(organismUser, idGroup, mCurrentUserId, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
        //   FirestoreHelper.setStatusUser(organismUser, idGroup, mCurrentUserId, false);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean screenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            screenOn = pm.isInteractive();
        } else {
            screenOn = pm.isScreenOn();
        }
        if (!screenOn) {    //Screen off by lock or power
            Intent checkingIntent = new Intent(this, GroupConversationActivity.class);
            checkingIntent.putExtra("checking", true);
            checkingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(checkingIntent);
            finish();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setImageUser();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void setImageUser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageProfileUri = data.getData();

            final StorageReference imgReference = mStroageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageProfileUri));

            String link = imgReference.toString();
            if (imageProfileUri != null) {
                imgReference.putFile(imageProfileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        mFirestore.collection("USERS").document(mCurrentUserId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String username = documentSnapshot.getString("username");
                                        String avatar = documentSnapshot.getString("imageProfileUrl");
                                        String organism = documentSnapshot.getString("organism");
                                        message.setText("");
                                        String chatId = "Chat" + idGroupChat;
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRANCE);
                                        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                                        Date dateTime = new Date(System.currentTimeMillis());
                                        ChatEntity chat = new ChatEntity(idGroupChat, chatId, mCurrentUserId, username, "",
                                                avatar, dateTime, link, organism, false);
                                        FirestoreHelper.addChat(organism, idGroupChat, chat);

                                    }
                                });
                    }
                });
            }
        }
    }
}
