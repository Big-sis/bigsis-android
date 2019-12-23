package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.bigsis.android.R;
import fr.bigsis.android.view.CurvedBottomNavigationView;

import static android.view.View.GONE;

public class SettingsActivity extends BigsisActivity {

    TextView tvProfile, tvPassword, tvLogOut, tvDeleteAccount;
    FloatingActionButton fbtGoToMapUpdate;
    FirebaseAuth mAuth;
    String userId;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvProfile = findViewById(R.id.tvProfile);
        tvPassword = findViewById(R.id.tvPassword);
        tvLogOut = findViewById(R.id.tvLogOut);
        tvDeleteAccount = findViewById(R.id.tvDeleteAccount);
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
        MenuItem selectedItem =
                curvedBottomNavigationView.getMenu().getItem(2);
        MenuItem item = curvedBottomNavigationView.getMenu().findItem(R.id.action_user_profile);
        item.setIcon(R.drawable.ic_profile_selected);
        selectItem(selectedItem, curvedBottomNavigationView);
        fbtGoToMapUpdate = findViewById(R.id.fbtGoToMapUpdate);
        fbtGoToMapUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, MapsActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        tvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO UPDATE PROFILE
                Toast.makeText(SettingsActivity.this, "You'll be able to update your informations but not now :D", Toast.LENGTH_LONG).show();
            }
        });

        tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO UPDATE PROFILE
                Toast.makeText(SettingsActivity.this, "You'll be able to update your password but not now :D", Toast.LENGTH_LONG).show();
            }
        });

        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });

        tvDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO UPDATE PROFILE
                Toast.makeText(SettingsActivity.this, "You'll be able to update your password but not now :D", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setToolBar() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        transitionContainer = findViewById(R.id.toolbarLayout);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imgBtBack = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        imgBtBack.setVisibility(View.VISIBLE);
        tvTitleToolbar.setText(R.string.settings);

        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, UserProfileActivity.class));
            }
        });
    }


}
