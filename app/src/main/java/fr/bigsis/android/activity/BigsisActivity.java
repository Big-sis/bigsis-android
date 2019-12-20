package fr.bigsis.android.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.fragment.ToolBarFragment;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public abstract class BigsisActivity extends AppCompatActivity {

    public ConstraintLayout transitionContainer;
    public ImageButton imbtSearch, imBtCancel, imBtAdd, imBt_notificationv, imgBtBack, imgBtContact, imBt_ic_back_frag;
    public TextView tvTitleToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    protected boolean selectItem(@NonNull MenuItem item, CurvedBottomNavigationView
            curvedBottomNavigationView) {
        switch (item.getItemId()) {
            case R.id.action_user_profile:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            case R.id.action_message:
              //  startActivity(new Intent(this, GroupConversationActivity.class));
                return true;
            case R.id.action_events:
               // startActivity(new Intent(this, EventListActivity.class));
                return true;
            case R.id.action_trip:
                startActivity(new Intent(this, TripListActivity.class));
                return true;
        }
        return false;
    }


    protected void setAppLocal(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }
}
