package de.aaronoe.greet.utils;


import android.content.ContentValues;
import android.database.Cursor;

import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.model.User;
import de.aaronoe.greet.repository.db.PostsContract;

public class DbUtils {

    public static ContentValues getContentValuesForPost(Post post) {
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

    /**
     * Utiliy method to get a {@link User} object from a Cursor given the position in the cursor
     * @param cursor cursor for the {@link de.aaronoe.greet.repository.db.PostsContract.PostEntry} table
     * @param position in the cursor, if the position does not exist, the method returns null
     * @return the {@link User} object if a user exists at that position in the cursor, null otherwise
     */
    public static Post getPostFromCursor(Cursor cursor, int position) {
        if (cursor.moveToPosition(position)) {
            Post post = new Post();
            post.setId(cursor.getString(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_POST_ID)));
            post.setPostText(cursor.getString(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_POST_TEXT)));
            post.setPostImageUrl(cursor.getString(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_IMAGE_URL)));
            post.setTimestamp(cursor.getLong(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_TIMESTAMP)));
            post.setNumberOfComments(cursor.getInt(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_NUMBER_COMMENTS)));
            post.setAuthor(new User(
                    cursor.getString(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_AUTHOR_ID)),
                    cursor.getString(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_AUTHOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_AUTHOR_PICTURE_URL)),
                    cursor.getString(cursor.getColumnIndex(PostsContract.PostEntry.COLUMN_AUTHOR_EMAIL))

            ));
            return post;
        } else {
            return null;
        }
    }

}
