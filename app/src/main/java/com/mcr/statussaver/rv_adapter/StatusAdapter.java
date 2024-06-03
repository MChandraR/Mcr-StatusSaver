package com.mcr.statussaver.rv_adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mcr.statussaver.R;
import com.mcr.statussaver.app.Core;
import com.mcr.statussaver.rv_model.StatusModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusAdapterHolder> {
    private final ArrayList<StatusModel> data;
    private final Context context;
    private final View parentView;
    private final Core appCore;

    public StatusAdapter(ArrayList<StatusModel> resData, Context cont, View pv){
        data = resData;
        context = cont;
        parentView = pv;
        appCore = new Core();
    }

    @NonNull
    @Override
    public StatusAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statusmainview,parent,false);
        return new StatusAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapterHolder holder, int position) {
        StatusModel cdata = data.get(position);
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss"+position).format(Calendar.getInstance().getTime());
        holder.FileName.setText(cdata.getFilename());
        Glide.with(context).asBitmap().load(Uri.fromFile(cdata.getFile())).override(200,200).into(holder.FileThumbnail);
        holder.SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appCore.saveStatus(cdata.getFile(),context,parentView,date+cdata.getType());
            }
        });
        holder.FileThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appCore.openFile(cdata.getFile(),context,parentView);
            }
        });

        if(cdata.getType().equals(".mp4")){
            holder.TipeFile.setText("Video");
        }else{
            holder.TipeFile.setText("Gambar");
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class StatusAdapterHolder extends RecyclerView.ViewHolder{
        private TextView FileName,TipeFile;
        private ConstraintLayout StatusArea;
        private ImageView FileThumbnail;
        private ImageButton SaveBtn;
        public StatusAdapterHolder(View itemView) {
            super(itemView);
            StatusArea = itemView.findViewById(R.id.StatusMainViewStatusArea);
            FileName = itemView.findViewById(R.id.FileName);
            TipeFile = itemView.findViewById(R.id.StatusMainViewTipeFile);
            FileThumbnail = itemView.findViewById(R.id.imageView);
            SaveBtn = itemView.findViewById(R.id.SaveBtn);
        }
    }

}
