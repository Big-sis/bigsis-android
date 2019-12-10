package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.Chat.ChatActivity;
import fr.bigsis.android.adapter.GroupConversationAdapter;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class GroupConversationActivity extends BigsisActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton fbtGoToMap;
    private GroupConversationAdapter adapter;
    private ImageButton imBtParticipant;
    private TextView tvTitle;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_conversation);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        curvedBottomNavigationView.setSelectedItemId(R.id.action_message);
        curvedBottomNavigationView.setItemIconTintList(null);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });
        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_message);
        item.setIcon(R.drawable.ic_dialog_selected);
        selectItem(selectedItem, curvedBottomNavigationView);
        fbtGoToMap = findViewById(R.id.fbtGoToMap);
        fbtGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroupConversationActivity.this, MapsActivity.class));
            }
        });
        setToolBar();
        setUpRecyclerView();
    }

    private void setToolBar() {
        transitionContainer = findViewById(R.id.toolbarLayoutGroup);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        tvTitle = transitionContainer.findViewById(R.id.tvTitleToolbar);
        imBtParticipant = transitionContainer.findViewById(R.id.imBt_participant);
        imBtParticipant.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.conversations);
    }

    private void setUpRecyclerView() {
        Query query = db.collection("users").document(mCurrentUserId).collection("groupChat");
        FirestoreRecyclerOptions<GroupChatEntity> options = new FirestoreRecyclerOptions.Builder<GroupChatEntity>()
                .setQuery(query, GroupChatEntity.class)
                .build();
        adapter = new GroupConversationAdapter(options, GroupConversationActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

       adapter.setOnItemClickListener(new GroupConversationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String id = documentSnapshot.getId();
                Intent intent = new Intent(GroupConversationActivity.this, ChatActivity.class);
                intent.putExtra("Id_Group", id);
                startActivity(intent);
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
    }
}
