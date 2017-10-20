package de.aaronoe.greet.model;


import java.util.UUID;

public class Group {

    private String groupdId;
    private String groupName;

    public Group() {}

    public Group(String groupdId, String groupName) {
        this.groupdId = groupdId;
        this.groupName = groupName;
    }

    public Group(String groupName) {
        this.groupName = groupName;
        this.groupdId = UUID.randomUUID().toString();
    }

    public String getGroupdId() {
        return groupdId;
    }

    public void setGroupdId(String groupdId) {
        this.groupdId = groupdId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
