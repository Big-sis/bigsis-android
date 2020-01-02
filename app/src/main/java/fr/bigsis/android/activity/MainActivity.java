package fr.bigsis.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import fr.bigsis.android.R;

public class MainActivity extends BigsisActivity {

    Button btSignIn, btSignUp;
    FirebaseAuth mFirebaseAuth;
    private ProgressBar mProgressBarSign;
    private ImageButton changeToEn;
    private ImageButton changeToFr;
    private Locale myLocale;
    private Locale current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btSignUp = findViewById(R.id.btSignUp);
        mProgressBarSign = findViewById(R.id.progressBarSign);
        changeToFr = findViewById(R.id.changeToFr);
        changeToEn = findViewById(R.id.changeToEn);

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        changeToFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLang("fr");
                saveLocale("fr");
            }
        });

        changeToEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLang("en");
                saveLocale("en");
            }
        });

        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString("Language", "");
        System.out.println(language);

        current = getResources().getConfiguration().locale;
        System.out.println(current.getLanguage());

        if (!language.equals(current.getLanguage())) {
            saveLocale(language);
            loadLocale();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();

        btSignIn = findViewById(R.id.btSignIn);
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBarSign.setVisibility(View.VISIBLE);
                if (mFirebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                } else if (mFirebaseAuth.getCurrentUser() != null) {
                    if (!mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    } else {
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    }
                }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProgressBarSign.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
