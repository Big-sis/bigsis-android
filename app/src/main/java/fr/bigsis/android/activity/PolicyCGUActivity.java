package fr.bigsis.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import fr.bigsis.android.R;
import fr.bigsis.android.helpers.KeyboardHelper;

public class PolicyCGUActivity extends BigsisActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_cgu);
        setToolBar();
        transitionContainer = findViewById(R.id.toolBarCGU);
        tvTitleToolbar = transitionContainer.findViewById(R.id.tvTitleToolbar);
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        String cgu = extras.getString("CGU");
        String policy = extras.getString("POLICY");
        if (cgu != null) {
            tvTitleToolbar.setText("Conditions générales d'utilisation");
        }
        if (policy != null) {
            tvTitleToolbar.setText("Politique de confidentialité");
        }
    }

    private void setToolBar() {
        transitionContainer = findViewById(R.id.toolBarCGU);
        transitionContainer.setBackground(getDrawable(R.drawable.gradient));
        imBt_ic_back_frag = transitionContainer.findViewById(R.id.imBt_ic_back_frag);
            imBt_ic_back_frag.setVisibility(View.VISIBLE);
            imBt_ic_back_frag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
