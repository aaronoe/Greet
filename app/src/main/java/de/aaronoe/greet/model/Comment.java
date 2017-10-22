package de.aaronoe.greet.model;

import org.parceler.Parcel;

import java.util.UUID;

@Parcel
public class Comment {

    private String commentText;
    private long timestamp;
    private String id;
    private User author;

    public Comment(String commentText, User author) {
        this.commentText = commentText;
        this.timestamp = System.currentTimeMillis();
        this.id = UUID.randomUUID().toString();
        this.author = author;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
