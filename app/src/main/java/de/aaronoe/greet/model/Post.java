package de.aaronoe.greet.model;


import java.util.UUID;

public class Post {

    private String id;
    private long timestamp;
    private User author;
    private String postImageUrl;
    private String postText;

    public Post(User author, String postImageUrl, String postText) {
        this.author = author;
        this.postImageUrl = postImageUrl;
        this.postText = postText;
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    public Post(String id, long timestamp, User author, String postImageUrl, String postText) {
        this.id = id;
        this.timestamp = timestamp;
        this.author = author;
        this.postImageUrl = postImageUrl;
        this.postText = postText;
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
}
