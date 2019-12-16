package fr.bigsis.android.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import fr.bigsis.android.adapter.ChatAdapter;
import fr.bigsis.android.entity.ChatEntity;
import fr.bigsis.android.helpers.FirestoreHelper;
import fr.bigsis.android.viewModel.ChatViewModel;


public class ChatActivity extends BigsisActivity {

    private FirebaseFirestore mFirestore;
    private EditText message;
    private ImageButton send, addImage, imBt_participant;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private String idGroup;
    private String titleGroup;
    private ChatAdapter adapter;
    private RecyclerView chats;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Uri imageProfileUri;
    private CircleImageView circleImageView;
    private StorageReference mStroageReference;
    private int STORAGE_PERMISSION_CODE = 2;
    public static String CHANNEL_ID = "ID_notif";
    private ChatViewModel viewModel;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        viewModel = ViewModelProviders.of(ChatActivity.this).get(ChatViewModel.class);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        idGroup = extras.getString("ID_GROUP");
        titleGroup = extras.getString("NAME_GROUP");
        message = findViewById(R.id.message_text);
        send = findViewById(R.id.send_message);
        chats = findViewById(R.id.chats);
        addImage = findViewById(R.id.add_image);
        linearLayout = findViewById(R.id.frameLayoutContainerGroup);
        ChatEntity.setGroup_ID(ChatActivity.this, idGroup);
        FirestoreHelper.setStatusUser(idGroup, mCurrentUserId, true);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
                    Toast.makeText(ChatActivity.this, "erreur", Toast.LENGTH_SHORT).show();
                } else {
                    addMessageToChatRoom();
                }
            }
        });

        mFirestore.collection("GroupChat").document(idGroup).collection("staffMembers")
                .document(mCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        showMessage();
        setToolBar();
       // updateToken();
    }

    private void showEditTextForStaffs(){
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        idGroup = extras.getString("ID_GROUP");

    }
    private void setToolBar() {
        transitionContainer = findViewById(R.id.toolbarLayoutChatRoom);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        imBt_participant = transitionContainer.findViewById(R.id.imBt_participant);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(titleGroup);
        imgBtBack.setVisibility(View.VISIBLE);
        imBt_participant.setVisibility(View.VISIBLE);
        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, GroupConversationActivity.class));
            }
        });

        imBt_participant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ParticipantsListActivity.class);
                intent.putExtra("ID_GROUP", idGroup);
                startActivity(intent);
            }
        });
    }

    /*private void updateToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String uid = FirebaseAuth.getInstance().getUid();

        if(uid != null) {
            mFirestore.collection("users")
                    .document(uid)
                    .update("token",token);

        }
    }*/

    private void addMessageToChatRoom() {
        mFirestore.collection("users").document(mCurrentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String avatar = documentSnapshot.getString("imageProfileUrl");
                        String chatMessage = message.getText().toString();
                        String token = documentSnapshot.getString("token");
                        message.setText("");
                        String chatId = idGroup + "Chat";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRANCE);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                        Date dateTime = new Date(System.currentTimeMillis());
                        ChatEntity chat = new ChatEntity(idGroup, chatId, mCurrentUserId, username, chatMessage,
                                avatar, dateTime,"", false);
                        ChatEntity notification = new ChatEntity(idGroup, mCurrentUserId, username, chatMessage,
                                dateTime);
                        FirestoreHelper.addData("GroupChat", idGroup,"chat", chat);
                        FirestoreHelper.updateData("GroupChat", idGroup,"notification",
                                "IDnotification", notification);
                        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

                      /*  mFirestore.collection("notification").document("IDnotification")
                                .set(notification);*/

                        mFirestore.collection("GroupChat")
                                .document(idGroup).collection("participants")
                                .whereEqualTo("online", false).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            notification.setUsername(username);
                                            notification.setMessage(chatMessage);
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String userId = document.getData().get("token").toString();
                                                Toast.makeText(ChatActivity.this, userId,Toast.LENGTH_SHORT).show();
                                                mFirestore.collection("GroupChat")
                                                        .document(idGroup).collection("participants")
                                                        .document(userId)
                                                        .collection("notif")
                                                .document("notifId").set(notification);
                                                mFirestore.collection("notification").document(token).set(notification);

                                            }
                                        }
                                    }
                                            });
                                }

                                });
                    }


    private void showMessage() {
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        idGroup = extras.getString("ID_GROUP");
        Query query = db.collection("GroupChat")
                .document(idGroup)
                .collection("chat")
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatEntity> options = new FirestoreRecyclerOptions.Builder<ChatEntity>()
                .setQuery(query, ChatEntity.class)
                .build();
        adapter = new ChatAdapter(options, ChatActivity.this, mCurrentUserId);
        RecyclerView recyclerView = findViewById(R.id.chats);
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
        FirestoreHelper.setStatusUser(idGroup, mCurrentUserId, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
        FirestoreHelper.setStatusUser(idGroup, mCurrentUserId, false);
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
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                        mFirestore.collection("users").document(mCurrentUserId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String username = documentSnapshot.getString("username");
                                        String avatar = documentSnapshot.getString("imageProfileUrl");
                                        message.setText("");
                                        String chatId = idGroup + "Chat";
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRANCE);
                                        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                                        Date dateTime = new Date(System.currentTimeMillis());
                                        ChatEntity chat = new ChatEntity(idGroup, chatId, mCurrentUserId, username, "",
                                                avatar, dateTime, link, false);
                                        FirestoreHelper.addData("GroupChat", idGroup,"chat", chat);

                                    }
                                });
                        /*mFirestore.collection("GroupChat")
                                .document(idGroup)
                                .collection("chat")
                                .add(chat);*/

                    //    String user_id = mFirebaseAuth.getCurrentUser().getUid();
                      /*  FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("GroupChat")
                                .document(idGroup)
                                .collection("chat")
                                .document(viewModel.getIdMessage().toString())
                                .update("imageMessage", link).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ChatActivity.this, "Votre  a bien été modifiée",
                                        Toast.LENGTH_LONG).show();
                            }
                        });*/
                    }
                });
            }
        }
    }

        public void setNotificatios(int notificationId){

            Intent intent = new Intent(this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_dialog_selected)
                    .setContentTitle("titile")
                    .setContentText("text")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());

        }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
