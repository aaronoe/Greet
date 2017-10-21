package de.aaronoe.greet.model;


import java.io.Serializable;
import java.util.UUID;

public class Group implements Serializable {

    private String groupId;
    private String groupName;

    public Group() {}

    public Group(String groupdId, String groupName) {
        this.groupId = groupdId;
        this.groupName = groupName;
    }

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
}
