package com.smamine.tshirapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.DTO.OrderResult;
import com.smamine.tshirapp.model.DTO.ProductDTO;
import com.smamine.tshirapp.model.DTO.UserDTO;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrdersHistoryActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    RecyclerView recyclerView;
    public  static final String ORDER = "order";
     List<OrderResult> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String fbID=Profile.getCurrentProfile().getId();
        getOrders(fbID);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        recyclerView =(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1,1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

    }


    private void getOrders(String fbID) {

        final ProgressDialog progressDialog = new ProgressDialog(OrdersHistoryActivity.this);

        final Activity activity = this ;
        Request request = new Request.Builder()
                .url(ConstName.ORDER + "?fbID=" + fbID)
                .build();

        progressDialog.show();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {


                int status = response.code();
                if (status == ConstName.NOTFOUND) {

                } else {
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<List<OrderResult>>() {
                    }.getType();
                     orders = gson.fromJson(response.body().charStream(), collectionType);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(OrdersHistoryActivity.this, orders);
                            recyclerView.setAdapter(rcAdapter);
                            progressDialog.dismiss();

                        }
                    });


                }

            }
        });
    }


    class SolventRecyclerViewAdapter extends RecyclerView.Adapter<SolventViewHolders> {

        private List<OrderResult> itemList;
        private Context context;

        public SolventRecyclerViewAdapter(Context context, List<OrderResult> itemList) {
            this.itemList = itemList;
            this.context = context;
        }

        @Override
        public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, null);
            SolventViewHolders rcv = new SolventViewHolders(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(SolventViewHolders holder, int position) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = simpleDateFormat.format(itemList.get(position).getCreationDate());
            holder.info.setText(itemList.get(position).getRef()+"-"+date);
            //holder.countryPhoto.setImageResource(itemList.get(position).image);

            if(itemList.get(position).getStatus().equals("NEW")){
                holder.cardView.setCardBackgroundColor(Color.parseColor("#9E9E9E"));
            }if(itemList.get(position).getStatus().equals("PROGRESS")){
                holder.cardView.setCardBackgroundColor(Color.parseColor("#81C784"));
            }
            if(itemList.get(position).getStatus().equals("CANCELED")){
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FF5722"));
            }
            if(itemList.get(position).getStatus().equals("DELIVERED")){
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#0091EA"));
                }

            Glide.with(OrdersHistoryActivity.this)
                    .load(ConstName.GET_LOGO+itemList.get(position).getProduct().getLogoPath())
                    .into(holder.countryPhoto);
        }

        @Override
        public int getItemCount() {
            return this.itemList.size();
        }
    }


    class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView info;
        public ImageView countryPhoto;
        public CardView cardView;


        public SolventViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            info = (TextView) itemView.findViewById(R.id.country_name);
            countryPhoto = (ImageView) itemView.findViewById(R.id.country_photo);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(OrdersHistoryActivity.this, OrderDetails.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ORDER,orders.get(getPosition()));
            i.putExtras(bundle);
            startActivity(i);
        }
    }

}
