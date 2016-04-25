package com.smamine.tshirapp.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by aminedev on 1/17/16.
 */
public class ImagerOrder implements Serializable{
    private String uri;
    private float x;
    private float y;
    private int width;
    private int height;
    private String uuid;
    private Boolean logo =false;



    public ImagerOrder(String uri, float x, float y, int width, int height) {
        this.uri = uri;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.uuid = UUID.randomUUID().toString();
    }

    public ImagerOrder(String uri, float x, float y, int width, int height,boolean logo) {
        this.uri = uri;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.uuid = UUID.randomUUID().toString();
        this.logo=logo;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }


    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getLogo() {
        return logo;
    }

    public void setLogo(Boolean logo) {
        this.logo = logo;
    }
}
