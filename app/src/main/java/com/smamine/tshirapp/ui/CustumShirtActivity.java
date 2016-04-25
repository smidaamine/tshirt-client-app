package com.smamine.tshirapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smamine.tshirapp.R;
import com.smamine.tshirapp.constUtil.ConstName;
import com.smamine.tshirapp.model.DTO.CateogoryDTO;
import com.smamine.tshirapp.model.DTO.LogoDTO;
import com.smamine.tshirapp.model.DTO.ProductDTO;
import com.smamine.tshirapp.model.DTO.TextColorDTO;
import com.smamine.tshirapp.model.ImagerOrder;
import com.smamine.tshirapp.model.TextOrder;
import com.smamine.tshirapp.model.ViewOrder;
import com.smamine.tshirapp.util.ImageUtil;
import com.smamine.tshirapp.util.ProductAdapter;
import com.smamine.tshirapp.util.StoreViews;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustumShirtActivity extends AppCompatActivity {
    public final static String PRODUCT_ID = "PRODUCT_ID";
    public final static String ORDER_ID = "ORDER_ID";
    public final static String APP_PATH_SD_CARD = "/tshirtapp";
    String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
    public static final String VIEW_SELECTED = "VIEW_SELECTED";
    public static final int FRONT = 0;
    private static final int SELECT_PICTURE = 1;
    public static final int BACK = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;
    public List<ProductDTO> productList;
    public List<LogoDTO> logoList;
    public List<TextColorDTO> textColorList;
    public List<CateogoryDTO> categoryList;
    private final OkHttpClient client = new OkHttpClient();
    private String productTypeID;
    private String size;
    ImageView front;
    ImageView back;
    ImageView right;
    ImageView left;
    HashMap<Integer, ViewOrder> viewList;
    int selected = FRONT;
    RelativeLayout workspaceImage;
    ImageView addText;
    FloatingActionButton create;
    ImageView logo;
    float realX = 0;
    float realY = 0;
    ImageView imageSelected = null;
    TextView textSelected = null;
    private Spinner spinnerProduct;
    String orderID = UUID.randomUUID().toString();
    private ListView listView;
    AlertDialog dialog;
    Spinner categorySpinner;
    private ProgressDialog progressDialog;
    Spinner dlgTextColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custum_shirt);
        Bundle bundle = getIntent().getExtras();
        productTypeID = bundle.getString(ProductActivity.PRODUCTTYPE);
        size = bundle.getString(ProductActivity.SIZE);

        progressDialog = new ProgressDialog(CustumShirtActivity.this);
        getProducts();
        ImageView sizePlusImage = (ImageView) findViewById(R.id.sizePlusImage);
        ImageView sizeMinusImage = (ImageView) findViewById(R.id.sizeMinusImage);
        ImageView addImage = (ImageView) findViewById(R.id.addImage);
        ImageView deleteImage = (ImageView) findViewById(R.id.deleteImage);
        ImageView editText = (ImageView) findViewById(R.id.editText);
        workspaceImage = (RelativeLayout) findViewById(R.id.workspaceImage);
        addText = (ImageView) findViewById(R.id.addText);
        create = (FloatingActionButton) findViewById(R.id.create);
        logo = (ImageView) findViewById(R.id.logo);
        front = (ImageView) findViewById(R.id.front);
        back = (ImageView) findViewById(R.id.back);
        right = (ImageView) findViewById(R.id.right);
        left = (ImageView) findViewById(R.id.left);
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);


        viewList = new HashMap<>();
        viewList.put(FRONT, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), FRONT));
        viewList.put(BACK, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), BACK));
        viewList.put(RIGHT, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), RIGHT));
        viewList.put(LEFT, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), LEFT));


        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(CustumShirtActivity.this);
                View content = LayoutInflater.from(CustumShirtActivity.this)
                        .inflate(R.layout.logo_dlg, null);
                builder.setView(content);
                builder.setTitle("Logos");
                listView = (ListView) content.findViewById(R.id.listView);
                categorySpinner = (Spinner) content.findViewById(R.id.category);
                getLogos(null);
                getCategories();


                dialog = builder.create();
                dialog.show();
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));
               /* try {
                    createImage("" + selected);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                workspaceImage.removeAllViews();

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CustumShirtActivity.this);
                    builder.setMessage("Delete image").setTitle("Alert");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            workspaceImage.removeView(imageSelected);
                            imageSelected = null;
                        }
                    });
                    builder.show();

                }

                if (textSelected != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CustumShirtActivity.this);
                    builder.setMessage("Delete Text").setTitle("Alert");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            workspaceImage.removeView(textSelected);
                            textSelected = null;
                        }
                    });
                    builder.show();

                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textSelected != null) {

                    List<String> listFonts = new ArrayList<String>();
                    try {
                        listFonts = Arrays.asList(getAssets().list("fonts"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    final AlertDialog.Builder builder = new AlertDialog.Builder(CustumShirtActivity.this);
                    View content = LayoutInflater.from(CustumShirtActivity.this)
                            .inflate(R.layout.edit_text_dlg, null);
                    builder.setView(content);
                    builder.setTitle("TextView");
                    final EditText dlgEditText = (EditText) content.findViewById(R.id.text);
                    final Spinner dlgFont = (Spinner) content.findViewById(R.id.font);
                    FontAdapter fontAdapter = new FontAdapter(listFonts);
                    dlgFont.setAdapter(fontAdapter);
                    dlgTextColor = (Spinner) content.findViewById(R.id.colorText);

                    dlgEditText.setText(textSelected.getText());
                    final List<String> finalListFonts = listFonts;

                    dlgEditText.setText(textSelected.getText());
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + finalListFonts.get(dlgFont.getSelectedItemPosition()));
                            textSelected.setTag(R.string.font_name, finalListFonts.get(dlgFont.getSelectedItemPosition()));
                            if (dlgTextColor.getSelectedItem() != null)
                                textSelected.setTag(R.string.color, ((TextColorDTO) dlgTextColor.getSelectedItem()).getValue());
                            else
                                textSelected.setTag(R.string.color, Color.WHITE);

                            textSelected.setTypeface(typeface);
                            textSelected.setText(dlgEditText.getText());
                            textSelected.setTextColor(Color.parseColor(((TextColorDTO) dlgTextColor.getSelectedItem()).getValue()));


                        }
                    });


                    builder.show();
                    getTextColors();

                }
            }
        });

        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));
                try {
                    createImage("" + selected);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                workspaceImage.removeAllViews();
                front.setBackgroundColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
                back.setBackgroundColor(Color.TRANSPARENT);
                right.setBackgroundColor(Color.TRANSPARENT);
                left.setBackgroundColor(Color.TRANSPARENT);
                if (viewList.get(FRONT) != null) {
                    for (TextOrder o : viewList.get(FRONT).getTextOrders()) {
                        generateView(o);
                    }

                    for (ImagerOrder o : viewList.get(FRONT).getImagerOrder()) {
                        generateImage(o);
                    }
                }
                selected = FRONT;
                int myWidth = workspaceImage.getWidth();
                int myHeight = workspaceImage.getHeight();
                ProductDTO temp = (ProductDTO) spinnerProduct.getSelectedItem();
                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPath())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                //TODO set image depende on seleted area
                                workspaceImage.setBackground(ob);
                            }
                        });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                front.setBackgroundColor(Color.TRANSPARENT);
                back.setBackgroundColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
                right.setBackgroundColor(Color.TRANSPARENT);
                left.setBackgroundColor(Color.TRANSPARENT);

                viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));
                try {
                    createImage("" + selected);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                workspaceImage.removeAllViews();
                if (viewList.get(BACK) != null) {
                    for (TextOrder o : viewList.get(BACK).getTextOrders()) {
                        generateView(o);
                    }
                    for (ImagerOrder o : viewList.get(BACK).getImagerOrder()) {
                        generateImage(o);
                    }

                }
                selected = BACK;
                int myWidth = workspaceImage.getWidth();
                int myHeight = workspaceImage.getHeight();
                ProductDTO temp = (ProductDTO) spinnerProduct.getSelectedItem();
                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPathBack())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                //TODO set image depende on seleted area
                                workspaceImage.setBackground(ob);
                            }
                        });
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                front.setBackgroundColor(Color.TRANSPARENT);
                back.setBackgroundColor(Color.TRANSPARENT);
                right.setBackgroundColor(Color.TRANSPARENT);
                left.setBackgroundColor(getResources().getColor(R.color.com_facebook_likeview_text_color));

                viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));
                try {
                    createImage("" + selected);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                workspaceImage.removeAllViews();
                if (viewList.get(LEFT) != null) {
                    for (TextOrder o : viewList.get(LEFT).getTextOrders()) {
                        generateView(o);
                    }
                    for (ImagerOrder o : viewList.get(LEFT).getImagerOrder()) {
                        generateImage(o);
                    }

                }
                selected = LEFT;
                int myWidth = workspaceImage.getWidth();
                int myHeight = workspaceImage.getHeight();
                ProductDTO temp = (ProductDTO) spinnerProduct.getSelectedItem();
                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPathLeft())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                //TODO set image depende on seleted area
                                workspaceImage.setBackground(ob);
                            }
                        });
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                front.setBackgroundColor(Color.TRANSPARENT);
                back.setBackgroundColor(Color.TRANSPARENT);
                right.setBackgroundColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
                left.setBackgroundColor(Color.TRANSPARENT);

                viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));
                try {
                    createImage("" + selected);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                workspaceImage.removeAllViews();
                if (viewList.get(RIGHT) != null) {
                    for (TextOrder o : viewList.get(RIGHT).getTextOrders()) {
                        generateView(o);
                    }
                    for (ImagerOrder o : viewList.get(RIGHT).getImagerOrder()) {
                        generateImage(o);
                    }

                }
                selected = RIGHT;
                int myWidth = workspaceImage.getWidth();
                int myHeight = workspaceImage.getHeight();
                ProductDTO temp = (ProductDTO) spinnerProduct.getSelectedItem();
                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPathRight())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                //TODO set image depende on seleted area
                                workspaceImage.setBackground(ob);
                            }
                        });
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));

                    createImage("" + selected);
                    Intent i = new Intent(CustumShirtActivity.this, OrderRecapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ORDER_ID, orderID);
                    bundle.putSerializable(VIEW_SELECTED, viewList);
                    bundle.putString(PRODUCT_ID, ((ProductDTO) spinnerProduct.getSelectedItem()).getId());
                    i.putExtras(bundle);
                    startActivity(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ProductDTO temp = productList.get(position);


                int myWidth = workspaceImage.getWidth();
                int myHeight = workspaceImage.getHeight();

                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPath())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                workspaceImage.setBackground(ob);
                            }
                        });

                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPath())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(50, 50) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                front.setImageDrawable(ob);
                            }
                        });
                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPathBack())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(50, 50) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                back.setImageDrawable(ob);
                            }
                        });
                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPathLeft())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(50, 50) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                left.setImageDrawable(ob);
                            }
                        });
                Glide.with(CustumShirtActivity.this)
                        .load(ConstName.GET_LOGO + temp.getLogoPathRight())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(50, 50) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                right.setImageDrawable(ob);
                            }
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        sizePlusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected != null) {
                    imageSelected.getLayoutParams().width = imageSelected.getLayoutParams().width + 10;
                    imageSelected.getLayoutParams().height = imageSelected.getLayoutParams().height + 10;
                    imageSelected.requestLayout();
                    imageSelected.invalidate();
                }

                if (textSelected != null) {
                    textSelected.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelected.getTextSize() + 0.5F);

                }
            }
        });
        sizeMinusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected != null) {
                    imageSelected.getLayoutParams().width = imageSelected.getLayoutParams().width - 10;
                    imageSelected.getLayoutParams().height = imageSelected.getLayoutParams().height - 10;
                    imageSelected.requestLayout();
                    imageSelected.invalidate();
                }
                if (textSelected != null) {
                    textSelected.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelected.getTextSize() - 0.5F);
                }
            }
        });

        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView myTextView = new TextView(CustumShirtActivity.this);
                myTextView.setLayoutParams(mRparams);
                myTextView.setText("Change me");
                myTextView.setTextSize(20);

                workspaceImage.addView(myTextView);

                ///////////////

                myTextView.setOnTouchListener(new View.OnTouchListener()

                {

                    float dx = 0, dy = 0, x = 0, y = 0;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (textSelected != null) {
                            textSelected.setBackgroundResource(0);
                            textSelected.setPadding(0, 0, 0, 0);


                        }
                        myTextView.setBackgroundColor(getResources().getColor(R.color.black_overlay));
                        myTextView.setPadding(0, 0, 1, 0);
                        textSelected = myTextView;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {


                                x = event.getX();
                                y = event.getY();
                                dx = x - myTextView.getX();
                                dy = y - myTextView.getY();

                                break;

                            }

                            case MotionEvent.ACTION_MOVE:
                                dx = x - myTextView.getX();
                                dy = y - myTextView.getY();
                                float realX = event.getX() - dx;
                                float realY = event.getY() - dy;
                                myTextView.setX(realX);
                                myTextView.setY(realY);
                                break;

                            case MotionEvent.ACTION_UP:

                                // nbClik=0;
                        }
                        return true;

                    }
                });


                //////////////////////////


            }
        });


        workspaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected != null) {
                    imageSelected.setBackgroundResource(0);
                    imageSelected.setPadding(0, 0, 0, 0);
                    imageSelected = null;

                } else if (textSelected != null) {
                    textSelected.setBackgroundResource(0);
                    textSelected.setPadding(0, 0, 0, 0);
                    textSelected = null;
                }
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                //  if(!viewList.get(selected).getImagerOrder().isEmpty())
                viewList.get(selected).getImagerOrder().add(new ImagerOrder(getPath(CustumShirtActivity.this,selectedImageUri), 0, 0, 100, 100));


            }
        }
        regenerateView();


    }

    private void regenerateView() {
        if (selected == FRONT) {

            front.setBackgroundColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
            back.setBackgroundColor(Color.TRANSPARENT);
            right.setBackgroundColor(Color.TRANSPARENT);
            left.setBackgroundColor(Color.TRANSPARENT);
            if (viewList.get(FRONT) != null) {
                for (TextOrder o : viewList.get(FRONT).getTextOrders()) {
                    generateView(o);
                }
                for (ImagerOrder o : viewList.get(FRONT).getImagerOrder()) {
                    generateImage(o);
                }

            }
        } else if (selected == BACK) {

            front.setBackgroundColor(Color.TRANSPARENT);
            back.setBackgroundColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
            right.setBackgroundColor(Color.TRANSPARENT);
            left.setBackgroundColor(Color.TRANSPARENT);
            if (viewList.get(BACK) != null) {
                for (TextOrder o : viewList.get(BACK).getTextOrders()) {
                    generateView(o);
                }

                for (ImagerOrder o : viewList.get(BACK).getImagerOrder()) {
                    generateImage(o);
                }

            }

        } else if (selected == RIGHT) {

            front.setBackgroundColor(Color.TRANSPARENT);
            back.setBackgroundColor(Color.TRANSPARENT);
            right.setBackgroundColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
            left.setBackgroundColor(Color.TRANSPARENT);
            if (viewList.get(RIGHT) != null) {
                for (TextOrder o : viewList.get(RIGHT).getTextOrders()) {
                    generateView(o);
                }

                for (ImagerOrder o : viewList.get(RIGHT).getImagerOrder()) {
                    generateImage(o);
                }

            }

        } else if (selected == LEFT) {

            front.setBackgroundColor(Color.TRANSPARENT);
            back.setBackgroundColor(Color.TRANSPARENT);
            right.setBackgroundColor(Color.TRANSPARENT);
            left.setBackgroundColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
            if (viewList.get(LEFT) != null) {
                for (TextOrder o : viewList.get(LEFT).getTextOrders()) {
                    generateView(o);
                }

                for (ImagerOrder o : viewList.get(LEFT).getImagerOrder()) {
                    generateImage(o);
                }

            }

        }
    }


   /* @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));
        //viewList.put(BACK, new ViewOrder(getEditeText(), getImageOrder(), BACK));

        savedInstanceState.putSerializable(VIEW_SELECTED, viewList);
        savedInstanceState.putInt(SELECTED, selected);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy

        // Restore state members from saved instance
        viewList = (HashMap<Integer, ViewOrder>) savedInstanceState.getSerializable(VIEW_SELECTED);
        selected = savedInstanceState.getInt(SELECTED);
        Log.i("amine", "data restored"+viewList);

        super.onRestoreInstanceState(savedInstanceState);


    }*/

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }


        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

