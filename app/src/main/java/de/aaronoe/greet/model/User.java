package de.aaronoe.greet.model;


import com.google.firebase.auth.FirebaseUser;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
public class User {

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

    public String userID;
    public String profileName;
    public String pictureUrl;
    public String emailAdress;
    public String messagingToken;


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

    public String getMessagingToken() {
        return messagingToken;
    }

    public void setMessagingToken(String messagingToken) {
        this.messagingToken = messagingToken;
    }
}