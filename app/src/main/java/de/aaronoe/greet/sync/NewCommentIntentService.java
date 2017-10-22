package de.aaronoe.greet.sync;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Comment;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.repository.FireStore;

import static de.aaronoe.greet.sync.MyFirebaseMessagingService.KEY_TEXT_REPLY;

@SuppressLint("Registered")
@EIntentService
public class NewCommentIntentService extends IntentService {

    private static final String TAG = "NewCommentIntentService";
    Intent intent;

    public NewCommentIntentService() {
        super(TAG);
    }

    @ServiceAction
    public void addCommentToPost(Group group, Post post) {
        CharSequence com = getMessageText(intent);
        if (com == null) return;
        String comment = com.toString();
        FireStore.addCommentToPost(FirebaseFirestore.getInstance(), group, post, new Comment(comment,
                new User(FirebaseAuth.getInstance().getCurrentUser())));
        sendConfirmNotification();
    }

    private void sendConfirmNotification() {
        Notification repliedNotification =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_logo_24dp)
                        .setContentText(getString(R.string.replied_to_persons_comment))
                        .build();

            // Issue the new notification.

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, repliedNotification);
        }
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        this.intent = intent;
    }
}
