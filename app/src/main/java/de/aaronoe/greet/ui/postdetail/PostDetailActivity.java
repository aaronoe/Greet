package de.aaronoe.greet.ui.postdetail;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Comment;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.repository.FireStore;
import de.aaronoe.greet.utils.DateUtils;
import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("Registered")
@EActivity(R.layout.activity_post_detail)
public class PostDetailActivity extends AppCompatActivity {

    @ViewById(R.id.post_author_iv)
    CircleImageView mAuthorIv;
    @ViewById(R.id.author_name_tv)
    TextView mAuthorNameTv;
    @ViewById(R.id.post_date_tv)
    TextView mPostDateTv;
    @ViewById(R.id.post_text)
    TextView mPostTextView;
    @ViewById(R.id.post_image_iv)
    ImageView mPostImageView;
    @ViewById(R.id.comments_pb)
    ProgressBar mProgressBar;
    @ViewById(R.id.comments_rv)
    RecyclerView mCommentsRv;
    @ViewById(R.id.comments_empty_tv)
    TextView mEmptyCommentsTv;

    @InstanceState
    @Extra
    Post mPost;

    @InstanceState
    @Extra
    Group mGroup;

    boolean firstImageLoaded = false;
    private PostDetailViewModel mPostViewModel;
    private CommentAdapter mCommentAdapter;

    @AfterViews
    void init() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (mPost != null) {
            mAuthorNameTv.setText(mPost.getAuthor().getProfileName());
            mPostDateTv.setText(DateUtils.getGroupItemString(this, mPost.getTimestamp()));

            postponeEnterTransition();
            Glide.with(this)
                    .load(mPost.getAuthor().getPictureUrl())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            mAuthorIv.setImageDrawable(resource);
                            // We need this because we have a race condition of loading two images asynchronously
                            // and the call to start the transition should only be made after both are loaded
                            if (firstImageLoaded) {
                                supportStartPostponedEnterTransition();
                            } else {
                                firstImageLoaded = true;
                            }
                        }
                    });

            mPostTextView.setText(mPost.getPostText());

            if (mPost.getPostImageUrl() == null) {
                mPostImageView.setVisibility(View.GONE);
                supportStartPostponedEnterTransition();
            } else {
                mPostImageView.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(mPost.getPostImageUrl())
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                mPostImageView.setImageDrawable(resource);
                                if (firstImageLoaded) {
                                    supportStartPostponedEnterTransition();
                                } else {
                                    firstImageLoaded = true;
                                }
                            }
                        });
            }

            mCommentAdapter = new CommentAdapter(this);
            mCommentsRv.setAdapter(mCommentAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mCommentsRv.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                    layoutManager.getOrientation());
            mCommentsRv.addItemDecoration(dividerItemDecoration);

            mPostViewModel = ViewModelProviders.of(this).get(PostDetailViewModel.class);

            mPostViewModel.getCommentsLiveData(mGroup, mPost).observe(this, new Observer<List<Comment>>() {
                @Override
                public void onChanged(@Nullable List<Comment> comments) {
                    updateUi(comments);
                }
            });

        }
    }

    private void updateUi(List<Comment> comments) {
        if (comments == null || comments.size() == 0) {
            // show error view
            mCommentsRv.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mEmptyCommentsTv.setVisibility(View.VISIBLE);
        } else {
            mCommentsRv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mEmptyCommentsTv.setVisibility(View.GONE);
            mCommentAdapter.setComments(comments);
        }
    }

    @Click(R.id.fab_add)
    void onClickAddComment() {
        new LovelyTextInputDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_comment_white_24dp)
                .setTitle(getString(R.string.comment_on_post_placeholder, mPost.getAuthor().getProfileName()))
                .setMessage(R.string.comment_dialog_message)
                .setInputFilter(R.string.invalid_comment, new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return !text.isEmpty();
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        FireStore.addCommentToPost(
                                FirebaseFirestore.getInstance(),
                                mGroup,
                                mPost, new Comment(text, new User(FirebaseAuth.getInstance().getCurrentUser()))
                        );
                        Toast.makeText(PostDetailActivity.this, R.string.comment_added_toast, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
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


}
