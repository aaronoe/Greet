package de.aaronoe.greet.repository.db;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static de.aaronoe.greet.repository.db.PostsContract.PostEntry.TABLE_NAME;

public class PostsContentProvider extends ContentProvider {

    private static final String TAG = "PostsContentProvider";
    public static final int POSTS = 100;
    public static final int POSTS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI
                (PostsContract.CONTENT_AUTHORITY, PostsContract.PATH_WIDGET, POSTS);
        uriMatcher.addURI
                (PostsContract.CONTENT_AUTHORITY, PostsContract.PATH_WIDGET + "/#", POSTS_WITH_ID);

        return uriMatcher;
    }

    private PostDbHelper mPostDbHelper;


    @Override
    public boolean onCreate() {
        mPostDbHelper = new PostDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mPostDbHelper.getWritableDatabase();
        Cursor result;

        switch (sUriMatcher.match(uri)) {
            case POSTS:
                result = db.query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case POSTS_WITH_ID:

                String id = uri.getPathSegments().get(1);

                result = db.query(
                        TABLE_NAME,
                        projection,
                        PostsContract.PostEntry.COLUMN_POST_ID + "=?",
                        new String[]{id},
                        null,
                        null,
                        null);

                break;

            default:
                throw new UnsupportedOperationException("Unsupported Operation for: " + uri);

        }

        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        Log.d(TAG, "insert() called with: uri = [" + uri + "], values = [" + values + "]");

        final SQLiteDatabase db = mPostDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {

            case POSTS:

                long id = db.insert(
                        TABLE_NAME,
                        null,
                        values);


                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PostsContract.PostEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unsupported Operation for: " + uri);

        }

        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numberOfRowsDeleted;

        SQLiteDatabase db = mPostDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case POSTS:
                numberOfRowsDeleted = db.delete(TABLE_NAME, null, null);
                break;

            case POSTS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                numberOfRowsDeleted = db.delete(
                        TABLE_NAME,
                        PostsContract.PostEntry.COLUMN_POST_ID + "=?",
                        new String[]{id});

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (numberOfRowsDeleted != 0) {
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return numberOfRowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
