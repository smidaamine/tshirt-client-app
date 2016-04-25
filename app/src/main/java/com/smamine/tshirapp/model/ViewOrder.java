package com.smamine.tshirapp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aminedev on 1/17/16.
 */
public class ViewOrder implements Serializable {

    private List<TextOrder> textOrders;
    private List<ImagerOrder> imagerOrder;
    private int name ;

    public ViewOrder(List<TextOrder> textOrders, List<ImagerOrder> imagerOrder, int name) {
        this.textOrders = textOrders;
        this.imagerOrder = imagerOrder;
        this.name = name;
    }

    public List<TextOrder> getTextOrders() {
        return textOrders;
    }

    public void setTextOrders(List<TextOrder> textOrders) {
        this.textOrders = textOrders;
    }

    public List<ImagerOrder> getImagerOrder() {
        return imagerOrder;
    }

    public void setImagerOrder(List<ImagerOrder> imagerOrder) {
        this.imagerOrder = imagerOrder;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }
}
