package de.aaronoe.greet.repository.db;


import android.net.Uri;

public class PostsContract {

    static final String CONTENT_AUTHORITY = "de.aaronoe.greet";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_WIDGET = "widget";



    public static final class PostEntry {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WIDGET).build();

        static final String TABLE_NAME = "WIDGET_POSTS";

        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_TIMESTAMP = "post_timestamp";
        public static final String COLUMN_IMAGE_URL = "post_image_url";
        public static final String COLUMN_POST_TEXT = "post_text";
        public static final String COLUMN_NUMBER_COMMENTS = "post_number_comments";
        public static final String COLUMN_AUTHOR_ID = "post_author_id";
        public static final String COLUMN_AUTHOR_NAME = "post_author_name";
        public static final String COLUMN_AUTHOR_PICTURE_URL = "post_author_picture_url";
        public static final String COLUMN_AUTHOR_EMAIL = "post_author_email";

    }

}
