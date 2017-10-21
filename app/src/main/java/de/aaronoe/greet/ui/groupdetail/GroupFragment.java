package de.aaronoe.greet.ui.groupdetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.repository.FireStore;

@EFragment(R.layout.group_detail)
public class GroupFragment extends android.support.v4.app.Fragment {

    @ViewById(R.id.heroImageView)
    ImageView mHeroImageView;
    @ViewById(R.id.app_bar)
    Toolbar mToolbar;
    @ViewById(R.id.post_list_pb)
    ProgressBar mProgressBar;
    @ViewById(R.id.empty_message_container)
    ConstraintLayout mEmptyMessageContainer;
    @ViewById(R.id.posts_rv)
    RecyclerView mPostsRv;
    @ViewById(R.id.fab_add)
    FloatingActionButton mFabAdd;

    private PostAdapter mPostAdapter;

    @FragmentArg
    @InstanceState
    Group mGroup;

    private GroupViewModel mGroupViewModel;
    private MutableLiveData<List<Post>> mFirebaseGroup;

    private static final String TAG = "GroupFragment";

    @AfterViews
    void init() {

        if (mGroup != null) {

            mToolbar.setTitle(mGroup.getGroupName());
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

            mGroupViewModel = ViewModelProviders.of(getActivity()).get(GroupViewModel.class);
            mFirebaseGroup = mGroupViewModel.getLivePosts(mGroup);

            mPostAdapter = new PostAdapter(getContext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mPostsRv.setLayoutManager(linearLayoutManager);
            mPostsRv.setAdapter(mPostAdapter);

            subscribeToChanges();
        }
    }

    private void subscribeToChanges() {
        mFirebaseGroup.observe(getActivity(), new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                Log.e(TAG, "onChanged: " + posts );
                updateUi(posts);
            }
        });
    }

    @Click(R.id.fab_add)
    void addPost() {
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        User user = new User(mauth.getCurrentUser());
        FireStore.postToGroup(FirebaseFirestore.getInstance(), mGroup.getGroupId(), new Post(user, null, "Bodertest"));
    }

    private void updateUi(List<Post> posts) {
        if (posts == null || posts.size() == 0) {
            mEmptyMessageContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mPostsRv.setVisibility(View.GONE);
        } else {
            mEmptyMessageContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mPostsRv.setVisibility(View.VISIBLE);
            mPostAdapter.setPostList(posts);
        }
    }


}
