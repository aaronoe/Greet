package de.aaronoe.greet.ui.groupdetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.PostsRepository;
import de.aaronoe.greet.ui.newpost.NewPostActivity_;
import de.aaronoe.greet.ui.postdetail.PostDetailActivity_;

@OptionsMenu(R.menu.group_menu)
@EFragment(R.layout.group_detail)
public class GroupFragment extends android.support.v4.app.Fragment implements PostAdapter.PostClickCallback {

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

    @Bean
    PostsRepository mRepository;

    @FragmentArg
    @InstanceState
    Group mGroup;

    @FragmentArg
    @InstanceState
    boolean isTabletLayout = false;

    private MutableLiveData<List<Post>> mFirebaseGroup;

    private static final String TAG = "GroupFragment";

    @AfterViews
    void init() {

        if (mGroup != null) {

            if (!isTabletLayout) {
                if ((getActivity()) != null) {
                    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
                }
                setHasOptionsMenu(true);

                mToolbar.setTitle(mGroup.getGroupName());
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
            } else {
                mToolbar.setVisibility(View.GONE);
            }

            GroupViewModel mGroupViewModel = ViewModelProviders.of(getActivity()).get(GroupViewModel.class);
            mFirebaseGroup = mGroupViewModel.getLivePosts(mGroup);

            mPostAdapter = new PostAdapter(getContext(), this);
            StaggeredGridLayoutManager gridLayoutManager =
                    new StaggeredGridLayoutManager(getResources().getInteger(R.integer.post_grid_span_count), StaggeredGridLayoutManager.VERTICAL);
            mPostsRv.setLayoutManager(gridLayoutManager);
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

    @Override
    public void onPostClick(Post post, CardView view, ImageView postIv) {
        Intent intent = PostDetailActivity_.intent(this).mPost(post).mGroup(mGroup).get();

        Pair<View, String> container = new Pair<>((View) view, getString(R.string.transition_author_iv));
        Pair<View, String> image = new Pair<>((View) postIv, getString(R.string.transition_key_post_image));

        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), container, image);
        getActivity().startActivity(intent, options.toBundle());
    }
    
    @OptionsItem(R.id.menu_show_widget)
    void onClickShowInWidget() {
        new LovelyStandardDialog(getContext())
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_comment_white_24dp)
                .setButtonsColorRes(R.color.colorAccent)
                .setTitle(R.string.group_widet_add_title)
                .setMessage(R.string.group_widget_add_message)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.posts_shown_in_widget, Snackbar.LENGTH_SHORT).show();
                        }
                        setWidgetInPrefs();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void setWidgetInPrefs() {
        mRepository.selectGroupForWidget(mGroup);
    }

    @Click(R.id.fab_add)
    void addPost() {
        NewPostActivity_
                .intent(this)
                .mGroup(mGroup)
                .start();
    }

    private void updateUi(List<Post> posts) {
        if (posts == null || posts.size() == 0) {
            mEmptyMessageContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mPostsRv.setVisibility(View.GONE);
        } else {
            if (mEmptyMessageContainer == null) return;
            mEmptyMessageContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mPostsRv.setVisibility(View.VISIBLE);
            mPostAdapter.setPostList(posts);
        }
    }


}
