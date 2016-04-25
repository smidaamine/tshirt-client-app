package com.smamine.tshirapp.model;

import com.smamine.tshirapp.model.DTO.ImageDTO;
import com.smamine.tshirapp.model.DTO.TextDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aminedev on 1/19/16.
 */
public class Order implements Serializable{
    String id;
    String uuid;
    int quantity;
    String productID;
    String fbID;

    List<ImageDTO> imagerOrderList;
    List<TextDTO> textOrders;


    public Order(List<TextDTO> textOrders, String uuid, List<ImageDTO> imagerOrderList,int quantity,String productID,String fbID) {
        this.textOrders = textOrders;
        this.uuid = uuid;
        this.imagerOrderList = imagerOrderList;
        this.quantity=quantity;
        this.productID=productID;
        this.fbID=fbID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<ImageDTO> getImagerOrderList() {
        return imagerOrderList;
    }

    public void setImagerOrderList(List<ImageDTO> imagerOrderList) {
        this.imagerOrderList = imagerOrderList;
    }

    public List<TextDTO> getTextOrders() {
        return textOrders;
    }

    public void setTextOrders(List<TextDTO> textOrders) {
        this.textOrders = textOrders;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getFbID() {
        return fbID;
    }

    public void setFbID(String fbID) {
        this.fbID = fbID;
    }
}
