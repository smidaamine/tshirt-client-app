package com.smamine.tshirapp.model;

import android.content.Context;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;



/**
 * Created by Amine on 12/11/15.
 */
public class UserRepository {


    public static User findUserByFbID(String fbUserID) {

        User result = new Select().from(User.class)
                .where(User.FB_USER_ID+" = ?", fbUserID)
                .executeSingle();


        return result;

    }


    public static boolean save(User user) {


        User temp = new User();
        User.cloneUser(user, temp);
        Long result =user.save();


        if (result != null)
            return true;
        else
            return false;


    }


    public static boolean isEmpty() {


        List<User> results = new Select().from(User.class).execute();
        return results.isEmpty();

    }


    public static Boolean update(User user) {
        User temp = new User();
        User.cloneUser(user, temp);
        Long result =user.save();


        if (result != null)
            return true;
        else
            return false;
        


    }


    public static  void delete(String fbUserID){
        new Delete().from(User.class).where(User.FB_USER_ID+" = ?", fbUserID).execute();

    }
}
