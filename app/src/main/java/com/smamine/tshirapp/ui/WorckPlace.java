package com.smamine.tshirapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;


import java.io.IOException;
import java.lang.reflect.Type;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.DTO.ProductTypeDTO;
import com.smamine.tshirapp.model.User;
import com.smamine.tshirapp.model.UserRepository;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WorckPlace extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PRODUCTTYPE="PRODUCTRYPE";
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private final OkHttpClient client = new OkHttpClient();
    private  RecyclerView recyclerView;
   private  List<ProductTypeDTO> listType;
    private  ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worck_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressDialog = new ProgressDialog(WorckPlace.this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        String fbUSerID = getIntent().getStringExtra(ConstName.USER_FB_ID);
        LinearLayout navHeader = (LinearLayout) findViewById(R.id.nav_header);
        FrameLayout.LayoutParams draLayoutParams = (FrameLayout.LayoutParams) drawer.getLayoutParams();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) navHeader.getLayoutParams();
        layoutParams.height = 300;

        User user = UserRepository.findUserByFbID(fbUSerID);

        TextView firstName = (TextView) findViewById(R.id.first_name);
        TextView lastName = (TextView) findViewById(R.id.last_name);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        String profileImgUrl = "https://graph.facebook.com/" + user.getFbUserId()+ "/picture?type=small";


        Glide.with(WorckPlace.this)
                .load(profileImgUrl)
                .into(imageView);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);


        getListTypes();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.worck_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            LoginManager.getInstance().logOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {

            Intent intent = new Intent(this,OrdersHistoryActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_details) {
            Intent intent = new Intent(this,UserDetails.class);
            startActivity(intent);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



//    private List<ItemObjects> getListItemData(){
//        List<ItemObjects> listViewItems = new ArrayList<ItemObjects>();
//        listViewItems.add(new ItemObjects("One",R.drawable.ic_menu_camera));
//        listViewItems.add(new ItemObjects("Ethane", R.drawable.ic_menu_send));
//
//
//        return listViewItems;
//    }





    public void getListTypes(){

        final Activity activity = this ;
        final Request request = new Request.Builder()
                .url(ConstName.PRODUCT_TYPE)
                .build();


        progressDialog.show();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WorckPlace.this, "Error", Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();
                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) {
                if(response.code()==ConstName.OK) {
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<List<ProductTypeDTO>>() {
                    }.getType();
                    listType = gson.fromJson(response.body().charStream(), collectionType);
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(WorckPlace.this, listType);
                            recyclerView.setAdapter(rcAdapter);
                            progressDialog.dismiss();

                        }
                    });

                }
            }
        });


    }
    class SolventRecyclerViewAdapter extends RecyclerView.Adapter<SolventViewHolders> {

        private List<ProductTypeDTO> itemList;
        private Context context;

        public SolventRecyclerViewAdapter(Context context, List<ProductTypeDTO> itemList) {
            this.itemList = itemList;
            this.context = context;
        }

        @Override
        public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solvent_list, null);
            SolventViewHolders rcv = new SolventViewHolders(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(SolventViewHolders holder, int position) {

            Glide.with(WorckPlace.this)
                    .load(ConstName.GET_LOGO+itemList.get(position).getLogoPath())
                    .into(holder.countryPhoto);
        }

        @Override
        public int getItemCount() {
            return this.itemList.size();
        }
    }


    class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView countryName;
        public ImageView countryPhoto;

        public SolventViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            countryName = (TextView) itemView.findViewById(R.id.country_name);
            countryPhoto = (ImageView) itemView.findViewById(R.id.country_photo);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(WorckPlace.this,ProductActivity.class);
            Bundle bundle = new Bundle();
           bundle.putSerializable(WorckPlace.PRODUCTTYPE,listType.get(getPosition()));
            i.putExtras(bundle);
            startActivity(i);
        }
}











}
