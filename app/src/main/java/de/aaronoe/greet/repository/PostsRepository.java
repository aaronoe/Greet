package de.aaronoe.greet.repository;

import android.database.Cursor;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

import de.aaronoe.greet.GreetApplication;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.db.PostsContract;
import de.aaronoe.greet.utils.WidgetPrefs_;

import static de.aaronoe.greet.utils.DbUtils.getContentValuesForPost;

@EBean
public class PostsRepository {

    @Pref
    WidgetPrefs_ mWidgetPrefs;

    @App
    GreetApplication greetApplication;

    public void updatePosts(List<Post> postList) {

        greetApplication.getContentResolver().delete(PostsContract.PostEntry.CONTENT_URI, null, null);

        for (Post post : postList) {
            greetApplication.getContentResolver().insert(PostsContract.PostEntry.CONTENT_URI, getContentValuesForPost(post));
        }

    }

    public String getWidgetGroupName() {
        return mWidgetPrefs.groupName().get();
    }

    /**
     * Since this method is only going to be called from the Widget's RemoteViews service it's going to be non blocking anyways
     * @return Cursor containing any posts
     */
    public Cursor getPostsCursor() {
        return greetApplication.getContentResolver().query(
                PostsContract.PostEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

}
