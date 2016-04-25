package com.smamine.tshirapp.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.Gson;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.DTO.UserDTO;
import com.smamine.tshirapp.model.User;
import com.smamine.tshirapp.model.UserRepository;
import com.smamine.tshirapp.ui.InscriptionActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by aminedev on 1/16/16.
 */
public class AccountService {

    public static final MediaType JASON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public void createAccount(final UserDTO user, final Context context, final User userCreate) throws Exception {

        final Activity activity = (Activity) context;
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        RequestBody requestBody = RequestBody.create(JASON, userJson);
        Request request = new Request.Builder()
                .url(ConstName.ACCOUNT)
                .post(requestBody)
                .build();

         client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "Error creation client", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                int status = response.code();
                if (status == 400) {
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                         //   Toast.makeText(context, "USer Exixt", Toast.LENGTH_LONG).show();
                            Boolean result = UserRepository.save(userCreate);
                            activity.finish();
                        }
                    });
                } else {
                    System.out.println(response.body().string());
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Boolean result = UserRepository.save(userCreate);
                            Toast.makeText(context, "Created", Toast.LENGTH_LONG).show();
                            activity.finish();
                        }
                    });
                }

            }
        });


    }


}
