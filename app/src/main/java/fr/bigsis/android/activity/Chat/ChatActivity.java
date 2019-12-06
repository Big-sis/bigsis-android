package fr.bigsis.android.activity.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.BigsisActivity;
import fr.bigsis.android.adapter.ChatAdapter;
import fr.bigsis.android.entity.ChatEntity;

public class ChatActivity extends BigsisActivity {

    private FirebaseFirestore mFirestore;
    private EditText message;
    private ImageButton send;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private String idGroup;
    private ChatAdapter adapter;
    private RecyclerView chats;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        idGroup = extras.getString("ID_GROUP");
        message = findViewById(R.id.message_text);
        send = findViewById(R.id.send_message);
        chats = findViewById(R.id.chats);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chats.setLayoutManager(manager);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().isEmpty()) {
                    Toast.makeText(
                            ChatActivity.this,
                            "erreur",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    addMessageToChatRoom();
                }
            }
        });
        showMessage();
    }

    private void addMessageToChatRoom() {
        mFirestore.collection("users").document(mCurrentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String chatMessage = message.getText().toString();
                        message.setText("");
                        String chatId = idGroup + "Chat";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRANCE);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                        Date dateTime = new Date(System.currentTimeMillis());
                        ChatEntity chat = new ChatEntity(idGroup, chatId, mCurrentUserId, username, chatMessage, dateTime);
                        mFirestore.collection("GroupChat")
                                .document(idGroup)
                                .collection("chat")
                                .add(chat);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);
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
    }
}
