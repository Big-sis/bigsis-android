package fr.bigsis.android.activity.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.BigsisActivity;
import fr.bigsis.android.adapter.ChatsAdapter;
import fr.bigsis.android.entity.ChatEntity;

public class ChatActivity extends BigsisActivity {

    private String userId = "";
    private FirebaseFirestore mFirestore;
    private EditText message;
    private ImageButton send;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private String idGroup;
    private ChatsAdapter adapter;
    private RecyclerView chats;

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
        showChatMessages();
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
                ChatEntity chat = new ChatEntity(idGroup, chatId, mCurrentUserId, username, chatMessage, System.currentTimeMillis());
                mFirestore.collection("GroupChat")
                        .document(idGroup)
                        .collection("chat")
                        .add(chat);
            }
        });
    }

    private void showChatMessages() {
        mFirestore.collection("GroupChat")
                .document(idGroup)
                .collection("chat")
                .orderBy("sent", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<ChatEntity> messages = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            messages.add(
                                    new ChatEntity(
                                            doc.getId(),
                                            doc.getString("chatRoomId"),
                                            doc.getString("id"),
                                            doc.getString("username"),
                                            doc.getString("message"),
                                            doc.getLong("sent")
                                    )
                            );
                        }
                        adapter = new ChatsAdapter(messages, userId);
                        chats.setAdapter(adapter);
                    }
                });

    }
}
