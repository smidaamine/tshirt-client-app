package com.smamine.tshirapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;

import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.User;
import com.smamine.tshirapp.model.UserRepository;

public class UserDetails extends AppCompatActivity {

    private User user ;
    private EditText name;
    private EditText lastName;
    private EditText phone;
    private EditText willaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageView = (ImageView) findViewById(R.id.user_image);
        user = UserRepository.findUserByFbID(Profile.getCurrentProfile().getId());
        String profileImgUrl = "https://graph.facebook.com/" + user.getFbUserId() + "/picture?type=large";


        Glide.with(this)
                .load(profileImgUrl)
                .into(imageView);

        name = (EditText) findViewById(R.id.first_name);



        lastName = (EditText) findViewById(R.id.last_name);


        phone = (EditText) findViewById(R.id.phone);

        willaya = (EditText) findViewById(R.id.willaya);

        updateUi();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDetails.this, UpadteProfile.class);

              intent.putExtra(ConstName.USER_FB_ID, user.getFbUserId());
                startActivity(intent);
            }
        });
    }

    private void updateUi() {
        name.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        phone.setText(user.getPhoneNumber());
        willaya.setText(user.getWillaya());

    }


    @Override
    protected void onResume() {
        super.onResume();
        user = UserRepository.findUserByFbID(Profile.getCurrentProfile().getId());
        updateUi();

    }
}
