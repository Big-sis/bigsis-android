package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class EventListActivity extends BigsisActivity {

    FirebaseFirestore mFirestore;
    @BindView(R.id.rv_list_event)
    RecyclerView mRecycler;
    private FloatingActionButton buttonMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        ButterKnife.bind(this);
        mFirestore = FirebaseFirestore.getInstance();

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
                //addTrip();
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
              /*  Fragment addMenu = manager.findFragmentByTag("ADD_MENU_FRAGMENT");
                Fragment searchMenu = manager.findFragmentByTag("SEARCH_MENU_FRAGMENT");
                if (addMenu != null) {
                    ft.remove(addMenu).commitAllowingStateLoss();
                }
                if (searchMenu != null) {
                    ft.remove(searchMenu).commitAllowingStateLoss();
                }*/

                tvTitleToolbar.setText(R.string.events);
                KeyboardHelper.CloseKeyboard(EventListActivity.this, view);
            }
        });
    }
}
