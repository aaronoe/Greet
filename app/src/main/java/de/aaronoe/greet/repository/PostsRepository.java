package de.aaronoe.greet.repository;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import java.util.List;
import java.util.Objects;

import de.aaronoe.greet.GreetApplication;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.db.PostsContract;
import de.aaronoe.greet.widget.GroupWidgetProvider;

import static de.aaronoe.greet.utils.DbUtils.getContentValuesForPost;

@EBean
public class PostsRepository {

    private static final String WIDGET_GROUP_PREFS = "WIDGET_GROUP_PREFS";
    public static final String KEY_GROUP_ID = "KEY_GROUP_ID";
    public static final String UNDEFINED = "UNDEFINED";
    public static final String KEY_GROUP_NAME = "KEY_GROUP_NAME";
    @App
    GreetApplication greetApplication;

    private SharedPreferences mSharedPrefs;

    @AfterInject
    void init() {
        mSharedPrefs = greetApplication.getSharedPreferences(WIDGET_GROUP_PREFS, Context.MODE_PRIVATE);
    }

    public void updatePosts(List<Post> postList) {

        greetApplication.getContentResolver().delete(PostsContract.PostEntry.CONTENT_URI, null, null);

        for (Post post : postList) {
            greetApplication.getContentResolver().insert(PostsContract.PostEntry.CONTENT_URI, getContentValuesForPost(post));
        }

        updateAppWidget();

    }

    private void updateAppWidget() {
        Intent intent = new Intent(greetApplication, GroupWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(greetApplication);
        ComponentName componentName = new ComponentName(greetApplication, GroupWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetManager.getAppWidgetIds(componentName));

        greetApplication.sendBroadcast(intent);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.widget_posts_list);
    }

    public Group getWidgetGroup() {
        String groupId = mSharedPrefs.getString(KEY_GROUP_ID, UNDEFINED);
        String groupName = mSharedPrefs.getString(KEY_GROUP_NAME, UNDEFINED);
        if (groupId.equals(UNDEFINED) || groupName.equals(UNDEFINED)) return null;
        return new Group(groupId, groupName);
    }

    public void selectGroupForWidget(Group group) {
        mSharedPrefs
                .edit()
                .putString(KEY_GROUP_ID, group.getGroupId())
                .putString(KEY_GROUP_NAME, group.getGroupName())
                .apply();

        updateAppWidget();
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
