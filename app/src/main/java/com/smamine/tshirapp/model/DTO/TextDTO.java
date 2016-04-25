package com.smamine.tshirapp.model.DTO;

/**
 * Created by aminedev on 1/19/16.
 */
public class TextDTO {


    private String value;
    private int view;
    private String font;

    public TextDTO(String value, int view, String font) {
        this.value = value;
        this.view = view;
        this.font = font;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
