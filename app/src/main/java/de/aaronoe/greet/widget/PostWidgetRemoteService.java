package de.aaronoe.greet.widget;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutionException;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.PostsRepository;
import de.aaronoe.greet.repository.PostsRepository_;
import de.aaronoe.greet.ui.postdetail.PostDetailActivity_;
import de.aaronoe.greet.utils.DateUtils;
import de.aaronoe.greet.utils.DbUtils;

public class PostWidgetRemoteService extends RemoteViewsService {

    private PostsRepository mRepo;
    private Group mGroup;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        mRepo = PostsRepository_.getInstance_(getApplicationContext());
        mGroup = mRepo.getWidgetGroup();
        return new PostsRemoteViewsFactory();
    }

    class PostsRemoteViewsFactory implements RemoteViewsFactory {

        private Cursor mPostCursor;

        @Override
        public void onCreate() {
            // no op
        }

        @Override
        public void onDataSetChanged() {
            mPostCursor = mRepo.getPostsCursor();
        }

        @Override
        public void onDestroy() {
            mPostCursor.close();
        }

        @Override
        public int getCount() {
            return mPostCursor == null ? 0 : mPostCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Post post = DbUtils.getPostFromCursor(mPostCursor, position);
            if (post == null) return null;

            final RemoteViews views = new RemoteViews(getPackageName(), R.layout.post_item_widget);

            views.setTextViewText(R.id.author_name_tv, post.getAuthor().getProfileName());
            views.setTextViewText(R.id.post_text, post.getPostText());
            views.setTextViewText(R.id.post_date_tv, DateUtils.getGroupItemString(getApplicationContext(), post.getTimestamp()));
            try {
                Bitmap profileIcon = Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(post.getAuthor().getPictureUrl())
                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
                views.setImageViewBitmap(R.id.post_author_iv, profileIcon);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (post.getPostImageUrl() == null) {
                views.setViewVisibility(R.id.post_image_iv, View.GONE);
            } else {
                views.setViewVisibility(R.id.post_image_iv, View.VISIBLE);

                try {
                    Bitmap bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(post.getPostImageUrl())
                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    views.setImageViewBitmap(R.id.post_image_iv, bitmap);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            Intent fillIntent = PostDetailActivity_.intent(getApplicationContext())
                    .mGroup(mGroup)
                    .mPost(post)
                    .get();

            views.setOnClickFillInIntent(R.id.widget_item_container, fillIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

}
