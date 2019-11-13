package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.bigsis.android.R;
import fr.bigsis.android.fragment.ProfileFragment;
import fr.bigsis.android.fragment.ToolBarFragment;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class UserProfileActivity extends BigsisActivity implements ToolBarFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListenerProfile {
    ImageButton imgBtProfile, imgBtBack, imBtSettings;
    FloatingActionButton fbTrip;
    ConstraintLayout transitionContainer;
    FirebaseAuth mAuth;
    String userId;
    FirebaseFirestore mFirestore;
    TextView tvUserName;
    ProfileFragment fragmentProfile = ProfileFragment.newInstance();
    Button btContact, btAdvice;
    String firstname, lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setToolBar();
        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        curvedBottomNavigationView.setSelectedItemId(R.id.action_user_profile);
        curvedBottomNavigationView.setItemIconTintList(null);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });
        MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_user_profile);
        item.setIcon(R.drawable.ic_profile_selected);
        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        selectItem(selectedItem, curvedBottomNavigationView);
        fbTrip = findViewById(R.id.fbTrip);
        fbTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this, TripListActivity.class));
            }
        });
        btContact = findViewById(R.id.btContact);
        btContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, ContactListActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
                .document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        firstname = documentSnapshot.getString("firstname");
                        lastname = documentSnapshot.getString("lastname");
                        transitionContainer = findViewById(R.id.toolbarLayout);
                        tvUserName = transitionContainer.findViewById(R.id.tvTitleToolbar);
                        tvUserName.setText(firstname + " " + lastname);
                    }
                });
    }

    private void setToolBar() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imgBtProfile = transitionContainer.findViewById(R.id.imBt_ic_profile_frag);
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        imBtSettings = transitionContainer.findViewById(R.id.imBt_ic_setting);
        tvUserName = transitionContainer.findViewById(R.id.tvTitleToolbar);
        imgBtProfile.setVisibility(View.VISIBLE);
        imBtSettings.setVisibility(View.VISIBLE);

        imgBtProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionContainer);
                imgBtProfile.setVisibility(View.GONE);
                imgBtBack.setVisibility(View.VISIBLE);
                openFragment();
            }
        });

        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionContainer);
                onFragmentInteractionProfile();
                imgBtProfile.setVisibility(View.VISIBLE);
                imgBtBack.setVisibility(View.GONE);
                tvUserName.setText(firstname + " " + lastname);
            }
        });

        imBtSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    public void onFragmentInteractionTool() {
        onBackPressed();
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
    public void onFragmentInteractionProfile() {
        onBackPressed();
    }
}
