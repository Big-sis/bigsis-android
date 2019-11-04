package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import fr.bigsis.android.R;
import fr.bigsis.android.fragment.ToolBarFragment;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;

public abstract class BigsisActivity extends AppCompatActivity {

    ConstraintLayout transitionContainer;
    ImageButton imbtSearch, imBtCancel, imBtAdd;
    TextView tvTitleToolbar;

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
                Toast.makeText(this, "ddd", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_events:
                Toast.makeText(this, "ii", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_trip:
                startActivity(new Intent(this, TripListActivity.class));
                return true;
        }
        return false;
    }
}
