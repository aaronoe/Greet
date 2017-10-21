package de.aaronoe.greet.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.greet.LoginActivity;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.repository.FireStore;
import de.aaronoe.greet.ui.groupdetail.GroupHostActivity_;
import de.aaronoe.greet.ui.search.SearchActivity_;

public class MainActivity extends AppCompatActivity implements GroupAdapter.GroupClickCallback {

    private FirebaseAuth mAuth;
    private User mUser;
    private MainViewModel mViewModel;
    private static final String TAG = "MainActivity";
    private GroupAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.empty_message_container)
    ConstraintLayout mEmptyMessageContainer;
    @BindView(R.id.join_group_button)
    Button mJoinGroupButton;
    @BindView(R.id.create_group_button)
    Button mCreateGroupButton;
    @BindView(R.id.group_list_pb)
    ProgressBar mProgressBar;
    @BindView(R.id.groups_rv)
    RecyclerView mGroupsRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mAdapter = new GroupAdapter(this);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mGroupsRv.setAdapter(mAdapter);
        mGroupsRv.setLayoutManager(mLayoutManager);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            goToLogin();
        } else {
            mUser = new User(mAuth.getCurrentUser());
            subscribeToUserGroups();
        }
    }

    private void subscribeToUserGroups() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getUserGroups(mUser).observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable List<Group> groups) {
                Log.d(TAG, "onChanged() called with: groups = [" + groups + "]");
                updateUi(groups);
            }
        });
    }

    private void updateUi(List<Group> groups) {
        mEmptyMessageContainer.setVisibility(View.GONE);
        mGroupsRv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        if (groups == null || groups.size() == 0) {
            mEmptyMessageContainer.setVisibility(View.VISIBLE);
            mCreateGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Create group", Toast.LENGTH_SHORT).show();
                    FireStore.createGroup(FirebaseFirestore.getInstance(), mUser, new Group("Boders"));
                }
            });
            mJoinGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Join Group", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mGroupsRv.setVisibility(View.VISIBLE);
            mAdapter.setGroups(groups);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                SearchActivity_.intent(this).start();
                break;
            case R.id.menu_create:
                FireStore.createGroup(FirebaseFirestore.getInstance(), mUser, new Group("Boders"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }

    @Override
    public void onGroupClick(Group group) {
        GroupHostActivity_.intent(this).mGroup(group).start();
    }
}
