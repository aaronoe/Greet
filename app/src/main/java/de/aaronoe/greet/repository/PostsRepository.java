package de.aaronoe.greet.repository;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.aaronoe.greet.GreetApplication;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.db.PostsContract;
import de.aaronoe.greet.sync.WidgetSyncJob;
import de.aaronoe.greet.sync.WidgetUpdatePostsIntentService_;
import de.aaronoe.greet.widget.GroupWidgetProvider;

import static de.aaronoe.greet.utils.DbUtils.getContentValuesForPost;

@EBean
public class PostsRepository {

    private static final String WIDGET_GROUP_PREFS = "WIDGET_GROUP_PREFS";
    private static final String KEY_GROUP_ID = "KEY_GROUP_ID";
    private static final String UNDEFINED = "UNDEFINED";
    private static final String KEY_GROUP_NAME = "KEY_GROUP_NAME";
    private static final String GROUP_POSTS_SYNC_JOB = "group_posts_sync_job";

    private static final int JOB_INTERVAL_MINUTES = 15;
    private static final int JOB_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(JOB_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = JOB_INTERVAL_SECONDS;

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

        WidgetUpdatePostsIntentService_.intent(greetApplication).updatePostsForWidget().start();

        scheduleSyncJob();
    }

    private void scheduleSyncJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(greetApplication));

        Job myJob = dispatcher.newJobBuilder()
                .setService(WidgetSyncJob.class)
                .setTag(GROUP_POSTS_SYNC_JOB)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(
                        JOB_INTERVAL_SECONDS,
                        JOB_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                // Overwrite any existing jobs
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_UNMETERED_NETWORK
                )
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public void refreshWidgetPosts() {
        Group group = getWidgetGroup();
        FireStore.getGroupPostsReference(FirebaseFirestore.getInstance(), group.getGroupId())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Post> postList = new ArrayList<>();
                for (DocumentSnapshot value : task.getResult()) {
                    if (!value.exists()) continue;
                    postList.add(value.toObject(Post.class));
                }
                updatePosts(postList);
            }
        });
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
