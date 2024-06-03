package com.mcr.statussaver.app;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import com.mcr.statussaver.BuildConfig;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class Core {

    public void showSnackbar(Context context, View view, String message){
        Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
    }

    public String getSavedDir(){
        return "/DCIM/WhatsApp Status";
    }

    public String getAltSavedDir(){
        return "/DCIM/Status";
    }

    public String getPackagesName(){
        return "com.mcr.statussaver";
    }

    public String getFileType(File file){
        String filename = file.getName();
        int index = 0;
        for(int i = filename.length() -1 ; i>0 ; i--){
            if(filename.charAt(i) == (char)'.'){
                index = i;
                break;
            }
        }

        return filename.substring(index);
    }

    public void saveStatus(File file, Context context, View parentView,String filename){
        file.setReadable(true);
        File rootDir = Environment.getExternalStorageDirectory();

        File whatsAppDir = new File(rootDir,"/DCIM/WhatsApp Status");
        String locatio = "/DCIM/WhatsApp Status";
        if(!whatsAppDir.exists()){
            whatsAppDir.mkdirs();
        }
        if(!whatsAppDir.exists()){
            locatio = "/DCIM/Status";
            whatsAppDir = new File(rootDir,"/DCIM/Status");
            whatsAppDir.mkdirs();
        }
        whatsAppDir.setReadable(true);
        //Cek apakah ada file nomedia agar folder tidak hidden
        File nomedia = new File(whatsAppDir,".nomedia");
        if(nomedia.exists()){
            nomedia.delete();
        }

        File newfile  = new File(whatsAppDir,filename);
        newfile.setReadable(true);
        FileInputStream IS  ;
        try {
            IS = new FileInputStream(file);
            FileOutputStream OS ;
            try {
                OS = new FileOutputStream(newfile);
                byte[] buff = new byte[1024];
                IS.read(buff);
                do{
                    OS.write(buff);
                }while(IS.read(buff) != -1);
                OS.flush();
                OS.close();

                showSnackbar(context,parentView,"Status telah disimpan ke "+locatio);
            }catch (Exception e){
                showSnackbar(context,parentView,"Tidak dapat mengcopy file !");
            }
            try{
                IS.close();
            }catch (Exception e){}
        }catch (Exception e){
            showSnackbar(context,parentView,"Tidak dapat mengcopy file !");
        }

        //Scan file setelah ditambah agar dideteksi sistem
        Intent scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(newfile);
        scan.setData(uri);
        context.sendBroadcast(scan);

    }



    public boolean checkPermission(Context context){
        int permision1 = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permision2 = ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permision1 != -1 && permision2 != -1){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkPermissionR(Context context){
        boolean permission = Environment.isExternalStorageManager();
        if(permission){
            return true;
        }else{ return false; }
    }

    public void openFile(File file,Context context,View parentv){
        try{
            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context), BuildConfig.APPLICATION_ID+".provider",file);
            String mimeType = context.getContentResolver().getType(uri);
            Intent open = new Intent(Intent.ACTION_VIEW);
            open.setDataAndType(uri,mimeType);
            open.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(open);
        }catch (Exception e){
            showSnackbar(context,parentv,"Tidak dapat membuka file !");
        }
    }

    public void shareFile(File file,Context context,View parentv){
        try{
            Intent share = new Intent(Intent.ACTION_SEND);
            //Uri uri = Uri.fromFile(file);
            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context),BuildConfig.APPLICATION_ID+".provider",file);
            String type = context.getContentResolver().getType(uri);
            share.setType(type);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.putExtra(Intent.EXTRA_STREAM,uri);
            context.startActivity(Intent.createChooser(share,"Bagikan dengan"));
        }catch (Exception e){
            showSnackbar(context,parentv,"Tidak dapat membagikan file !");
        }

    }

}
