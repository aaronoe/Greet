package de.aaronoe.greet.sync;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;

import de.aaronoe.greet.repository.PostsRepository;

@SuppressLint("Registered")
@EIntentService
public class WidgetUpdatePostsIntentService extends IntentService {

    private static final String TAG = "WidgetUpdatePostsIntent";

    @Bean
    PostsRepository mRepo;

    public WidgetUpdatePostsIntentService() {
        super(TAG);
    }

    @ServiceAction
    public void updatePostsForWidget() {
        mRepo.refreshWidgetPosts();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
