package fr.bigsis.android.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fr.bigsis.android.R;
import fr.bigsis.android.fragment.AddEventFragment;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class EventListActivity extends BigsisActivity implements AddEventFragment.OnFragmentInteractionListener {

    AddEventFragment fragmentOpen = AddEventFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        setToolBar();

        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        curvedBottomNavigationView.setSelectedItemId(R.id.action_events);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });
        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        selectItem(selectedItem, curvedBottomNavigationView);
    }

    private void setToolBar() {
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imbtSearch = transitionContainer.findViewById(R.id.imBt_search_frag);
        imBtAdd = transitionContainer.findViewById(R.id.imBt_add_frag);
        imBtCancel = transitionContainer.findViewById(R.id.imBt_cancel_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(R.string.events);
        //TODO IF ADMIN
        imBtAdd.setVisibility(View.VISIBLE);

        imBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment();
                imBtAdd.setVisibility(View.GONE);
                imBtCancel.setVisibility(View.VISIBLE);
                tvTitleToolbar.setText(R.string.create_event);
            }
        });
    }

    public void openFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_event, fragmentOpen, "SEARCH_MENU_FRAGMENT")
                .commit();
    }

    @Override
    public void onFragmentInteractionEvent() {
        onBackPressed();
    }

}
