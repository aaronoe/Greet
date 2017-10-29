package de.aaronoe.greet.ui.search;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.ui.main.GroupAdapter;

@SuppressLint("Registered")
@EActivity(R.layout.activity_search)
@OptionsMenu(R.menu.searchbar)
public class SearchActivity extends AppCompatActivity implements GroupAdapter.GroupClickCallback {

    private SearchViewModel mViewModel;

    @ViewById(R.id.search_empty_message_container)
    ConstraintLayout mEmptyContainer;
    @ViewById(R.id.group_list_pb)
    ProgressBar mProgressBar;
    @ViewById(R.id.groups_rv)
    RecyclerView mGroupsRv;

    @Extra
    List<Group> mExistingGroups;

    private GroupAdapter mGroupAdapter;
    private boolean mFirstLoad = true;

    @AfterViews
    void init() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mGroupAdapter = new GroupAdapter(this, this);
        mGroupsRv.setAdapter(mGroupAdapter);
        mGroupsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        mViewModel.getGroups(null).observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable List<Group> groups) {
                updateUi(processGroups(groups));
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        onClickSearch();
    }

    @Override
    public void onGroupClick(final Group group) {
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_group_add_white_24dp)
                .setTitle(R.string.join_group_title)
                .setMessage(R.string.join_group_message)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SearchActivity.this, getString(R.string.joining_group, group.getGroupName()), Toast.LENGTH_SHORT).show();
                        mViewModel.joinGroup(new User(FirebaseAuth.getInstance().getCurrentUser()), group);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void updateUi(List<Group> groups) {
        if (groups == null || groups.size() == 0) {
            mProgressBar.setVisibility(View.GONE);
            mGroupsRv.setVisibility(View.GONE);
            mEmptyContainer.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyContainer.setVisibility(View.GONE);
            mGroupsRv.setVisibility(View.VISIBLE);
            mGroupAdapter.setGroups(groups);
        }
    }

    /**
     * Not so nice method which is in O(n^2) and filters out already joined groups
     */
    private List<Group> processGroups(List<Group> groups) {
        List<Group> result = new ArrayList<>();
        for (Group usersGroup : groups) {
            boolean userJoinedAlready = false;
            for (Group group : mExistingGroups) {
                if (usersGroup.getGroupId().equals(group.getGroupId())) {
                    userJoinedAlready = true;
                }
            }
            if (!userJoinedAlready) {
                result.add(usersGroup);
            }
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OptionsItem(R.id.menu_search)
    void onClickSearch() {
        new LovelyTextInputDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_comment_white_24dp)
                .setTitle(R.string.search_for_groups)
                .setMessage(R.string.search_groups_message)
                .setInputFilter(R.string.invalid_comment, new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return !text.isEmpty();
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        mViewModel.getGroups(text);
                        if (mFirstLoad) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mFirstLoad = false;
                        }
                    }
                })
                .show();
    }

}
