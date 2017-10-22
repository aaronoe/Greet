package de.aaronoe.greet.model;

import org.parceler.Parcel;

import java.util.UUID;

@Parcel
public class Comment {

    private String commentText;
    private long timestamp;
    private String id;
    private User user;

    public Comment(String commentText, User user) {
        this.commentText = commentText;
        this.timestamp = System.currentTimeMillis();
        this.id = UUID.randomUUID().toString();
        this.user = user;
    }

    public Comment() {}

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
