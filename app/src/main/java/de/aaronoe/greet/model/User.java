package de.aaronoe.greet.model;


public class User {

    public User(String userID, String profileName, String pictureUrl, String emailAdress) {
        this.userID = userID;
        this.profileName = profileName;
        this.pictureUrl = pictureUrl;
        this.emailAdress = emailAdress;
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
}
