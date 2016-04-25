package com.smamine.tshirapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.smamine.tshirapp.services.AccountService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class InscriptionActivityFragment extends Fragment {
    ProgressDialog progressDialog ;
    AccountService accountService = new AccountService();
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


    EditText phoneNumber;
    FormEditText firstName;
    EditText lastName;
    Spinner willaya;


    public InscriptionActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_inscription, container, false);

          phoneNumber = (EditText) view.findViewById(R.id.user_phone);
          firstName = (FormEditText) view.findViewById(R.id.first_name);
          lastName = (EditText) view.findViewById(R.id.last_name);
          willaya = (Spinner) view.findViewById(R.id.willaya);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,listWillaya);
        willaya.setAdapter(adapter);

        Button continueBtn = (Button) view.findViewById(R.id.save_button);

        final String fbid = getActivity().getIntent().getStringExtra(ConstName.USER_FB_ID);

        getClient(fbid);




            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    UserDTO userDTO = new UserDTO(Profile.getCurrentProfile().getId(), Profile.getCurrentProfile().getName()
                            , firstName.getText().toString()
                            , lastName.getText().toString()
                            , willaya.getSelectedItem().toString()
                            , phoneNumber.getText().toString()
                            , Profile.getCurrentProfile().getLinkUri().toString()
                            , Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString());
                    try {
                        User user = new User();
                        Profile profile = Profile.getCurrentProfile();
                        user.setUserUri(profile.getLinkUri().toString());
                        user.setFirstName(firstName.getText().toString());
                        user.setLastName(lastName.getText().toString());
                        user.setFbUserId(profile.getId());
                        user.setPhoneNumber(phoneNumber.getText().toString());
                        user.setWillaya(willaya.getSelectedItem().toString());

                        accountService.createAccount(userDTO, getActivity(),user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                  ///  getActivity().finish();

                }
            });


            return view;

    }

    private void getClient(String fbID) {


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();

        final Activity activity = getActivity() ;
        Request request = new Request.Builder()
                .url(ConstName.ACCOUNT + "/" + fbID)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        activity.finish();
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {


                int status = response.code();
                if (status == ConstName.NOTFOUND) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });

                } else {
                    Gson gson = new Gson();

                    final UserDTO user = gson.fromJson(response.body().charStream(), UserDTO.class);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            phoneNumber.setText(user.getPhoneNumber());
                            phoneNumber.setEnabled(false);
                            firstName.setText(user.getFirstName());
                            firstName.setEnabled(false);
                            lastName.setText(user.getLastName());
                            lastName.setEnabled(false);
                            willaya.setSelection(getWillayaCode(user.getWilaya()), false);
                            willaya.setEnabled(false);
                            progressDialog.dismiss();

                        }
                    });


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
