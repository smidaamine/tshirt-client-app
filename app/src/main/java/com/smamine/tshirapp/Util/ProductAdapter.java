package com.smamine.tshirapp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smamine.tshirapp.R;
import com.smamine.tshirapp.model.DTO.ProductDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aminedev on 1/19/16.
 */
public class ProductAdapter extends ArrayAdapter<ProductDTO> {
    List<ProductDTO> items;
    Context context;

    public ProductAdapter(List<ProductDTO> list,Context context) {
        super(context, R.layout.product_addp, list);
        items = new ArrayList<ProductDTO>(list);
        this.context=context;
        this.setNotifyOnChange(false);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(R.layout.product_addp_mini, parent, false);

        final ProductDTO item = getItem(position);
        ((RelativeLayout) row.findViewById(R.id.relative)).
                setBackgroundColor(Color.parseColor(item.getColor()));


        return (row);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(R.layout.product_addp, parent, false);

        final ProductDTO item = getItem(position);
        TextView size = (TextView) row.findViewById(R.id.size);
        size.setText("" + item.getSize());
        row.setBackgroundColor(Color.parseColor(item.getColor()));


        return (row);
    }
}