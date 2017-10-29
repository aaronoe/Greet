package de.aaronoe.greet.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.greet.LoginActivity;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.repository.FireStore;
import de.aaronoe.greet.repository.PostsRepository;
import de.aaronoe.greet.repository.PostsRepository_;
import de.aaronoe.greet.ui.groupdetail.GroupFragment;
import de.aaronoe.greet.ui.groupdetail.GroupFragment_;
import de.aaronoe.greet.ui.groupdetail.GroupHostActivity_;
import de.aaronoe.greet.ui.search.SearchActivity_;

public class MainActivity extends AppCompatActivity implements GroupAdapter.GroupClickCallback {

    public static final String ANDROID_APPWIDGET_ACTION_APPWIDGET_CONFIGURE = "android.appwidget.action.APPWIDGET_CONFIGURE";
    private PostsRepository mRepository;
    private User mUser;
    private GroupAdapter mAdapter;
    private MutableLiveData<List<Group>> mLiveGroups;
    private boolean isConfigureWidgetScreen = false;
    private boolean isTabletLayout = false;

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

        isTabletLayout = getResources().getBoolean(R.bool.isTabletLayout);
        if (isTabletLayout) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_pane, TabletEmptyFragment_.builder().build())
                    .commit();
        }

        isConfigureWidgetScreen = Objects.equals(getIntent().getAction(), ANDROID_APPWIDGET_ACTION_APPWIDGET_CONFIGURE);

        if (isConfigureWidgetScreen) {

            // If the user does not select a widget and presses the back button, the widget creation is cancelled
            setResult(RESULT_CANCELED);

            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_comment_white_24dp)
                    .setButtonsColorRes(R.color.colorAccent)
                    .setTitle(R.string.select_widget_group)
                    .setMessage(R.string.select_group_for_widget_long)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }

        mRepository = PostsRepository_.getInstance_(getApplicationContext());
        ButterKnife.bind(this);
        mAdapter = new GroupAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mGroupsRv.setAdapter(mAdapter);
        mGroupsRv.setLayoutManager(mLayoutManager);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            goToLogin();
        } else {
            mUser = new User(mAuth.getCurrentUser());
            subscribeToUserGroups();
        }
    }

    private void subscribeToUserGroups() {
        MainViewModel mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mLiveGroups = mViewModel.getUserGroups(mUser);
        mLiveGroups.observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable List<Group> groups) {
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
                    onClickCreateGroup();
                }
            });
            mJoinGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchActivity_.intent(MainActivity.this).mExistingGroups(mLiveGroups.getValue()).start();
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
                SearchActivity_.intent(this).mExistingGroups(mLiveGroups.getValue()).start();
                break;
            case R.id.menu_create:
                onClickCreateGroup();
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
    public void onGroupClick(final Group group) {
        if (isConfigureWidgetScreen) {
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_comment_white_24dp)
                    .setButtonsColorRes(R.color.colorAccent)
                    .setTitle(R.string.confirm_selection)
                    .setMessage(getString(R.string.confirm_widget_dialog_message, group.getGroupName()))
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.widget_posts_confirm_dialog, group.getGroupName()),
                                    Toast.LENGTH_SHORT).show();
                            mRepository.selectGroupForWidget(group);
                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return;
        }
        if (isTabletLayout) {
            GroupFragment groupFragment =  GroupFragment_.builder().mGroup(group).isTabletLayout(true).build();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_pane, groupFragment)
                    .commit();
            return;
        }

        GroupHostActivity_.intent(this).mGroup(group).start();
    }

    private void onClickCreateGroup() {
        new LovelyTextInputDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_group_add_white_24dp)
                .setTitle(getString(R.string.create_group_button))
                .setMessage(R.string.create_group_message)
                .setInputFilter("Invalid Name", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return !text.isEmpty();
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        Toast.makeText(MainActivity.this, getString(R.string.creating_group_toast, text), Toast.LENGTH_SHORT).show();
                        FireStore.createGroup(MainActivity.this, FirebaseFirestore.getInstance(), mUser, new Group(text));
                    }
                })
                .show();
    }
}
