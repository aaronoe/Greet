package de.aaronoe.greet.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.FireStore;
import de.aaronoe.greet.ui.groupdetail.GroupHostActivity_;
import de.aaronoe.greet.ui.main.MainActivity;
import de.aaronoe.greet.ui.postdetail.PostDetailActivity_;

import static android.support.v4.app.RemoteInput.*;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String DEFAULT_NOTIFICATION_CHANNEL = "DEFAULT_NOTIFICATION_CHANNEL";
    public static final String KEY_TEXT_REPLY = "key_text_reply";


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            processMessage(remoteMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private NotificationCompat.Action getReplyAction(Group group, Post post) {

        String replyLabel = getResources().getString(R.string.leave_reply);
        android.support.v4.app.RemoteInput remoteInput = new Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        Intent intent = NewCommentIntentService_.intent(this).addCommentToPost(group, post).get();
        //Intent intent = AddCommentIntentService.intent(this).addCommentToPost(group, post).get();
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        return new NotificationCompat.Action.Builder(R.drawable.ic_send_24dp,
                getString(R.string.action_reply), pendingIntent)
                .addRemoteInput(remoteInput)
                .build();
    }

    private void sendNotification(final Group group,
                                  final Post post,
                                  final String commentAuthor,
                                  final String commentText,
                                  String commentAuthorProfile) {
        Intent mainActivtiyIntent = new Intent(this, MainActivity.class);
        Intent groupDetailIntent = GroupHostActivity_.intent(this).mGroup(group).get();
        Intent postDetailIntent = PostDetailActivity_.intent(this).mGroup(group).mPost(post).get();

        mainActivtiyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                        .addNextIntent(mainActivtiyIntent)
                        .addNextIntent(groupDetailIntent)
                        .addNextIntent(postDetailIntent)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        Glide.with(this).asBitmap().load(commentAuthorProfile).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(MyFirebaseMessagingService.this, DEFAULT_NOTIFICATION_CHANNEL)
                                .setSmallIcon(R.drawable.ic_logo_24dp)
                                .setLargeIcon(resource)
                                .setContentTitle(getString(R.string.notification_new_comment_title))
                                .setContentText(getString(R.string.new_comment_notification_body, commentAuthor, commentText))
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .addAction(getReplyAction(group, post))
                                .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                }

            }
        });

    }

    private void processMessage(RemoteMessage message) {
        Map<String, String> messageData = message.getData();

        if (messageData == null) {
            return;
        }

        final String groupId = messageData.get("groupId");
        final String postId = messageData.get("postId");
        final String commentId = messageData.get("commentId");
        final String commentAuthor = messageData.get("commentAuthor");
        final String commentText = messageData.get("commentText");
        final String commentAuthorProfile = messageData.get("commentAuthorProfile");

        if (groupId == null || postId == null || commentId == null
                || commentAuthor == null || commentText == null || commentAuthorProfile == null) return;

        FireStore.getGroupReference(FirebaseFirestore.getInstance(), groupId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isComplete() || !task.isSuccessful()) return;
                final Group group = task.getResult().toObject(Group.class);

                FireStore.getPostReference(FirebaseFirestore.getInstance(), group, postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isComplete() || !task.isSuccessful()) return;
                        Post post = task.getResult().toObject(Post.class);

                        sendNotification(group, post, commentAuthor, commentText, commentAuthorProfile);
                    }
                });

            }
        });

    }
}