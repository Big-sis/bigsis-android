package fr.bigsis.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;

import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.adapter.ChooseStaffAdapter;
import fr.bigsis.android.entity.UserEntity;
import fr.bigsis.android.fragment.AddEventFragment;

public class ChooseUserActivity extends BigsisActivity implements AddEventFragment.OnFragmentInteractionListener {
    @BindView(R.id.rv_staff)
    RecyclerView mRecycler;
    @BindView(R.id.swipe_refresh_layout_staff)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private FirebaseFirestore mFirestore;
    Button btFinish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        ButterKnife.bind(this);
        mFirestore = FirebaseFirestore.getInstance();
        setUpAdapter();
    }

    private void setUpAdapter() {
        btFinish = findViewById(R.id.btFinish);
        Intent i = getIntent();
        String staffM = i.getStringExtra("STAFF");
        if (staffM != null) {
            PagedList.Config config = new PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPrefetchDistance(10)
                    .setPageSize(20)
                    .build();
            Query query = FirebaseFirestore.getInstance().collection("users");
            FirestorePagingOptions<UserEntity> options = new FirestorePagingOptions.Builder<UserEntity>()
                    .setLifecycleOwner(this)
                    .setQuery(query, config, UserEntity.class)
                    .build();

            ChooseStaffAdapter adapter = new ChooseStaffAdapter(options, this, mSwipeRefreshLayout, btFinish);

            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRecycler.setAdapter(adapter);
btFinish.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        adapter.onClickButton();
    }
});
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    adapter.refresh();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onFragmentInteractionEvent() {

    }
}
