package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.bigsis.android.R;
import fr.bigsis.android.adapter.EventListAdapter;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.fragment.AddEventFragment;
import fr.bigsis.android.fragment.ChooseFragment;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;
import fr.bigsis.android.viewModel.ChooseUsersViewModel;

public class EventListActivity extends BigsisActivity implements AddEventFragment.OnFragmentInteractionListener, ChooseFragment.OnFragmentInteractionListener {

    FirebaseFirestore mFirestore;
    AddEventFragment fragmentAdd = AddEventFragment.newInstance();
    ChooseFragment chooseUsersFragment = ChooseFragment.newInstance();
    EventListAdapter adapter;
    private FloatingActionButton buttonMap;
    private ChooseUsersViewModel viewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mCurrentUserId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        mFirestore = FirebaseFirestore.getInstance();
        viewModel = ViewModelProviders.of(this).get(ChooseUsersViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        curvedBottomNavigationView.setSelectedItemId(R.id.action_events);
        curvedBottomNavigationView.setItemIconTintList(null);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });
        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_events);
        item.setIcon(R.drawable.ic_event_selected);
        selectItem(selectedItem, curvedBottomNavigationView);
        buttonMap = findViewById(R.id.fbMapEvent);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventListActivity.this, MapsActivity.class));
            }
        });
        setToolBar();
        setUpRecyclerView();
    }

    private void setToolBar() {
        transitionContainer = findViewById(R.id.toolbarLayoutEvent);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imBtAdd = transitionContainer.findViewById(R.id.imBt_add_frag);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(R.string.events);
        imBtAdd.setVisibility(View.VISIBLE);

        imBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentAddEvent();
                imBtAdd.setVisibility(View.GONE);
                imBtCancel.setVisibility(View.VISIBLE);
                tvTitleToolbar.setText(R.string.create_event);
            }
        });

        imBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imBtCancel.setVisibility(View.GONE);
                imBtAdd.setVisibility(View.VISIBLE);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addFrag = manager.findFragmentByTag("ADD_EVENT_FRAGMENT");
                if (addFrag != null) {
                    ft.remove(addFrag).commitAllowingStateLoss();
                }
                tvTitleToolbar.setText(R.string.events);
                KeyboardHelper.CloseKeyboard(EventListActivity.this, view);
            }
        });
    }

    private void openFragmentAddEvent() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_event, fragmentAdd, "ADD_EVENT_FRAGMENT")
                .commit();
    }

    private void setUpRecyclerView() {
        Query query = db.collection("events");
        FirestoreRecyclerOptions<EventEntity> options = new FirestoreRecyclerOptions.Builder<EventEntity>()
                .setQuery(query, EventEntity.class)
                .build();
        adapter = new EventListAdapter(options, EventListActivity.this);
        RecyclerView recyclerView = findViewById(R.id.rv_list_event);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteractionAddEvent() {
    }

    @Override
    public void onFragmentInteraction() {

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
