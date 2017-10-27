package de.aaronoe.greet.widget;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

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
        mGroup = new Group(mRepo.getWidgetGroupName());
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

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.post_item);

            views.setTextViewText(R.id.author_name_tv, post.getAuthor().getProfileName());
            views.setTextViewText(R.id.post_text, post.getPostText());
            views.setTextViewText(R.id.post_date_tv, DateUtils.getGroupItemString(getApplicationContext(), post.getTimestamp()));
            views.setImageViewUri(R.id.post_author_iv, Uri.parse(post.getAuthor().getPictureUrl()));

            Intent fillIntent = PostDetailActivity_.intent(getApplicationContext()).mGroup(mGroup).mPost(post).get();
            views.setOnClickFillInIntent(R.id.post_item_card, fillIntent);

            if (post.getPostImageUrl() == null) {
                views.setViewVisibility(R.id.post_image_iv, View.GONE);
            } else {
                views.setViewVisibility(R.id.post_image_iv, View.VISIBLE);
                views.setImageViewUri(R.id.post_image_iv, Uri.parse(post.getPostImageUrl()));
            }

            return null;
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
