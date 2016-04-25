package com.smamine.tshirapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.smamine.tshirapp.model.UserRepository;
import com.smamine.tshirapp.ui.InscriptionActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class LogInActivityFragment extends Fragment {
    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    public LogInActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_log_in, container, false);


        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        final TextView loginText = (TextView) view.findViewById(R.id.login_text);
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(LoginResult loginResult) {

                if (UserRepository.isEmpty()==true){

                    Intent i = new Intent(getActivity(), InscriptionActivity.class);
                    getActivity().startActivity(i);

                }


                getActivity().finish();

            }

            @Override
            public void onCancel() {
                System.out.println("ddd");

            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("LoginActivity.class", exception.toString());
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }





}
