package de.aaronoe.greet.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.repository.PostsRepository;
import de.aaronoe.greet.repository.PostsRepository_;
import de.aaronoe.greet.ui.groupdetail.GroupHostActivity_;
import de.aaronoe.greet.ui.main.MainActivity;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link GroupWidgetConfigureActivity GroupWidgetConfigureActivity}
 */
public class GroupWidgetProvider extends AppWidgetProvider {

    static PostsRepository mRepository;
    static Group mGroup;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.group_widget);
        String groupName = mRepository.getWidgetGroupName();
        if (groupName != null) {
            views.setTextViewText(R.id.group_name_tv, mRepository.getWidgetGroupName());

            Intent appIntent = GroupHostActivity_.intent(context).mGroup(mGroup).get();

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(appIntent);

            PendingIntent appPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_layout_main, appPendingIntent);
        } else {
            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_layout_main, appPendingIntent);
        }

        Intent remoteAdapterIntent = new Intent(context, PostWidgetRemoteService.class);
        views.setRemoteAdapter(R.id.widget_posts_list, remoteAdapterIntent);
        views.setEmptyView(R.id.widget_posts_list, R.id.empty_view);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mRepository = PostsRepository_.getInstance_(context);
        mGroup = new Group(mRepository.getWidgetGroupName());
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            GroupWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

