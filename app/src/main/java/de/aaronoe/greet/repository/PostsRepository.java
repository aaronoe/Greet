package de.aaronoe.greet.repository;

import android.app.Application;
import android.content.ContentValues;

import java.util.List;

import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.repository.db.PostsContract;

public class PostsRepository {


    public void updatePosts(Application application, List<Post> postList) {

        application.getContentResolver().delete(PostsContract.PostEntry.CONTENT_URI, null, null);

        for (Post post : postList) {
            application.getContentResolver().insert(PostsContract.PostEntry.CONTENT_URI, getContentValuesForPost(post));
        }

    }

    private ContentValues getContentValuesForPost(Post post) {
        ContentValues cv = new ContentValues();
        cv.put(PostsContract.PostEntry.COLUMN_AUTHOR_EMAIL, post.getAuthor().getEmailAdress());
        cv.put(PostsContract.PostEntry.COLUMN_AUTHOR_ID, post.getAuthor().getUserID());
        cv.put(PostsContract.PostEntry.COLUMN_AUTHOR_NAME, post.getAuthor().getProfileName());
        cv.put(PostsContract.PostEntry.COLUMN_AUTHOR_PICTURE_URL, post.getAuthor().getPictureUrl());
        cv.put(PostsContract.PostEntry.COLUMN_POST_ID, post.getId());
        cv.put(PostsContract.PostEntry.COLUMN_POST_TEXT, post.getPostText());
        cv.put(PostsContract.PostEntry.COLUMN_IMAGE_URL, post.getPostImageUrl());
        cv.put(PostsContract.PostEntry.COLUMN_NUMBER_COMMENTS, post.getNumberOfComments());
        cv.put(PostsContract.PostEntry.COLUMN_TIMESTAMP, post.getTimestamp());
        return cv;
    }

}
