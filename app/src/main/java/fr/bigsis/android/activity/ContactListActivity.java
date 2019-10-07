package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import fr.bigsis.android.R;
import fr.bigsis.android.fragment.SearchContactFragment;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class ContactListActivity extends AppCompatActivity implements SearchContactFragment.OnFragmentInteractionContact {

    FloatingActionButton fbTrip;
    ConstraintLayout transitionContainer;
    ImageButton imgBtBack, imBtSearch;
    TextView tvTitle;
    SearchContactFragment fragmentProfile = SearchContactFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        setToolBar();
        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });
        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        selectItem(selectedItem, curvedBottomNavigationView);
        fbTrip = findViewById(R.id.fbTrip);
        fbTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactListActivity.this, TripListActivity.class));
            }
        });
    }

    private void setToolBar() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        imBtSearch = transitionContainer.findViewById(R.id.imBt_search_right_frag);
        tvTitle = transitionContainer.findViewById(R.id.tvTitleToolbar);
        imgBtBack.setVisibility(View.VISIBLE);
        imBtSearch.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.contacts);

        imBtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionContainer);
                imBtSearch.setVisibility(View.GONE);
             //   openFragment();
            }
        });

        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private boolean selectItem(@NonNull MenuItem item, CurvedBottomNavigationView curvedBottomNavigationView) {
        switch (item.getItemId()) {
            case R.id.action_user_profile:
                startActivity(new Intent(ContactListActivity.this, UserProfileActivity.class));

                return true;
            case R.id.action_message:
                Toast.makeText(ContactListActivity.this, "ddd", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_events:
                Toast.makeText(ContactListActivity.this, "ii", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_route:
                Toast.makeText(ContactListActivity.this, "hh", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    public void openFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_to_bottom, R.animator.exit_to_top, R.animator.enter_to_bottom, R.animator.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_profile, fragmentProfile, "PROFILE_USER_FRAGMENT")
                .commit();
    }

    @Override
    public void onFragmentInteractionContact() {
        onBackPressed();
    }
}
