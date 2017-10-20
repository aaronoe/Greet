package de.aaronoe.greet.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.greet.LoginActivity;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.repository.FireStore;
import de.aaronoe.greet.ui.main.GroupAdapter;
import de.aaronoe.greet.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity {

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
        mAdapter = new GroupAdapter();
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

        /*
        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.getPhotoUrl() != null) {
                    User us = new User(user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail());
                    Post pst = new Post(us, "https://developer.android.com/images/systrace/frame-unselected.png", "I love boder");
                    FireStore.postToGroup(FirebaseFirestore.getInstance(), pst);
                }
            }
        }); */
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

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }
}
