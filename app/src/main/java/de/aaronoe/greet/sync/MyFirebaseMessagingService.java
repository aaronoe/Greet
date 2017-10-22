package de.aaronoe.greet.sync;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.FireStore;

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

                        //sendNotification(group, post, commentAuthor, commentText, commentAuthorProfile);
                        SendNotificationIntentService_.intent(MyFirebaseMessagingService.this)
                                .sendNotification(group, post, commentAuthor, commentText, commentAuthorProfile).start();
                    }
                });

            }
        });

    }
}