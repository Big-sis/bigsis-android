package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.adapter.ChooseGroupAdapter;
import fr.bigsis.android.entity.OrganismEntity;

public class ChooseGroupActivity extends AppCompatActivity {

    ChooseGroupAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button btFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group);
        btFinish = findViewById(R.id.btFinishChoice);
        ButterKnife.bind(this);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Intent iin = getIntent();
        Bundle extras = iin.getExtras();
        String organism = extras.getString("ORGANISM");
        RecyclerView mRecyclerRequest = findViewById(R.id.rvChooseCampus);

        Query query = FirebaseFirestore.getInstance()
                .collection(organism).document("AllCampus").collection("AllCampus").orderBy("groupName", Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();
        FirestorePagingOptions<OrganismEntity> options = new FirestorePagingOptions.Builder<OrganismEntity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, OrganismEntity.class)
                .build();
        ChooseGroupAdapter adapterRequest = new ChooseGroupAdapter(options, ChooseGroupActivity.this, btFinish, organism);
        mRecyclerRequest.setLayoutManager(new LinearLayoutManager(ChooseGroupActivity.this));
        mRecyclerRequest.setAdapter(adapterRequest);
    }
}
