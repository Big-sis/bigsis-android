package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import fr.bigsis.android.R;
import fr.bigsis.android.view.CurvedBottomNavigationView;

import static fr.bigsis.android.helpers.UpdateProfileHelper.updateFieldAllUsers;
import static fr.bigsis.android.helpers.UpdateProfileHelper.updateFieldProfile;
import static fr.bigsis.android.helpers.UpdateProfileHelper.updateProfileFriends;

public class UpdateProfileActivity extends BigsisActivity {

    FloatingActionButton fbtGoToMapUserProfile;
    EditText etUsernameUpdate, etNameUpdate, etFirstnameUpdate, etDescriptionUpdate;
    Button btUpdate;
    ProgressBar progressBarUpdate;
    String usernameUpdate, nameUpdate, firstnameUpdate, descriptionUpdate, mCurrentUserId;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        etUsernameUpdate = findViewById(R.id.etUsernameUpdate);
        etNameUpdate = findViewById(R.id.etNameUpdate);
        etFirstnameUpdate = findViewById(R.id.etFirstnameUpdate);
        etDescriptionUpdate = findViewById(R.id.etDescriptionUpdate);
        btUpdate = findViewById(R.id.btUpdate);
        progressBarUpdate = findViewById(R.id.progressBarUpdate);

        getInfoProfile();
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
        fbtGoToMapUserProfile = findViewById(R.id.fbtGoToMapUserProfile);
        fbtGoToMapUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateProfileActivity.this, MapsActivity.class));
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
        tvTitleToolbar.setText(R.string.update_profile);

        imgBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateProfileActivity.this, SettingsActivity.class));
            }
        });
    }

    private void getInfoProfile() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFirestore.collection("USERS").document(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String getFirstname = documentSnapshot.getString("firstname");
                String getLastname = documentSnapshot.getString("lastname");
                String getUsername = documentSnapshot.getString("username");
                String getDescription = documentSnapshot.getString("description");
                etFirstnameUpdate.setText(getFirstname);
                etNameUpdate.setText(getLastname);
                etUsernameUpdate.setText(getUsername);
                etDescriptionUpdate.setText(getDescription);
                btUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfile(getFirstname, getLastname, getUsername, getDescription);
                        updateFieldProfile(mCurrentUserId, "AllTrips", "Creator");
                        updateFieldProfile(mCurrentUserId, "AllTrips", "Participants");

                        updateFieldProfile(mCurrentUserId, "AllEvents", "StaffMembers");
                        updateFieldProfile(mCurrentUserId, "AllEvents", "Participants");
                        updateFieldProfile(mCurrentUserId, "AllEvents", "Creator");

                        updateFieldProfile(mCurrentUserId, "AllChatGroups", "Creator");
                        updateFieldProfile(mCurrentUserId, "AllChatGroups", "Participants");
                        updateFieldProfile(mCurrentUserId, "AllChatGroups", "StaffMembers");

                        updateFieldAllUsers(mCurrentUserId);

                        updateProfileFriends(mCurrentUserId, "RequestReceived");
                        updateProfileFriends(mCurrentUserId, "RequestSent");
                        updateProfileFriends(mCurrentUserId, "Friends");

                    }
                });
            }
        });
    }

    private void updateProfile(String firstnameUser, String lastnameUser, String userNameUser, String descriptionUser) {
        progressBarUpdate.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        firstnameUpdate = etFirstnameUpdate.getText().toString();
        nameUpdate = etNameUpdate.getText().toString();
        usernameUpdate = etUsernameUpdate.getText().toString();
        descriptionUpdate = etDescriptionUpdate.getText().toString();
        DocumentReference documentReference = mFirestore.collection("USERS").document(mCurrentUserId);
        if (!firstnameUser.equals(firstnameUpdate)) {
            documentReference.update("firstname", firstnameUpdate);
            startActivity(new Intent(UpdateProfileActivity.this, UserProfileActivity.class));
        }
        if (!lastnameUser.equals(nameUpdate)) {
            documentReference.update("lastname", nameUpdate);
            startActivity(new Intent(UpdateProfileActivity.this, UserProfileActivity.class));
        }
        if (!descriptionUser.equals(descriptionUpdate)) {
            documentReference.update("description", descriptionUpdate.toLowerCase().trim());
            startActivity(new Intent(UpdateProfileActivity.this, UserProfileActivity.class));
        }

        if (!userNameUser.equals(usernameUpdate)) {
            mFirestore = FirebaseFirestore.getInstance();
            CollectionReference usersRef = mFirestore.collection("USERS");
            Query query = usersRef.whereEqualTo("username", usernameUpdate);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String user = documentSnapshot.getString("username").toLowerCase().trim();
                            if (user.equals(usernameUpdate.toLowerCase())) {
                                etUsernameUpdate.setError(getString(R.string.username_exist));
                                etUsernameUpdate.requestFocus();
                                return;
                            } else {
                                documentReference.update("username", usernameUpdate.toLowerCase().trim());
                                startActivity(new Intent(UpdateProfileActivity.this, UserProfileActivity.class));
                            }
                        }
                    }
                }
            });
        }
    }
}