//    public String getPath(Uri uri) {
//        // just some safety built in
//        if (uri == null) {
//            // TODO perform some logging or show user feedback
//            return null;
//        }
//        // try to retrieve the image from the media store first
//        // this will only work for images selected from gallery
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        if (cursor != null) {
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        }
//        // this is our fallback here
//        return uri.getPath();
//    }

    public static final int BUFFER_SIZE = 1024 * 8;

    static void writeExternalToCache(Bitmap bitmap, File file) {
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

    }

    public void createImage(String fileID) throws IOException {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        workspaceImage.invalidate();
        Bitmap ddd = ImageUtil.getBitmapFromView(workspaceImage);
        writeExternalToCache(ddd, ImageUtil.createImageFile(orderID, fileID + "-screenshot", fullPath));


        // Bitmap bm = BitmapFactory.decodeFile(imagePath,options);
    }

    public void getLogos(String id) {

        final Activity activity = this;
        final ProgressDialog progressDialog = new ProgressDialog(CustumShirtActivity.this);

        final Request request = new Request.Builder()
                .url(ConstName.GET_LOGO_LIST + "?category=" + id)
                .build();

        // progressDialog.show();
        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == ConstName.OK) {
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<List<LogoDTO>>() {
                    }.getType();
                    logoList = gson.fromJson(response.body().charStream(), collectionType);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogoAdapter logoAdapter = new LogoAdapter(logoList);
                            listView.setAdapter(logoAdapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));

                                    viewList.get(selected).getImagerOrder().add(new ImagerOrder(logoList.get(position).getUuid(), 0, 0, 100, 100, true));

                                    workspaceImage.removeAllViews();
                                    regenerateView();
                                    dialog.hide();
                                }
                            });

                        }
                    });


                }
            }
        });


    }

    public void getTextColors() {

        final Activity activity = this;
        final ProgressDialog progressDialog = new ProgressDialog(CustumShirtActivity.this);

        final Request request = new Request.Builder()
                .url(ConstName.GET_TEXT_COLOR)
                .build();

        // progressDialog.show();
        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == ConstName.OK) {
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<List<TextColorDTO>>() {
                    }.getType();
                    textColorList = gson.fromJson(response.body().charStream(), collectionType);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextColorAdapter logoAdapter = new TextColorAdapter(textColorList);
                            dlgTextColor.setAdapter(logoAdapter);


                        }
                    });


                }
            }
        });


    }

    public void getCategories() {

        final Activity activity = this;
        final ProgressDialog progressDialog = new ProgressDialog(CustumShirtActivity.this);

        final Request request = new Request.Builder()
                .url(ConstName.GET_CATEGORY_LIST)
                .build();

        // progressDialog.show();
        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == ConstName.OK) {
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<List<CateogoryDTO>>() {
                    }.getType();
                    categoryList = gson.fromJson(response.body().charStream(), collectionType);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(categoryList);
                            categorySpinner.setAdapter(spinnerAdapter);

                            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    getLogos(categoryList.get(position).getId());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    viewList.put(selected, new ViewOrder(StoreViews.getEditeText(workspaceImage), StoreViews.getImageOrder(workspaceImage), selected));

                                    viewList.get(selected).getImagerOrder().add(new ImagerOrder(logoList.get(position).getUuid(), 0, 0, 100, 100, true));

                                    workspaceImage.removeAllViews();
                                    regenerateView();
                                    dialog.hide();
                                }
                            });*/

                        }
                    });


                }
            }
        });


    }

    public void getProducts() {

        final Activity activity = this;
        final ProgressDialog progressDialog = new ProgressDialog(CustumShirtActivity.this);

        final Request request = new Request.Builder()
                .url(ConstName.PRODUCT + "?type=" + productTypeID + "&size=" + size)
                .build();

        progressDialog.show();
        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustumShirtActivity.this, "Error", Toast.LENGTH_LONG).show();
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
                            ProductAdapter productAdapter = new ProductAdapter(productList, CustumShirtActivity.this);
                            spinnerProduct.setAdapter(productAdapter);
                            progressDialog.dismiss();
                            int myWidth = workspaceImage.getWidth();
                            int myHeight = workspaceImage.getHeight();
                            ProductDTO temp = productList.get(0);
                            Glide.with(CustumShirtActivity.this)
                                    .load(ConstName.GET_LOGO + temp.getLogoPath())
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                            //TODO set image depende on seleted area
                                            workspaceImage.setBackground(ob);
                                        }
                                    });

                            Glide.with(CustumShirtActivity.this)
                                    .load(ConstName.GET_LOGO + temp.getLogoPathBack())
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(50, 50) {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                            back.setImageDrawable(ob);
                                        }
                                    });

                            Glide.with(CustumShirtActivity.this)
                                    .load(ConstName.GET_LOGO + temp.getLogoPathBack())
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(50, 50) {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                            front.setImageDrawable(ob);
                                        }
                                    });
                            Glide.with(CustumShirtActivity.this)
                                    .load(ConstName.GET_LOGO + temp.getLogoPathLeft())
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(50, 50) {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                            left.setImageDrawable(ob);
                                        }
                                    });
                            Glide.with(CustumShirtActivity.this)
                                    .load(ConstName.GET_LOGO + temp.getLogoPathRight())
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(50, 50) {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                                            right.setImageDrawable(ob);
                                        }
                                    });

                        }
                    });


                }
            }
        });


    }


    public void generateImage(ImagerOrder imagerOrder) {
        RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final ImageView myImage = new ImageView(CustumShirtActivity.this);
        myImage.setLayoutParams(mRparams);
        myImage.setX(imagerOrder.getX());
        myImage.setY(imagerOrder.getY());
        if (imagerOrder.getLogo() == false) {
            Bitmap temp = ImageUtil.decodeSampledBitmapFromResource(imagerOrder.getUri(), imagerOrder.getWidth(), imagerOrder.getHeight());
            myImage.setImageBitmap(temp);
            myImage.setTag(R.string.isLogo, false);

        } else {
            Glide.with(CustumShirtActivity.this)
                    .load(ConstName.GET_LOGO + imagerOrder.getUri())
                    .into(myImage);
            myImage.setTag(R.string.isLogo, true);

        }
        myImage.setTag(R.string.logoUri, imagerOrder.getUri());
        myImage.getLayoutParams().width = imagerOrder.getWidth();
        myImage.getLayoutParams().height = imagerOrder.getHeight();
        //img.requestLayout();
        myImage.setOnTouchListener(new View.OnTouchListener() {
            float dx = 0, dy = 0, x = 0, y = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (imageSelected != null) {
                    imageSelected.setBackgroundResource(0);
                    imageSelected.setPadding(0, 0, 0, 0);


                }

                imageSelected = myImage;
                myImage.setBackgroundColor(getResources().getColor(R.color.black_overlay));
                myImage.setPadding(0, 0, 1, 0);


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        x = event.getX();
                        y = event.getY();
                        dx = x - myImage.getX();
                        dy = y - myImage.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = x - myImage.getX();
                        dy = y - myImage.getY();
                        realX = event.getX() - dx;
                        realY = event.getY() - dy;
                        myImage.setX(realX);
                        myImage.setY(realY);

                        break;
                    case MotionEvent.ACTION_UP: {
                        //your stuff

                        break;
                    }


                }
                return true;

            }

        });
        workspaceImage.addView(myImage);

    }

    public void generateView(TextOrder textOrder) {
        final RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView myTextView = new TextView(CustumShirtActivity.this);
        myTextView.setLayoutParams(mRparams);
        myTextView.setX(textOrder.getPositionX());
        myTextView.setY(textOrder.getPositionY());
        if (textOrder.getFontName() != null) {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + textOrder.getFontName());
            myTextView.setTypeface(typeface);
            myTextView.setTag(R.string.font_name, textOrder.getFontName());

        }
        myTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textOrder.getTextSize());
        myTextView.setText(textOrder.getValue());
        if (textOrder.getTextColor() != null) {
            myTextView.setTextColor(Color.parseColor(textOrder.getTextColor()));
            myTextView.setTag(R.string.color, textOrder.getTextColor());
        }


        workspaceImage.addView(myTextView);


        myTextView.setOnTouchListener(new View.OnTouchListener()

        {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float dx = 0, dy = 0, x = 0, y = 0;

                if (textSelected != null) {
                    textSelected.setBackgroundResource(0);
                    textSelected.setPadding(0, 0, 0, 0);


                }
                myTextView.setBackgroundColor(getResources().getColor(R.color.black_overlay));
                myTextView.setPadding(0, 0, 1, 0);
                textSelected = myTextView;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {


                        x = event.getX();
                        y = event.getY();
                        dx = x - myTextView.getX();
                        dy = y - myTextView.getY();

                        break;

                    }

                    case MotionEvent.ACTION_MOVE:
                        dx = x - myTextView.getX();
                        dy = y - myTextView.getY();
                        float realX = event.getX() - dx;
                        float realY = event.getY() - dy;
                        myTextView.setX(realX);
                        myTextView.setY(realY);
                        break;

                    case MotionEvent.ACTION_UP:

                        // nbClik=0;
                }


                return true;

            }
        });


    }


    class LogoAdapter extends ArrayAdapter<LogoDTO> {
        List<LogoDTO> items;

        LogoAdapter(List<LogoDTO> list) {
            super(CustumShirtActivity.this, R.layout.logo_adapter, list);
            items = new ArrayList<>(list);
            this.setNotifyOnChange(false);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.logo_adapter, parent, false);
            }
            final LogoDTO item = getItem(position);
            TextView tag = (TextView) row.findViewById(R.id.tag);
            ImageView logo = (ImageView) row.findViewById(R.id.logo);

            tag.setText(item.getTag());
            Glide.with(CustumShirtActivity.this)
                    .load(ConstName.GET_LOGO + item.getUuid())
                    .into(logo);


            return (row);
        }

    }


    class SpinnerAdapter extends ArrayAdapter<CateogoryDTO> {
        List<CateogoryDTO> items;

        SpinnerAdapter(List<CateogoryDTO> list) {
            super(CustumShirtActivity.this, R.layout.logo_adapter, list);
            items = new ArrayList<>(list);
            this.setNotifyOnChange(false);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.category_adapter, parent, false);
            }
            final CateogoryDTO item = getItem(position);
            TextView tag = (TextView) row.findViewById(R.id.name);

            tag.setText(item.getName());


            return (row);
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.category_adapter, parent, false);
            }
            final CateogoryDTO item = getItem(position);
            TextView tag = (TextView) row.findViewById(R.id.name);

            tag.setText(item.getName());

            return (row);
        }

    }


    class FontAdapter extends ArrayAdapter<String> {
        List<String> items;

        FontAdapter(List<String> list) {
            super(CustumShirtActivity.this, R.layout.logo_adapter, list);
            items = new ArrayList<>(list);
            this.setNotifyOnChange(false);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.category_adapter, parent, false);
            }
            final String item = getItem(position);
            TextView tag = (TextView) row.findViewById(R.id.name);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + item);
            tag.setText("M&D");
            tag.setTypeface(typeface);


            return (row);
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.category_adapter, parent, false);
            }
            final String item = getItem(position);
            TextView tag = (TextView) row.findViewById(R.id.name);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + item);
            tag.setText("M&D");
            tag.setTypeface(typeface);
            return (row);
        }

    }


    class TextColorAdapter extends ArrayAdapter<TextColorDTO> {
        List<TextColorDTO> items;

        TextColorAdapter(List<TextColorDTO> list) {
            super(CustumShirtActivity.this, R.layout.color_adapter, list);
            items = new ArrayList<>(list);
            this.setNotifyOnChange(false);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.color_adapter, parent, false);
            }
            final TextColorDTO item = getItem(position);
            TextView tag = (TextView) row.findViewById(R.id.tag);
            tag.setText(item.getName());
            tag.setTextColor(Color.parseColor(item.getValue()));

            return (row);
        }


        @Override
        public View getDropDownView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.color_adapter, parent, false);
            }
            final TextColorDTO item = getItem(position);
            TextView tag = (TextView) row.findViewById(R.id.tag);
            tag.setText(item.getName());
            tag.setTextColor(Color.parseColor(item.getValue()));

            return (row);
        }


    }

}





