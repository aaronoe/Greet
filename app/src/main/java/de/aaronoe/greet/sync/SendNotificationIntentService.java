package de.aaronoe.greet.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;

import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;

@EIntentService
public class SendNotificationIntentService extends IntentService {

    private static final String TAG = "SendNotificationIntentS";

    public SendNotificationIntentService() {
        super(TAG);
    }

    @ServiceAction
    public void sendNotification(final Group group,
                                 final Post post,
                                 final String commentAuthor,
                                 final String commentText,
                                 String commentAuthorProfile) {

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
