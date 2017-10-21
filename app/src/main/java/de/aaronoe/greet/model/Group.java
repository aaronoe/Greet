package de.aaronoe.greet.model;

import org.parceler.Parcel;

import java.util.UUID;

@Parcel
public class Group {

    private String groupId;
    private String groupName;
    private Post latestPost;

    public Group() {}

    public Group(String groupName) {
        this.groupName = groupName;
        this.groupId = UUID.randomUUID().toString();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Post getLatestPost() {
        return latestPost;
    }

    public void setLatestPost(Post latestPost) {
        this.latestPost = latestPost;
    }
}