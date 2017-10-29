package de.aaronoe.greet.model;

import org.parceler.Parcel;

import java.util.UUID;

@Parcel
public class Post {

    public String id;
    public long timestamp;
    public User author;
    public String postImageUrl;
    public String postText;
    public int numberOfComments;

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

}