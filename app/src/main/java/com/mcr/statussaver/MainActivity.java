package com.mcr.statussaver;

import android.Manifest;
import android.content.ContentResolver;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mcr.statussaver.app.Core;
import com.mcr.statussaver.rv_adapter.StatusAdapter;
import com.mcr.statussaver.rv_model.StatusModel;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private File rootPath;
    private Core appCore;
    private View parentView;
    private TextView mainTextView;
    private RecyclerView RV;
    private RecyclerView.Adapter RV_Adapter;
    private RecyclerView.LayoutManager RV_LayoutManager;
    private ArrayList<StatusModel> reqData;
    private ArrayList<String> typeAllowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checkFolder();


        //Ngga digunain lagi

        appCore = new Core();
        parentView = findViewById(R.id.MainActivityParentView);
        RV = findViewById(R.id.MainActivityRecycleView);
        parentView = findViewById(R.id.MainActivityParentView);

        reqData = new ArrayList<>();
        typeAllowed = new ArrayList<>();
        typeAllowed.add(".jpg");
        typeAllowed.add(".png");
        typeAllowed.add(".jpeg");
        typeAllowed.add(".mp4");

        checkPermission();

        rootPath = Environment.getExternalStorageDirectory();
        File whatsAppFolder = new File(rootPath,"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses");
        whatsAppFolder.mkdir();
        if(!whatsAppFolder.exists()){
            whatsAppFolder = new File(rootPath,"/WhatsApp/Media/.Statuses");
            appCore.showSnackbar(this,parentView,"Folder tidak ditemukan , mencari ulang !");
        }
        ContentResolver CR = getContentResolver();

        File[] fileList = whatsAppFolder.listFiles();
        for(File file : fileList){
            File reqfile = new File(whatsAppFolder,file.getName());
            String type = appCore.getFileType(reqfile);
            for(String tipe : typeAllowed){
                if(tipe.equals(type) && file.length() >= 1024){
                    reqData.add(new StatusModel(file.getName(),reqfile,type));
                    break;
                }
            }
        }

        RV_Adapter = new StatusAdapter(reqData,this,parentView);
        RV_LayoutManager = new LinearLayoutManager(this);
        RV.setAdapter(RV_Adapter);
        RV.setLayoutManager(RV_LayoutManager);

    }

    private void checkPermission(){
        int permision1 = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permision2 = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permision1 != 1 || permision2 != 1){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
        }
    }

    private void checkFolder(){
        /*File rootDir = Environment.getExternalStorageDirectory();

        File whatsAppDir = new File(rootDir,"/DCIM/WhatsApp Status");
        if(!whatsAppDir.exists()){
            whatsAppDir.mkdirs();
            whatsAppDir.setReadable(true);
            whatsAppDir.setWritable(true);
        }
        if(!whatsAppDir.exists()){
            whatsAppDir = new File(rootDir,"/DCIM/Status");
            whatsAppDir.mkdirs();
        }*/

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}