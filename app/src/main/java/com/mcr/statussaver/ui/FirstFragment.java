package com.mcr.statussaver.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcr.statussaver.app.Core;
import com.mcr.statussaver.databinding.FragmentFirstBinding;
import com.mcr.statussaver.rv_adapter.StatusAdapter;
import com.mcr.statussaver.rv_model.StatusModel;

import java.io.File;
import java.util.ArrayList;

public class FirstFragment extends Fragment {
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
    private long LastModDir1,LastModDir2;
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
        LastModDir1 = 0;
        LastModDir2 = 0 ;

        if(appCore.checkPermission(getContext())){
            loadData();
        }

        System.gc();
        return view;
    }

    private void loadData(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            rootPath = Environment.getExternalStorageDirectory();
        }else{
            rootPath = Environment.getExternalStorageDirectory();
        }
        File whatsAppFolder = null;
        File whatsAppFolder1 = new File(rootPath,"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses");
        File whatsAppFolder2 = new File(rootPath,"/WhatsApp/Media/.Statuses");
        if(whatsAppFolder1.exists()){
            LastModDir1 = whatsAppFolder1.lastModified();
        }if(whatsAppFolder2.exists()){
            LastModDir2 = whatsAppFolder2.lastModified();
        }

        if(LastModDir1>LastModDir2){
            whatsAppFolder =whatsAppFolder1;
        }else{
            whatsAppFolder =whatsAppFolder2;
        }

        if(!whatsAppFolder.exists()){
            appCore.showSnackbar(getContext(),parentView,"Error :Tidak dapat menemukan folder whatsapp ! \n"+whatsAppFolder.getAbsolutePath());
        }else{
            File[] fileList = whatsAppFolder.listFiles();
            if(fileList.length > 0){
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
                fileList = null;
                whatsAppFolder = null;
                System.gc();
            }
        }
    }

}