package de.aaronoe.greet.sync;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.ui.groupdetail.GroupHostActivity_;
import de.aaronoe.greet.ui.main.MainActivity;
import de.aaronoe.greet.ui.postdetail.PostDetailActivity_;

import static de.aaronoe.greet.sync.MyFirebaseMessagingService.KEY_TEXT_REPLY;

@SuppressLint("Registered")
@EIntentService
public class SendNotificationIntentService extends IntentService {

    private static final String TAG = "SendNotificationIntentS";
    private static final String DEFAULT_NOTIFICATION_CHANNEL = "DEFAULT_NOTIFICATION_CHANNEL";


    public SendNotificationIntentService() {
        super(TAG);
    }

    @ServiceAction
    public void sendNotification(final Group group,
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


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(SendNotificationIntentService.this, DEFAULT_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_logo_24dp)
                        .setLargeIcon(getCircleBitmap(getCircleBitmap(getBitmapFromURL(commentAuthorProfile))))
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

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    private NotificationCompat.Action getReplyAction(Group group, Post post) {

        String replyLabel = getResources().getString(R.string.leave_reply);
        android.support.v4.app.RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
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

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
