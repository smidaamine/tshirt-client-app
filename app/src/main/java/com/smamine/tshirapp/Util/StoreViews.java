package com.smamine.tshirapp.util;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smamine.tshirapp.R;
import com.smamine.tshirapp.model.ImagerOrder;
import com.smamine.tshirapp.model.TextOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aminedev on 1/19/16.
 */
public class StoreViews {
    public static List<TextOrder> getEditeText(RelativeLayout workspaceImage) {
        List<TextOrder> tempList = new ArrayList<>();
        for (int i = 0; i < workspaceImage.getChildCount(); i++)
            if (workspaceImage.getChildAt(i) instanceof TextView) {
                TextView temp = (TextView) workspaceImage.getChildAt(i);
                tempList.add(new TextOrder(String.valueOf(temp.getText()), "front", (String)temp.getTag(R.string.font_name), temp.getTextSize(), temp.getX(), temp.getY(),(String)temp.getTag(R.string.color)));
                //   workspaceImage.removeView(temp);
            }
        return tempList;
    }

    public static List<ImagerOrder> getImageOrder(RelativeLayout workspaceImage) {

        List<ImagerOrder> tempList = new ArrayList<>();
        for (int i = 0; i < workspaceImage.getChildCount(); i++)
            if (workspaceImage.getChildAt(i) instanceof ImageView) {
                ImageView temp = (ImageView) workspaceImage.getChildAt(i);
                tempList.add(new ImagerOrder((String) temp.getTag(R.string.logoUri)
                        , temp.getX()
                        , temp.getY()
                        , temp.getLayoutParams().width
                        , temp.getLayoutParams().height
                        ,(boolean)temp.getTag(R.string.isLogo)));
                //workspaceImage.removeView(temp);
            }
        return tempList;

    }

}
