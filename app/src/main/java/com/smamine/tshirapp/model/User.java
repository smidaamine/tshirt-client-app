package com.smamine.tshirapp.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Amine on 12/11/15.
 */
@Table(name = "Client", id = BaseColumns._ID)
public class User extends Model {

    public static final String FB_USER_ID = "fbUserId";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String WILLAYA = "willaya";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String USER_URI = "userUri";

    @Column(name = "fbUserId", index = true)
    private String fbUserId;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "willaya")
    private String willaya;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(name = "userUri")
    private String userUri;


    public String getFbUserId() {
        return fbUserId;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserUri() {
        return userUri;
    }

    public void setUserUri(String userUri) {
        this.userUri = userUri;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
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


    public String getWillaya() {
        return willaya;
    }

    public void setWillaya(String willaya) {
        this.willaya = willaya;
    }


    public static void cloneUser(User user, User temp) {


        temp.setFbUserId(user.getFbUserId());
        temp.setFirstName(user.getFirstName());
        temp.setLastName(user.getLastName());
        temp.setWillaya(user.getWillaya());
        temp.setPhoneNumber(user.getPhoneNumber());
        temp.setUserUri(user.getUserUri());


    }


    public User(String fbUserId, String firstName, String lastName, String willaya, String phoneNumber, String userUri) {
       super();
        this.fbUserId = fbUserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.willaya = willaya;
        this.phoneNumber = phoneNumber;
        this.userUri = userUri;
    }


    public User() {
        super();
    }
}
