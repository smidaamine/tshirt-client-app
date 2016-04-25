package com.smamine.tshirapp.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by aminedev on 1/17/16.
 */
public class TextOrder implements Serializable {

    private String value;
    private String positionOnTishirt;
    private String fontName;
    private float TextSize;
    private float positionX;
    private float positionY;
    private String textColor;

    private String uuid;

    public TextOrder(String value, String positionOnTishirt, String fontName, float textSize, float positionX, float positionY , String textColor) {
        this.value = value;
        this.positionOnTishirt = positionOnTishirt;
        this.fontName = fontName;
        TextSize = textSize;
        this.positionX = positionX;
        this.positionY = positionY;
        this.uuid = UUID.randomUUID().toString();
        this.textColor = textColor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPositionOnTishirt() {
        return positionOnTishirt;
    }

    public void setPositionOnTishirt(String positionOnTishirt) {
        this.positionOnTishirt = positionOnTishirt;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public float getTextSize() {
        return TextSize;
    }

    public void setTextSize(float textSize) {
        TextSize = textSize;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
