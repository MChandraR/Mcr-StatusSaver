package com.mcr.statussaver.ui;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcr.statussaver.app.Core;
import com.mcr.statussaver.databinding.FragmentFirstBinding;
import com.mcr.statussaver.rv_adapter.StatusAdapter;
import com.mcr.statussaver.rv_model.StatusModel;

import java.io.File;
import java.util.ArrayList;

public class FirstFragmentBackup extends Fragment {
    private FragmentFirstBinding binding;
    private File rootPath;
    private Core appCore;
    private View parentView;
    private TextView mainTextView;
    private RecyclerView RV;
    private RecyclerView.Adapter RV_Adapter;
    private RecyclerView.LayoutManager RV_LayoutManager;
    private ArrayList<StatusModel> reqData;
    private ArrayList<String> typeAllowed;
    private TextView TotalStatus,TotalFoto,TotalVideo;
    private int JmlhFoto,JmlhVideo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        //checkFolder();

        appCore = new Core();
        parentView = binding.FirstFragmentViewParent;
        RV = binding.FirstFragmentRecycleView;
        TotalFoto = binding.FirsFragmentTotalFoto;
        TotalVideo = binding.FirstFragmentTotalVideo;
        TotalStatus = binding.FirsFragmentTotalStatus;

        reqData = new ArrayList<>();
        typeAllowed = new ArrayList<>();
        typeAllowed.add(".jpg");
        typeAllowed.add(".png");
        typeAllowed.add(".jpeg");
        typeAllowed.add(".mp4");
        JmlhFoto = 0 ;
        JmlhVideo = 0;

        /*Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        };
        handler.postDelayed(runnable,500);*/

        if(appCore.checkPermission(getContext())){
            loadData();
        }

        System.gc();
        return view;
    }

    private void loadData(){
        rootPath = Environment.getExternalStorageDirectory();
        File whatsAppFolder = new File(rootPath,"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses");
        //whatsAppFolder.mkdir();
        if(!whatsAppFolder.exists()){
            whatsAppFolder = new File(rootPath,"/WhatsApp/Media/.Statuses");
            //whatsAppFolder.mkdir();
        }
        if(!whatsAppFolder.exists()){
            appCore.showSnackbar(getContext(),parentView,"Error :Tidak dapat menemukan folder whatsapp !");
        }
        ContentResolver CR = getContext().getContentResolver();

        File[] fileList = whatsAppFolder.listFiles();
        for(File file : fileList){
            File reqfile = new File(whatsAppFolder,file.getName());
            String type = appCore.getFileType(reqfile);
            for(String tipe : typeAllowed){
                if(tipe.equals(type) && file.length() >= 1024){
                    if(type.equals(".mp4")){
                        JmlhVideo += 1;
                    }else{JmlhFoto += 1;}
                    reqData.add(new StatusModel(file.getName(),reqfile,type));
                    break;
                }
            }
        }

        RV_Adapter = new StatusAdapter(reqData,getContext(),parentView);
        RV_LayoutManager = new GridLayoutManager(getContext(),3);
        RV.setAdapter(RV_Adapter);
        RV.setLayoutManager(RV_LayoutManager);

        TotalStatus.setText("Status Dilihat (" + reqData.size() + ")");
        TotalFoto.setText("Foto (" + JmlhFoto + ")");
        TotalVideo.setText("Video (" + JmlhVideo + ")");
        reqData = null;
        whatsAppFolder = null;
        fileList = null;
    }

}