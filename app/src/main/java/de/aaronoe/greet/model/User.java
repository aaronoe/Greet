package de.aaronoe.greet.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

public class User implements Parcelable {

    public User() {}

    public User(String userID, String profileName, String pictureUrl, String emailAdress) {
        this.userID = userID;
        this.profileName = profileName;
        this.pictureUrl = pictureUrl;
        this.emailAdress = emailAdress;
    }

    public User(FirebaseUser firebaseUser) {
        this.userID = firebaseUser.getUid();
        this.profileName = firebaseUser.getDisplayName();
        if (firebaseUser.getPhotoUrl() != null) {
            this.pictureUrl = firebaseUser.getPhotoUrl().toString();
        }
        this.emailAdress = firebaseUser.getEmail();
    }

    private String userID;
    private String profileName;
    private String pictureUrl;
    private String emailAdress;


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        this.emailAdress = emailAdress;
    }

    protected User(Parcel in) {
        userID = in.readString();
        profileName = in.readString();
        pictureUrl = in.readString();
        emailAdress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(profileName);
        dest.writeString(pictureUrl);
        dest.writeString(emailAdress);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}