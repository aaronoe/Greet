package de.aaronoe.greet.model;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.UUID;

@Parcel
public class Group {

    public String groupId;
    public String groupName;
    public Post latestPost;

    public Group() {}

    public Group(String groupName) {
        this.groupName = groupName;
        this.groupId = UUID.randomUUID().toString();
    }

    public Group(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
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