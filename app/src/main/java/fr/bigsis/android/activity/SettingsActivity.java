package fr.bigsis.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.helpers.FirestoreHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;

import static fr.bigsis.android.helpers.FirestoreHelper.deleteUserFromOrganism;

public class SettingsActivity extends BigsisActivity {

    TextView tvPolicy, tvCGU, tvLogOut, tvDeleteAccount;
    FloatingActionButton fbtGoToMapUpdate;
    FirebaseAuth mAuth;
    String userId;
    FirebaseFirestore mFirestore;
    RelativeLayout relativeLayoutLanguage, relariveLayoutCampus, relariveLayoutCGU, relativeLayoutPolicy, relativeLayoutProfile;
    private Locale myLocale;
    private Locale current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        tvLogOut = findViewById(R.id.tvLogOut);
        tvDeleteAccount = findViewById(R.id.tvDeleteAccount);
        tvCGU = findViewById(R.id.tvCGU);
        tvPolicy = findViewById(R.id.tvPolicy);
        relariveLayoutCampus = findViewById(R.id.relariveLayoutCampus);
        relativeLayoutLanguage = findViewById(R.id.relativeLayoutLanguage);
        relariveLayoutCGU = findViewById(R.id.relariveLayoutCGU);
        relativeLayoutPolicy = findViewById(R.id.relativeLayoutPolicy);
        relativeLayoutProfile = findViewById(R.id.relativeLayoutProfile);
        tvPolicy.getPaint().setUnderlineText(true);
        tvCGU.getPaint().setUnderlineText(true);


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
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        relariveLayoutCGU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PolicyCGUActivity.class);
                intent.putExtra("CGU", "cgu");
                startActivity(intent);
            }
        });

        relativeLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, UpdateProfileActivity.class);

                startActivity(intent);
            }
        });

        relativeLayoutPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PolicyCGUActivity.class);
                intent.putExtra("POLICY", "policy");
                startActivity(intent);
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
                AlertDialog dialogBuilder = new AlertDialog.Builder(SettingsActivity.this).create();
                LayoutInflater inflater = LayoutInflater.from(SettingsActivity.this);
                View dialogView = inflater.inflate(R.layout.style_alert_dialog, null);
                Button btNo = dialogView.findViewById(R.id.btNo);
                TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
                Button btDelete = dialogView.findViewById(R.id.btDeleteFriend);
                tvTitleDialog.setText(R.string.want_to_delete_account);

                btDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFirestore.collection("USERS")
                                .document(userId)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String organism = documentSnapshot.getString("organism");
                                String groupCampus = documentSnapshot.getString("groupCampus");
                                FirestoreHelper.deleteUserFromCampus(organism, groupCampus, userId);
                                FirestoreHelper.deleteUserFromDbUsers(userId, "ChatGroup");
                                FirestoreHelper.deleteUserFromDbUsers(userId, "EventList");
                                FirestoreHelper.deleteUserFromDbUsers(userId, "Friends");
                                FirestoreHelper.deleteUserFromDbUsers(userId, "RequestSent");
                                FirestoreHelper.deleteUserFromDbUsers(userId, "StaffOf");
                                FirestoreHelper.deleteUserFromDbUsers(userId, "TripList");
                                FirestoreHelper.deleteUserFromDbUsers(userId, "RequestReceived");
                                FirestoreHelper.deleteUser(userId);
                                FirestoreHelper.deleteUserFromAll(organism, userId);
                                deleteUserFromOrganism(organism, userId);
                                FirestoreHelper.deleteImageFromStorage(userId);
                            }
                        });
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                            }
                        });
                        dialogBuilder.dismiss();
                    }
                });
                btNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();

            }
        });
        relativeLayoutLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogBuilder = new AlertDialog.Builder(SettingsActivity.this).create();
                LayoutInflater inflater = LayoutInflater.from(SettingsActivity.this);
                View dialogView = inflater.inflate(R.layout.style_dialog_language, null);
                Button btValidate = dialogView.findViewById(R.id.btValidate);
                RadioButton radio_french = dialogView.findViewById(R.id.radio_french);
                RadioButton radio_english = dialogView.findViewById(R.id.radio_english);
                SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                        Activity.MODE_PRIVATE);
                String language = prefs.getString("Language", "");
                current = getResources().getConfiguration().locale;
                if (current.getLanguage().equals("fr")) {
                    radio_french.setChecked(true);
                } else if (current.getLanguage().equals("en")) {
                    radio_english.setChecked(true);
                }
                if (!language.equals(current.getLanguage())) {
                    saveLocale(language);
                    loadLocale();
                }
                btValidate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(radio_french.isChecked()){
                            changeLang("fr");
                            saveLocale("fr");
                        } else if (radio_english.isChecked()){
                            changeLang("en");
                            saveLocale("en");
                        }
                        dialogBuilder.dismiss();
                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
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



    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = this.getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        this.getBaseContext().getResources().updateConfiguration(config, this.getBaseContext().getResources().getDisplayMetrics());
        this.recreate();

    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = this.getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }
}


