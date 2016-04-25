package com.smamine.tshirapp.model.DTO;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aminedev on 2/20/16.
 */
public class OrderResult implements Serializable {

    private String id;
    private ProductDTO product;

    private int quantity;
    private String status;
    private Date deliverDate;
    private Date creationDate;

    private Long ref;
    private double total;
    private  String canceledFor;
    private  String payment;

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public Long getRef() {
        return ref;
    }

    public void setRef(Long ref) {
        this.ref = ref;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCanceledFor() {
        return canceledFor;
    }

    public void setCanceledFor(String canceledFor) {
        this.canceledFor = canceledFor;
    }
}
