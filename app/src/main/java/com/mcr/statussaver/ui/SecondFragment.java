package com.mcr.statussaver.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.mcr.statussaver.R;
import com.mcr.statussaver.app.Core;
import com.mcr.statussaver.databinding.FragmentSecondBinding;
import com.mcr.statussaver.rv_adapter.SavedStatusAdapter;
import com.mcr.statussaver.rv_model.SavedStatusModel;

import java.io.File;
import java.util.ArrayList;

public class SecondFragment extends Fragment {
    private FragmentSecondBinding binding;
    private Core appCore;
    private ArrayList<SavedStatusModel> reqData;
    private ArrayList<String> allowedType;
    private RecyclerView RV;
    private RecyclerView.Adapter RV_Adapter;
    private RecyclerView.LayoutManager RV_Layout;
    private SecondFragment FA;
    public View parentView;
    public TextInputEditText searchInput,editInput;
    private File[] fileList;
    private ArrayList<SavedStatusModel> searchData;
    private LinearLayout EditArea;
    private Button saveBtn,cancelBtn;
    private File cfile,cdir;
    private String cfiletype,FilterType;
    private SavedStatusAdapter cadapter;
    private int cpos,PicturesCount,VideosCount;
    private File savedDir;
    private TextView FilesDetail,PicuresDetail,VideosDetail;
    private CardView FileFilter,PictureFilter,VideoFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        RV = binding.SecondFragmentRecycleView;
        saveBtn = binding.SecondFragmentSaveBtn;
        cancelBtn = binding.SecondFragmentCancelBtn;
        parentView = binding.SecondFragmentParentView;
        editInput = binding.SEcondFragmentEditFilename;
        VideoFilter = binding.SecondFragmentVideoFilter;
        searchInput = binding.SecondFragmentSearchInput;
        PicuresDetail = binding.SecondFragmentFotoDetail;
        VideosDetail = binding.SecondFragmentVideoDetail;
        FileFilter = binding.SecondFragmentAllFielFilter;
        EditArea = binding.SEcondFragmentEditFilenameArea;
        FilesDetail = binding.SecondFragmentAllFileDetail;
        PictureFilter = binding.SecondFragmentPictureFilter;
        FA = this;

        appCore = new Core();
        reqData = new ArrayList<>();
        allowedType = new ArrayList<>();

        allowedType.add(".jpg");
        allowedType.add(".png");
        allowedType.add(".jpeg");
        allowedType.add(".mp4");
        PicturesCount = 0;
        VideosCount = 0;
        FilterType = "All";

        if(appCore.checkPermission(getContext())){
            getFileData(true);
        }

        addEvent();
        System.gc();
        checkFilter();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getFileData(boolean ChangeLayout){
        File rootDir = Environment.getExternalStorageDirectory();
        savedDir = new File(rootDir,appCore.getSavedDir());
        if(!savedDir.exists()){
            savedDir.mkdirs();
            savedDir.setReadable(true);
        }
        if(!savedDir.exists()){
            savedDir = new File(rootDir,appCore.getAltSavedDir());
            savedDir.mkdirs();
            savedDir.setReadable(true);
        }
        fileList = savedDir.listFiles();

        for(File file : fileList){
            String type = appCore.getFileType(file);
            for(String allowed : allowedType){
               if(type.equals(allowed)){
                   if(type.equals(".mp4")){
                       VideosCount += 1;
                   }else{ PicturesCount += 1; }
                   reqData.add(new SavedStatusModel(file.getName(),file,type));
                   break;
               }
            }
        }

        reqData = sortByDate(reqData);
        RV_Adapter = new SavedStatusAdapter(reqData,getContext(),FA,parentView,savedDir);
        RV_Layout = new LinearLayoutManager(getContext());
        RV.setAdapter(RV_Adapter);
        if(ChangeLayout){
            RV.setLayoutManager(RV_Layout);
        }else{
            RV.setLayoutManager(RV_Layout);
        }

        FilesDetail.setText("Semua file ("+reqData.size()+")");
        PicuresDetail.setText("Gambar ("+PicturesCount+")");
        VideosDetail.setText("Video ("+VideosCount+")");
        fileList = null;
        System.gc();
    }

