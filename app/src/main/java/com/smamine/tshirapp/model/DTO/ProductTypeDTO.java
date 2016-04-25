package com.smamine.tshirapp.model.DTO;

import java.io.Serializable;

/**
 * Created by aminedev on 1/16/16.
 */
public class ProductTypeDTO implements Serializable {
    private String id ;
    private String name;
    private String logoPath;

    public ProductTypeDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
