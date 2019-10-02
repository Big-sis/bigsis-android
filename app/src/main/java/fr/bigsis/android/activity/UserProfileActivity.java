package fr.bigsis.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.ToolBarFragment;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public class UserProfileActivity extends AppCompatActivity implements ToolBarFragment.OnFragmentInteractionListener {
    ImageButton imgBtProfile, imgBtBack, imBtSettings;
    FloatingActionButton fbTrip;
    ConstraintLayout transitionContainer;
     FirebaseAuth mAuth;
     String userId, user_name;
     FirebaseFirestore mFirestore;
     TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

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
                startActivity(new Intent(UserProfileActivity.this, TripListActivity.class));
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
                        user_name = documentSnapshot.getString("username");
                        String description  = documentSnapshot.getString("description");
                        Toast.makeText(UserProfileActivity.this, user_name, Toast.LENGTH_SHORT).show();
                        transitionContainer = (ConstraintLayout) findViewById(R.id.toolbarLayout);
                        tvUserName = (TextView) transitionContainer.findViewById(R.id.tvTitleToolbar);
                        tvUserName.setText(user_name);

                    }
                });

    }

    private void setToolBar() {
        transitionContainer = (ConstraintLayout) findViewById(R.id.toolbarLayout);
        imgBtProfile = (ImageButton) transitionContainer.findViewById(R.id.imBt_ic_profile_frag);
        imgBtBack = (ImageButton) transitionContainer.findViewById(R.id.imBt_ic_back_frag);
        imBtSettings = (ImageButton) transitionContainer.findViewById(R.id.imBt_ic_setting);
        imgBtProfile.setVisibility(View.VISIBLE);
        imBtSettings.setVisibility(View.VISIBLE);

        imgBtProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionContainer);
                imgBtProfile.setVisibility(View.GONE);
                imgBtBack.setVisibility(View.VISIBLE);
                Toast.makeText(UserProfileActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });

        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionContainer);
                imgBtProfile.setVisibility(View.VISIBLE);
                imgBtBack.setVisibility(View.GONE);
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

    private boolean selectItem(@NonNull MenuItem item, CurvedBottomNavigationView curvedBottomNavigationView) {
        switch (item.getItemId()) {
            case R.id.action_user_profile:
                Toast.makeText(UserProfileActivity.this, "hello", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_message:
                Toast.makeText(UserProfileActivity.this, "ddd", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_events:
                Toast.makeText(UserProfileActivity.this, "ii", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_route:
                Toast.makeText(UserProfileActivity.this, "hh", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}