    public void deleteFile(File file, RelativeLayout layout){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Hapus file ?");
        alert.setMessage("Apakah anda ingin menghapus file ini ?");
        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                file.delete();
                scanFile(Uri.fromFile(file));
                getFileData(false);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    private void scanFile(Uri uri){
        Intent scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scan.setData(uri);
        getContext().sendBroadcast(scan);
    }

    private void addEvent(){
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //No action
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //No action juga
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(searchInput.getText().toString().equals("")){
                    RV_Adapter = new SavedStatusAdapter(reqData,getContext(),FA,parentView,savedDir);
                    RV.setAdapter(RV_Adapter);
                }else{
                    searchFile(searchInput.getText().toString());
                }
            }
        });

        if(saveBtn != null){
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!editInput.getText().toString().equals("")){
                        if(new File(cdir,editInput.getText().toString()+cfiletype).exists()){
                            appCore.showSnackbar(getContext(),parentView,"Nama file sudah digunakan !");
                        }else{
                            changeFilename(editInput.getText().toString());
                        }
                    }else{
                        appCore.showSnackbar(getContext(),parentView,"Input tidak boleh kosong !");
                    }
                }
            });
        }

        if(cancelBtn != null){
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editInput.setText("");
                    EditArea.setVisibility(View.GONE);
                }
            });
        }

        if(FileFilter!=null){
            FileFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FilterType = "All";
                    searchFile(searchInput.getText().toString());
                }
            });
        }
        if(PictureFilter!=null){
            PictureFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FilterType = "Picture";
                    searchFile(searchInput.getText().toString());
                }
            });
        }
        if(VideoFilter!=null){
            VideoFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FilterType = "Video";
                    searchFile(searchInput.getText().toString());
                }
            });
        }

    }


    public void searchFile(String key){
        fileList = savedDir.listFiles();
        searchData = new ArrayList<>();
        for(File file : fileList){
            if(matchKey(file.getName(),key) || key.equals("")){
                if(FilterType.equals("All")){
                    searchData.add(new SavedStatusModel(file.getName(),file,appCore.getFileType(file)));
                    continue;
                }else if(FilterType.equals("Picture")){
                    if(appCore.getFileType(file).equals(".jpg")||appCore.getFileType(file).equals(".jpeg")||appCore.getFileType(file).equals(".webp")||appCore.getFileType(file).equals(".png")){
                        searchData.add(new SavedStatusModel(file.getName(),file,appCore.getFileType(file)));
                        continue;
                    }
                }else if(FilterType.equals("Video")){
                    if(appCore.getFileType(file).equals(".mp4")){
                        searchData.add(new SavedStatusModel(file.getName(),file,appCore.getFileType(file)));
                        continue;
                    }
                }
            }
        }
        searchData = sortByDate(searchData);
        RV_Adapter = new SavedStatusAdapter(searchData,getContext(),FA,parentView,savedDir);
        RV.setAdapter(RV_Adapter);
        searchData = null;
        fileList = null;
        checkFilter();
        System.gc();
    }

    private boolean matchKey(String target, String key){
        for(int i = 0 ; i < target.length()-key.length()+1 ; i++){
            if(target.substring(i,i+key.length()).equalsIgnoreCase(key)){
                return true;
            }
        }
        return false;
    }

    public void setUpFile(File file, File dir, String type,SavedStatusAdapter Adapter, int pos){
        cfile  = file;
        cdir = dir;
        cfiletype = type;
        cadapter = Adapter;
        cpos = pos;
        EditArea.setVisibility(View.VISIBLE);
    }

    public void changeFilename(String name){
        if(cfile != null && cdir != null){
            try{
                cdir.mkdirs();
                File newFile = new File(cdir,name+cfiletype);
                cfile.renameTo(newFile);
                newFile.setLastModified(System.currentTimeMillis());
                cadapter.changeFileOnResult(newFile,cpos);
                scanFile(Uri.fromFile(newFile));
                scanFile(Uri.fromFile(cfile));
                EditArea.setVisibility(View.GONE);
                editInput.setText("");
            }catch (Exception e){

            }
        }
    }

    private void checkFilter(){
        if(FilterType.equals("All")){
            FileFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.light_green)));
            PictureFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
            VideoFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
        }else if(FilterType.equals("Picture")){
            FileFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
            PictureFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.light_green)));
            VideoFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
        }else if(FilterType.equals("Video")){
            FileFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
            PictureFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
            VideoFilter.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.light_green)));
        }
    }

    private ArrayList<SavedStatusModel> sortByDate(ArrayList<SavedStatusModel> data){
        ArrayList<SavedStatusModel> sorted = new ArrayList<>();
        int size = data.size();
        int lastindex = 0 ;
        while (sorted.size()<size){
            lastindex = 0;
            if(data.size()>1){
                for(int i = 0 ; i < data.size()-1 ; i++){
                    if(data.get(i+1).getFile().lastModified()>data.get(lastindex).getFile().lastModified()){
                        lastindex = i+1;
                    }
                }
            }else{
                lastindex = 0;
            }
            sorted.add(data.get(lastindex));
            data.remove(lastindex);
        }
        System.gc();
        return sorted;
    }

}