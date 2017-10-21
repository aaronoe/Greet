package de.aaronoe.greet.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Group implements Parcelable {

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

    protected Group(Parcel in) {
        groupId = in.readString();
        groupName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeString(groupName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}