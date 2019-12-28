package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.adapter.EventListAdapter;
import fr.bigsis.android.entity.EventEntity;
import fr.bigsis.android.fragment.AddEventFragment;
import fr.bigsis.android.fragment.ChooseParticipantFragment;
import fr.bigsis.android.fragment.ChooseStaffFragment;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;
import fr.bigsis.android.viewModel.ChooseUsersViewModel;

public class EventListActivity extends BigsisActivity implements AddEventFragment.OnFragmentInteractionListener,
        ChooseStaffFragment.OnFragmentInteractionListener, ChooseParticipantFragment.OnFragmentInteractionListener{

    FirebaseFirestore mFirestore;
    AddEventFragment fragmentAdd = AddEventFragment.newInstance();
    ChooseParticipantFragment chooseUsersFragment = ChooseParticipantFragment.newInstance();
    private FloatingActionButton buttonMap;
    private ChooseUsersViewModel viewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private ImageButton imBt_ic_back_frag;
    @BindView(R.id.swipe_refresh_layout_event)
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        ButterKnife.bind(this);

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
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imBtAdd = transitionContainer.findViewById(R.id.imBt_add_frag);
        imBt_ic_back_frag = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(R.string.events);
        imBtAdd.setVisibility(View.VISIBLE);
        FragmentManager manager = getSupportFragmentManager();

        if(chooseUsersFragment.isAdded()){
            imBt_ic_back_frag.setVisibility(View.VISIBLE);
        }
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
                FragmentTransaction ft = manager.beginTransaction();
                Fragment addFrag = manager.findFragmentByTag("ADD_EVENT_FRAGMENT");
                ft.remove(fragmentAdd).commitAllowingStateLoss();
                onBackPressed();

               /* if (addFrag != null) {
                    ft.remove(addFrag).commitAllowingStateLoss();
                }*/
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

        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nameCampus = documentSnapshot.getString("groupCampus");
                String organism = documentSnapshot.getString("organism");

                PagedList.Config config = new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPrefetchDistance(10)
                        .setPageSize(20)
                        .build();

                Query query = FirebaseFirestore.getInstance().collection(organism).document("AllCampus")
                        .collection("AllCampus").document(nameCampus)
                        .collection("Events").orderBy("dateStart");
                FirestorePagingOptions<EventEntity> options = new FirestorePagingOptions.Builder<EventEntity>()
                        .setLifecycleOwner(EventListActivity.this)
                        .setQuery(query, config, EventEntity.class)
                        .build();

                EventListAdapter adapter = new EventListAdapter(options, EventListActivity.this, mSwipeRefreshLayout, nameCampus, organism, fragmentAdd);
                RecyclerView mRecycler = findViewById(R.id.rv_list_event);
                mRecycler.setLayoutManager(new LinearLayoutManager(EventListActivity.this));
                mRecycler.setAdapter(adapter);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        adapter.refresh();
                    }
                });
            }
        });
    }

    @Override
    public void onFragmentInteractionAddEvent() {
    }

    @Override
    public void onFragmentInteraction() {

    }

}
