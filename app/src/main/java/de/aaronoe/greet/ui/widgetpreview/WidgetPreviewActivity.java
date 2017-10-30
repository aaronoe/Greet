package de.aaronoe.greet.ui.widgetpreview;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.PostsRepository;
import de.aaronoe.greet.repository.db.PostsContract;
import de.aaronoe.greet.ui.postdetail.PostDetailActivity_;

@SuppressLint("Registered")
@EActivity(R.layout.activity_widget_preview)
public class WidgetPreviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, PostCursorAdapter.PostClickCallback {

    @ViewById(R.id.widget_preview_empty_tv)
    TextView mEmptyTv;
    @ViewById(R.id.widget_preview_pb)
    ProgressBar mProgressBar;
    @ViewById(R.id.widget_preview_rv)
    RecyclerView mPostsRv;

    @Bean
    PostsRepository mRepo;

    private PostCursorAdapter mPostAdapter;

    @AfterViews
    void init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mPostAdapter = new PostCursorAdapter(this, this);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(getResources().getInteger(R.integer.post_grid_span_count), StaggeredGridLayoutManager.VERTICAL);
        mPostsRv.setLayoutManager(gridLayoutManager);
        mPostsRv.setAdapter(mPostAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, PostsContract.PostEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() == 0) {
            mEmptyTv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mPostsRv.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyTv.setVisibility(View.GONE);
            mPostsRv.setVisibility(View.VISIBLE);
            mPostAdapter.setPostList(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onPostClick(Post post, CardView view, ImageView iv) {
        Intent intent = PostDetailActivity_.intent(this).mPost(post).mGroup(mRepo.getWidgetGroup()).get();

        Pair<View, String> container = new Pair<>((View) view, getString(R.string.transition_author_iv));
        Pair<View, String> image = new Pair<>((View) iv, getString(R.string.transition_key_post_image));

        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, container, image);
        startActivity(intent, options.toBundle());
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
