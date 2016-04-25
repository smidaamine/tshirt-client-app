package com.smamine.tshirapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.DTO.OrderResult;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Payment extends AppCompatActivity {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
    private String imageName;
    String imagePath= "";
    public final static String APP_PATH_SD_CARD = "/tshirtapp";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
    private final OkHttpClient client = new OkHttpClient();
    private ProgressDialog progressDialog;
    private Button sendPayment;
    private ImageView paymentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        progressDialog = new ProgressDialog(Payment.this);
        ImageButton takeImg = (ImageButton) findViewById(R.id.takeImg);
        sendPayment =(Button)findViewById(R.id.sendPayment);
        paymentImage =(ImageView)findViewById(R.id.paymentImage);
        final OrderResult orderResult = (OrderResult) getIntent().getSerializableExtra(OrdersHistoryActivity.ORDER);

        takeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();

                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, 1);
                }



            }
        });

        sendPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postImage(orderResult.getId());
            }
        });


        if(orderResult.getPayment()!=null) {
            Glide.with(Payment.this)
                    .load(ConstName.GET_PAYMENT + orderResult.getId())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(500, 500) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                            //TODO set image depende on seleted area
                            paymentImage.setBackground(ob);
                        }
                    });
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView mImageView = (ImageView) findViewById(R.id.photo);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


//
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            mImageView.setImageBitmap(bitmap);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bm = BitmapFactory.decodeFile(imagePath,options);
            mImageView.setImageBitmap(bm);



        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name

        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        imageName = UUID.randomUUID().toString();


        File file = File.createTempFile(
                imageName,  /* prefix */
                ".jpg",         /* suffix */
                dir      /* directory */
        );

        imagePath=file.getAbsolutePath();

        return file;
    }



    public void postImage(final String orderID) {


        final Activity activity = this;
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("orderID", orderID)
                .addFormDataPart("file", "temp.jpg",
                        RequestBody.create(MEDIA_TYPE_PNG, new File(imagePath)))
                .build();

        Request request = new Request.Builder()
                .url(ConstName.PAYMENT)
                .post(requestBody)
                .build();
        progressDialog.show();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(Payment.this, "Error", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                int status = response.code();
                if (status == 400) {
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            String message =response.header("Failure");
                            Toast.makeText(Payment.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(Payment.this, "Payment Saved", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);

                            Glide.with(Payment.this)
                                    .load(ConstName.GET_PAYMENT + orderID)
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(500, 500) {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                            //TODO set image depende on seleted area
                                            paymentImage.setBackground(ob);
                                        }
                                    });
                        }
                    });

                }

            }
        });
    }
}
