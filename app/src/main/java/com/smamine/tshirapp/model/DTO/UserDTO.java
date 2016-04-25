package com.smamine.tshirapp.model.DTO;

import java.io.Serializable;

/**
 * Created by aminedev on 1/16/16.
 */
public class UserDTO implements Serializable {

    private String fbID;
    private String username;
    private String firstName;
    private String lastName;
    private String wilaya;
    private String phoneNumber;
    private String urlFb;
    private String pictureUri;


    public UserDTO(String fbID, String username, String firstName, String lastName, String wilaya, String phoneNumber, String urlFb,String pictureUri) {
        this.fbID = fbID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.wilaya = wilaya;
        this.phoneNumber = phoneNumber;
        this.urlFb = urlFb;
        this.pictureUri=pictureUri;
    }

    public String getFbID() {
        return fbID;
    }

    public void setFbID(String fbID) {
        this.fbID = fbID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getWilaya() {
        return wilaya;
    }

    public void setWilaya(String wilaya) {
        this.wilaya = wilaya;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUrlFb() {
        return urlFb;
    }

    public void setUrlFb(String urlFb) {
        this.urlFb = urlFb;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }
}
