package com.smamine.tshirapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.DTO.ProductDTO;
import com.smamine.tshirapp.model.DTO.ProductTypeDTO;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductActivity extends AppCompatActivity {

    public static  final String  PRODUCTTYPE="PRODUCTTYPE";
    public static  final String  SIZE="SIZE";
    RecyclerView recyclerView;
    public List<ProductDTO> productList ;
    private final OkHttpClient client = new OkHttpClient();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ProductTypeDTO productTypeDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        recyclerView =(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1,1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        productTypeDTO = (ProductTypeDTO)getIntent().getSerializableExtra(WorckPlace.PRODUCTTYPE);
        getProducts();
    }


    public void getProducts(){
        final Activity activity = this;
        final ProgressDialog progressDialog = new ProgressDialog(ProductActivity.this);

        final Request request = new Request.Builder()
                .url(ConstName.PRODUCT+"?type="+productTypeDTO.getId())
                .build();

        progressDialog.show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(ProductActivity.this, "Error", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == ConstName.OK) {
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<List<ProductDTO>>() {
                    }.getType();
                    productList = gson.fromJson(response.body().charStream(), collectionType);
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(ProductActivity.this, productList);
                            recyclerView.setAdapter(rcAdapter);


                        }
                    });

                }
            }
        });


    }




    class SolventRecyclerViewAdapter extends RecyclerView.Adapter<SolventViewHolders> {

        private List<ProductDTO> itemList;
        private Context context;

        public SolventRecyclerViewAdapter(Context context, List<ProductDTO> itemList) {
            this.itemList = itemList;
            this.context = context;
        }

        @Override
        public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, null);
            SolventViewHolders rcv = new SolventViewHolders(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(SolventViewHolders holder, int position) {
            switch (itemList.get(position).getSize().toLowerCase()){
                case "s" :
                    holder.logo.setImageResource(R.drawable.ic_s);
                    break;
                case "l" :
                    holder.logo.setImageResource(R.drawable.ic_l);
                    break;
                case "m" :
                    holder.logo.setImageResource(R.drawable.ic_m);
                    break;
                case "xl" :
                    holder.logo.setImageResource(R.drawable.ic_xl);
                    break;
                case "xxl" :
                    holder.logo.setImageResource(R.drawable.ic_xxl);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return this.itemList.size();
        }
    }


    class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView logo;
        public SolventViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            logo= (ImageView) itemView.findViewById(R.id.country_photo);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(ProductActivity.this, CustumShirtActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(SIZE, productList.get(getPosition()).getSize());
            bundle.putString(PRODUCTTYPE, productList.get(getPosition()).getProductType().getId());
            i.putExtras(bundle);
            startActivity(i);
        }
    }





}
