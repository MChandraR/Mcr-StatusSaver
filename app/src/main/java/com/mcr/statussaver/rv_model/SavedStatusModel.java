package com.mcr.statussaver.rv_model;

import android.graphics.Bitmap;

import java.io.File;

public class SavedStatusModel {
    private String filename,type;
    private File file;
    private Bitmap bitmap;

    public SavedStatusModel(String fname,File fil,String typ){
        filename = fname;
        file = fil;
        type = typ;
    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return file;
    }

    public String getType() {
        return type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
