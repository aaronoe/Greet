package de.aaronoe.greet.ui.postdetail;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Post;
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

    @Extra
    Post mPost;

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
                            supportStartPostponedEnterTransition();
                        }
                    });

            mPostTextView.setText(mPost.getPostText());

            if (mPost.getPostImageUrl() == null) {
                mPostImageView.setVisibility(View.GONE);
            } else {
                mPostImageView.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(mPost.getPostImageUrl())
                        .into(mPostImageView);
            }
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


}
