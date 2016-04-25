package com.smamine.tshirapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.smamine.tshirapp.LogInActivity;
import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.UserRepository;
import com.smamine.tshirapp.services.AccountService;

public class SplashActivity extends AppCompatActivity {


    private Button start;
    AccountService accountService = new AccountService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FacebookSdk.sdkInitialize(this.getApplicationContext());


        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWithToken(AccessToken.getCurrentAccessToken());
            }
        });


    }


    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {

            Intent i;
            if (UserRepository.isEmpty()) {

                i = new Intent(SplashActivity.this, InscriptionActivity.class);
                i.putExtra(ConstName.USER_FB_ID, Profile.getCurrentProfile().getId());
                startActivity(i);
            } else {

                i = new Intent(SplashActivity.this, WorckPlace.class);
                i.putExtra(ConstName.USER_FB_ID, Profile.getCurrentProfile().getId());
                startActivity(i);

            }

        } else {

            Intent i = new Intent(SplashActivity.this, LogInActivity.class);
            startActivity(i);


        }
    }
}
