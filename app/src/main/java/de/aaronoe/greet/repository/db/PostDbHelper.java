package de.aaronoe.greet.repository.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.aaronoe.greet.repository.db.PostsContract.PostEntry;

public class PostDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "widget_posts.db";
    private static final int DATABASE_VERSION = 1;

    public PostDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE =
                "CREATE TABLE " + PostEntry.TABLE_NAME + " (" +
                        PostEntry.COLUMN_POST_ID + " TEXT PRIMARY KEY, " +
                        PostEntry.COLUMN_POST_TEXT + " TEXT NOT NULL, " +
                        PostEntry.COLUMN_IMAGE_URL + " TEXT, " +
                        PostEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                        PostEntry.COLUMN_NUMBER_COMMENTS + " INTEGER NOT NULL, " +
                        PostEntry.COLUMN_AUTHOR_ID + " TEXT NOT NULL, " +
                        PostEntry.COLUMN_AUTHOR_NAME + " TEXT NOT NULL, " +
                        PostEntry.COLUMN_AUTHOR_EMAIL + " TEXT NOT NULL, " +
                        PostEntry.COLUMN_AUTHOR_PICTURE_URL + " TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PostEntry.TABLE_NAME );
        onCreate(db);
    }
}
