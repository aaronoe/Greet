package de.aaronoe.greet.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Post implements Parcelable {

    private String id;
    private long timestamp;
    private User author;
    private String postImageUrl;
    private String postText;
    private int numberOfComments;

    public Post() {
    }

    public Post(User author, String postImageUrl, String postText) {
        this.author = author;
        this.postImageUrl = postImageUrl;
        this.postText = postText;
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.numberOfComments = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    protected Post(Parcel in) {
        id = in.readString();
        timestamp = in.readLong();
        author = (User) in.readValue(User.class.getClassLoader());
        postImageUrl = in.readString();
        postText = in.readString();
        numberOfComments = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(timestamp);
        dest.writeValue(author);
        dest.writeString(postImageUrl);
        dest.writeString(postText);
        dest.writeInt(numberOfComments);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}