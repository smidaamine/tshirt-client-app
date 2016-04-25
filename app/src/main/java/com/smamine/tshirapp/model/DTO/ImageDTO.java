package com.smamine.tshirapp.model.DTO;

/**
 * Created by aminedev on 1/19/16.
 */
public class ImageDTO {


    private String imagePath;
    private String uuid;
    private int view ;
    Boolean logo;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public ImageDTO(String imagePath, String uuid, int view,boolean logo) {
        this.imagePath = imagePath;
        this.uuid = uuid;
        this.view = view;
        this.logo = logo;
    }

    public Boolean getLogo() {
        return logo;
    }

    public void setLogo(Boolean logo) {
        this.logo = logo;
    }
}
