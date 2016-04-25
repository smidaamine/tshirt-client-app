package com.smamine.tshirapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.bumptech.glide.BitmapRequestBuilder;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.google.gson.Gson;
import com.smamine.tshirapp.MainActivity;
import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.DTO.ImageDTO;
import com.smamine.tshirapp.model.DTO.ProductDTO;
import com.smamine.tshirapp.model.DTO.TextDTO;
import com.smamine.tshirapp.model.Order;
import com.smamine.tshirapp.model.UserRepository;
import com.smamine.tshirapp.model.ViewOrder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderRecapActivity extends AppCompatActivity {
    public static final MediaType JASON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType ZIP = MediaType.parse("application/zip");
    public static final int VIEW_NUMBER = 4;
    private static final int BUFFER = 2048;
    private File folder;
    public static final int NUM_OF_COLUMNS = 1;
    public static final int GRID_PADDING = 1;


    private int columnWidth;
    private GridViewImageAdapter adapterGrid;
    private GridView gridView;
    public final static String APP_PATH_SD_CARD = "/tshirtapp";
    String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
    private Button create;
    private Button cancel;
    private OkHttpClient client = new OkHttpClient();
    private FormEditText quantityEdit ;

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        progressDialog = new ProgressDialog(OrderRecapActivity.this);
        setContentView(R.layout.activity_order_recap);
        Bundle bundle = getIntent().getExtras();
        final String orderID = bundle.getString(CustumShirtActivity.ORDER_ID);
        final HashMap<Integer, ViewOrder> viewOrders = (HashMap<Integer, ViewOrder>) bundle.getSerializable(CustumShirtActivity.VIEW_SELECTED);
        final String productID = bundle.getString(CustumShirtActivity.PRODUCT_ID);
        create = (Button) findViewById(R.id.create);
        cancel = (Button) findViewById(R.id.cancel);
        ShareButton shareButton = (ShareButton)findViewById(R.id.share);


        quantityEdit= (FormEditText) findViewById(R.id.quantity);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempQuantity= quantityEdit.getText().toString();
                FormEditText[] allFields    = { quantityEdit };


                boolean allValid = true;
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }

                if (allValid) {
                    sendOrder(viewOrders, productID, orderID, Integer.parseInt(tempQuantity));

                } else {
                    // EditText are going to appear with an exclamation mark and an explicative message.
                }

            }
        });


        folder = new File(fullPath);

        File[] files = null;
        File fileImg = null;
        File imageFolder = null;
        String shareImage= "";
        ArrayList<String> listFiles = new ArrayList<>();
        if (folder.isDirectory()) {
            imageFolder = new File(folder.getPath(), orderID);
        }

        if (imageFolder.isDirectory())
            files = imageFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                listFiles.add(f.getAbsolutePath());
                if(f.getName().charAt(0)=='0')
                    shareImage=f.getAbsolutePath();
            }

        }

        final File[] finalFiles = files;

        gridView = (GridView) findViewById(R.id.grid_view);
        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (finalFiles != null) {
                    Intent intent = new Intent(ProductDetailsActivity.this, ImageSwipeActivity.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                }
            }
        });*/

        InitilizeGridLayout();

        // Gridview adapter
        adapterGrid = new GridViewImageAdapter(OrderRecapActivity.this, listFiles,
                columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapterGrid);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(shareImage, options);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareButton.setShareContent(content);


    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((getScreenWidth() - ((NUM_OF_COLUMNS + 1) * padding)) / NUM_OF_COLUMNS);

        gridView.setNumColumns(NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }


    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager)
                getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public class GridViewImageAdapter extends BaseAdapter {

        private Activity _activity;
        private ArrayList<String> _filePaths = new ArrayList<String>();
        private int imageWidth;

        public GridViewImageAdapter(Activity activity, ArrayList<String> filePaths,
                                    int imageWidth) {
            this._activity = activity;
            this._filePaths = filePaths;
            this.imageWidth = imageWidth;
        }

        @Override
        public int getCount() {
            return this._filePaths.size();
        }

        @Override
        public Object getItem(int position) {
            return this._filePaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(_activity);
            } else {
                imageView = (ImageView) convertView;
            }

            // get screen dimensions
            Bitmap image = decodeFile(_filePaths.get(position), imageWidth,
                    imageWidth);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                    imageWidth));
            imageView.setImageBitmap(image);

            // image view click listener

            return imageView;
        }


    }

    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void sendOrder(HashMap<Integer, ViewOrder> viewOrders, String productID, String orderID, int quantity) {
        progressDialog.show();
        List<ImageDTO> temp = new ArrayList<>();
        List<TextDTO> tempText = new ArrayList<>();
        List<String> listFiles = new ArrayList<>();
        for (int key : viewOrders.keySet()) {
            ViewOrder value = viewOrders.get(key);


            //creat images
            for (int i = 0; i < value.getImagerOrder().size(); i++) {
                temp.add(new ImageDTO(value.getImagerOrder().get(i).getUri()
                        , value.getImagerOrder().get(i).getUuid()
                        , key,value.getImagerOrder().get(i).getLogo()));
                listFiles.add(value.getImagerOrder().get(i).getUri());
            }


            //create text
            for (int i = 0; i < value.getTextOrders().size(); i++) {
                tempText.add(new TextDTO(value.getTextOrders().get(i).getValue()
                        , key
                        , value.getTextOrders().get(i).getFontName()));
            }

        }

        Order order = new Order(tempText, orderID, temp, quantity, productID, Profile.getCurrentProfile().getId());


        //clean view dir
        for (int i = 0 ; i<VIEW_NUMBER;i++){
            if (isViewEmpty(tempText,temp,i))
                deleteScreenView(i,fullPath+"/"+orderID);
        }
        zip(temp, fullPath + "/temp.zip", orderID);
        Gson gson = new Gson();
        String orderGson = gson.toJson(order);

        postImages(orderID,orderGson,Profile.getCurrentProfile().getId());

    }


    public void postOrder(String order) {


        final Activity activity = this;
        RequestBody requestBody = RequestBody.create(JASON, order);
        Request request = new Request.Builder()
                .url(ConstName.ORDER)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(OrderRecapActivity.this, "Error", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(OrderRecapActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(OrderRecapActivity.this, "order Created", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                }

            }
        });
    }


    public void postImages(String orderID, final String order,String fbID) {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .build();

        //TODO check file size
        final Activity activity = this;
             RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("orderID", orderID)
                    .addFormDataPart("fbID", fbID)
                    .addFormDataPart("file", "temp.zip",
                            RequestBody.create(ZIP, new File(fullPath + "/temp.zip")))
                    .build();

            Request request = new Request.Builder()
                    .url(ConstName.UPLOAD)
                    .post(requestBody)
                    .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        progressDialog.dismiss();
                        Toast.makeText(OrderRecapActivity.this, "Error Server", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(OrderRecapActivity.this, "Error upload", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                    postOrder(order);
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {


                        }
                    });

                }

            }
        });


    }


    public void zip(List<ImageDTO> files, String zipFile, String orderID) {
        List<ImageDTO> _files = files;
        String _zipFile = zipFile;


        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(_zipFile);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                if(_files.get(i).getLogo()==true)
                    continue;
                FileInputStream fi = new FileInputStream(_files.get(i).getImagePath());
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_files.get(i).getUuid() + _files.get(i).getImagePath().substring(_files.get(i).getImagePath().lastIndexOf(".")));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            folder = new File(fullPath);

            File[] filesS = null;
            File fileImg = null;
            File imageFolder = null;
            ArrayList<String> listFiles = new ArrayList<>();
            if (folder.isDirectory()) {
                imageFolder = new File(folder.getPath(), orderID);
            }

            if (imageFolder.isDirectory())
                filesS = imageFolder.listFiles();
            if (files != null) {
                for (File f : filesS) {
                    listFiles.add(f.getAbsolutePath());
                }

            }
            for(String f : listFiles){
                FileInputStream fi = new FileInputStream(f);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry( f.substring(f.lastIndexOf("/")));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public boolean isViewEmpty(List<TextDTO> textDTOList,List<ImageDTO> imageDTOList,int view)
    {

        for (ImageDTO imageDTO : imageDTOList){
            if(imageDTO.getView()==view)
                return false;
        }

        for (TextDTO textDTO : textDTOList){
            if(textDTO.getView()==view)
                return  false;
        }
        return true;
    }

    public void deleteScreenView(int view,String fullPath){

     File f = new File(fullPath+"/"+view+"-screenshot.jpg");
        if(f.exists()){
            f.delete();
        }
    }
}
