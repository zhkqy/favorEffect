package com.example.guxiuzhong.favoreffect.surfaceview;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;

/**
 * Created by chenlei on 2016/5/11.
 */
public class HeartViewModel {

    private String name = "";
    private SoftReference<Bitmap> softBitmap =null;


    public HeartViewModel(String name, SoftReference<Bitmap> softBitmap) {
        this.name = name;
        this.softBitmap = softBitmap;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SoftReference<Bitmap> getSoftBitmap() {
        return softBitmap;
    }

    public void setSoftBitmap(SoftReference<Bitmap> softBitmap) {
        this.softBitmap = softBitmap;
    }
}
