package com.thepyramid.appslocker.common.Models;

/**
 * Created by samar ezz on 8/24/2017.
 */
public class LockedApp {


    private String name;
    private byte[] image;

    public LockedApp() {
    }

    public LockedApp(String name,byte[] image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[]  getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
