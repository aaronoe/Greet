package de.aaronoe.greet.ui.search;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.ui.main.GroupAdapter;

@SuppressLint("Registered")
@EActivity(R.layout.activity_search)
public class SearchActivity extends AppCompatActivity implements GroupAdapter.GroupClickCallback {

    private Timer timer = new Timer();
    private final long DELAY = 500; // milliseconds
    private SearchViewModel mViewModel;

    @ViewById(R.id.search_empty_message_container)
    ConstraintLayout mEmptyContainer;
    @ViewById(R.id.group_list_pb)
    ProgressBar mProgressBar;
    @ViewById(R.id.groups_rv)
    RecyclerView mGroupsRv;

    private GroupAdapter mGroupAdapter;
    private boolean mFirstLoad = true;

    @AfterViews
    void init() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mGroupAdapter = new GroupAdapter(this);
        mGroupsRv.setAdapter(mGroupAdapter);
        mGroupsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        mViewModel.getGroups(null).observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable List<Group> groups) {
                updateUi(groups);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
    }

    @Override
    public void onGroupClick(Group group) {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.searchbar, menu);

        final MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setMaxWidth(100000);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                searchItem.collapseActionView();
                mViewModel.getGroups(query);
                if (mFirstLoad) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mFirstLoad = false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                timer.cancel();
                timer = new Timer();
                final String text = newText;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (text.equals("")) return;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mViewModel.getGroups(text);
                                if (mFirstLoad) {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    mFirstLoad = false;
                                }
                            }
                        });

                    }
                }, DELAY);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
