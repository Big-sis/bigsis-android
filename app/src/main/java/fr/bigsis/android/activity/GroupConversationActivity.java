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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.adapter.GroupConversationAdapter;
import fr.bigsis.android.entity.GroupChatEntity;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class GroupConversationActivity extends BigsisActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton fbtGoToMap;
    private GroupConversationAdapter adapter;
    private ImageButton imBt_notification;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_conversation);

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
        imBt_notification = transitionContainer.findViewById(R.id.imBt_notification);
        imBt_notification.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.conversations);
    }

    private void setUpRecyclerView() {
        Query query = db.collection("GroupChat");
        FirestoreRecyclerOptions<GroupChatEntity> options = new FirestoreRecyclerOptions.Builder<GroupChatEntity>()
                .setQuery(query, GroupChatEntity.class)
                .build();
        adapter = new GroupConversationAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
