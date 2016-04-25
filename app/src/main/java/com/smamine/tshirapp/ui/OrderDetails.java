package com.smamine.tshirapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smamine.tshirapp.R;
import com.smamine.tshirapp.model.DTO.OrderResult;

import java.text.SimpleDateFormat;

public class OrderDetails extends AppCompatActivity {
    private static final String WAIT_PAYMENT="WAIT_PAYMENT";

    TextView ref;
    TextView status;
    TextView produit;
    TextView quantity;
    TextView total;
    TextView creationDate;
    TextView canceledFor;
    LinearLayout linearLayout;
    Button paymentBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ref = (TextView) findViewById(R.id.ref);
        status = (TextView) findViewById(R.id.status);
        produit = (TextView) findViewById(R.id.produit);
        quantity = (TextView) findViewById(R.id.quantity);
        total = (TextView) findViewById(R.id.total);
        creationDate = (TextView) findViewById(R.id.creationDate);
        canceledFor = (TextView) findViewById(R.id.canceledFor);
        linearLayout = (LinearLayout) findViewById(R.id.canceled);
        paymentBtn = (Button) findViewById(R.id.payment);


        final OrderResult orderResult = (OrderResult) getIntent().getSerializableExtra(OrdersHistoryActivity.ORDER);
        if (!orderResult.getStatus().equals("CANCELED")) {
            linearLayout.setVisibility(View.INVISIBLE);
        }

        if (!orderResult.getStatus().equals(WAIT_PAYMENT)) {
            paymentBtn.setVisibility(View.INVISIBLE);
        }
        ref.setText(orderResult.getRef().toString());
        status.setText(orderResult.getStatus());
        produit.setText(orderResult.getProduct().getProductType().getName());
        quantity.setText(orderResult.getQuantity() + "");
        canceledFor.setText(orderResult.getCanceledFor());
        total.setText(orderResult.getTotal() + "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        creationDate.setText(simpleDateFormat.format(orderResult.getCreationDate()));



        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(OrdersHistoryActivity.ORDER,orderResult);
                Intent intent = new Intent(OrderDetails.this,Payment.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }


}
