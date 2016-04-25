package com.smamine.tshirapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.User;
import com.smamine.tshirapp.model.DTO.UserDTO;
import com.smamine.tshirapp.model.UserRepository;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpadteProfile extends AppCompatActivity {
    public static final MediaType JASON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    String[] listWillaya = {"Adrar",
            "Chlef",
            "Laghouat",
            "Oum El Bouaghi",
            "Batna",
            "Béjaïa",
            "Biskra",
            "Béchar",
            "Blida",
            "Bouira",
            "Tamanrasset",
            "Tébessa",
            "Tlemcen",
            "Tiaret",
            "Tizi Ouzou",
            "Alger",
            "Djelfa",
            "Jijel",
            "Sétif",
            "Saïda",
            "Skikda",
            "Sidi Bel Abbès",
            "Annaba",
            "Guelma",
            "Constantine",
            "Médéa",
            "Mostaganem",
            "M'Sila",
            "Mascara",
            "Ouargla",
            "Oran",
            "El Bayadh",
            "Illizi",
            "Bordj Bou Arreridj",
            "Boumerdès",
            "El Tarf",
            "Tindouf",
            "Tissemsilt",
            "El Oued",
            "Khenchela",
            "Souk Ahras",
            "Tipaza",
            "Mila",
            "Aïn Defla",
            "Naâma",
            "Aïn Témouchent",
            "Ghardaïa",
            "Relizane"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upadte_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String fbid = getIntent().getStringExtra(ConstName.USER_FB_ID);
        final User user = UserRepository.findUserByFbID(fbid);
        final FormEditText phone = (FormEditText) findViewById(R.id.phone);
        final Spinner willaya = (Spinner) findViewById(R.id.willaya);

        phone.setText(user.getPhoneNumber());

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listWillaya);
willaya.setAdapter(adapter);
        willaya.setSelection(getWillayaCode(user.getWillaya()),false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Activity activity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormEditText[] allFields    = { phone };

                boolean allValid = true;
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }

                if (allValid) {
                    user.setPhoneNumber(phone.getText().toString());
                    user.setWillaya((String)willaya.getSelectedItem());


                    UserDTO userDTO = new UserDTO(fbid, Profile.getCurrentProfile().getName()
                            , Profile.getCurrentProfile().getFirstName()
                            , Profile.getCurrentProfile().getLastName()
                            , (String)willaya.getSelectedItem()
                            , phone.getText().toString()
                            , Profile.getCurrentProfile().getLinkUri().toString()
                            , Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString());


                    final ProgressDialog progressDialog = new ProgressDialog(UpadteProfile.this);

                    Gson gson = new Gson();
                    String userJson = gson.toJson(userDTO);

                    RequestBody requestBody = RequestBody.create(JASON, userJson);
                    Request request = new Request.Builder()
                            .url(ConstName.PROFILE)
                            .put(requestBody)
                            .build();

                    progressDialog.show();
                    client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {

                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(UpadteProfile.this, "Error Update", Toast.LENGTH_LONG).show();
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
                                        progressDialog.dismiss();
                                        Toast.makeText(UpadteProfile.this, "Error Update", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                System.out.println(response.body().string());

                                UserRepository.update(user);
                                activity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(UpadteProfile.this, "Updated", Toast.LENGTH_LONG).show();
                                        activity.finish();
                                    }
                                });

                            }

                        }
                    });
                } else {
                    // EditText are going to appear with an exclamation mark and an explicative message.
                }


            }
        });
    }


    public int  getWillayaCode(String willaya){
        for(int i =0; i< listWillaya.length;i++){
            if (listWillaya[i].equals(willaya))
                return i;
        }
        return 0;
    }

}
