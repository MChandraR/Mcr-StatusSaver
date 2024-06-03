package com.mcr.statussaver.rv_adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mcr.statussaver.R;
import com.mcr.statussaver.app.Core;
import com.mcr.statussaver.rv_model.SavedStatusModel;
import com.mcr.statussaver.ui.SecondFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SavedStatusAdapter extends RecyclerView.Adapter<SavedStatusAdapter.SavedStatusHolder> {
    private ArrayList<SavedStatusModel> data;
    private Context context;
    private SecondFragment SF;
    private LinearLayout[] MenuArea;
    private Core appCore;
    private View parentView;
    private File savedDir;
    private SavedStatusAdapter.SavedStatusHolder editHolder;


    public SavedStatusAdapter(ArrayList<SavedStatusModel> resData,Context ctx,SecondFragment Sf,View pview,File Sdir){
        data = resData;
        context = ctx;
        appCore = new Core();
        SF = Sf;
        MenuArea = new LinearLayout[data.size()];
        parentView = pview;
        savedDir = Sdir;
    }

    public SavedStatusAdapter getAdapter(){
        return this;
    }

    @NonNull
    @Override
    public SavedStatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.savedstatusmainview,null);
        SavedStatusHolder v = new SavedStatusHolder(view);
        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull SavedStatusHolder holder, int position) {
        SavedStatusModel cdata = data.get(position);
        holder.setIsRecyclable(false);
        int index = holder.getAdapterPosition();

        MenuArea[holder.getAdapterPosition()] = holder.Menu;
        Glide.with(context).asBitmap().load(Uri.fromFile(cdata.getFile())).override(100,100).into(holder.FileThumb);
        holder.FileName.setText(cdata.getFilename());
        if(cdata.getType().equals(".mp4")){
            holder.FileType.setText("Tipe : Video ");
        }else{
            holder.FileType.setText("Tipe : Gambar");
        }
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.FileArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckMenu(holder.Menu,index);
                appCore.openFile(cdata.getFile(),context,parentView);
            }
        });

        holder.FileDate.setText("Date : " + formater.format(cdata.getFile().lastModified()));
        holder.MenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.Menu.getVisibility()==View.GONE){
                    holder.Menu.setVisibility(View.VISIBLE);
                    CheckMenu(holder.Menu,index);
                }else{
                    holder.Menu.setVisibility(View.GONE);
                }
            }
        });
        holder.DeleteFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.setIsRecyclable(true);
                deleteFile(cdata.getFile(), holder.FileArea,holder.getAdapterPosition());
            }
        });
        holder.ShareFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appCore.shareFile(cdata.getFile(),context,parentView);
            }
        });

        holder.EditFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.setIsRecyclable(true);
                editHolder = holder;
                SF.setUpFile(cdata.getFile(),savedDir,cdata.getType(),getAdapter(),holder.getAdapterPosition());
                holder.Menu.setVisibility(View.GONE);
            }
        });
        System.gc();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SavedStatusHolder extends  RecyclerView.ViewHolder{
        private RelativeLayout FileArea;
        private ImageButton MenuBtn;
        private LinearLayout Menu,DeleteFile,ShareFile,EditFile;
        private ImageView FileThumb;
        private TextView FileName,FileType,FileDate;
        public SavedStatusHolder(@NonNull View itemView) {
            super(itemView);
            EditFile = itemView.findViewById(R.id.SavedStatusMainViewEdit);
            DeleteFile = itemView.findViewById(R.id.SavedStatusMainViewDelete);
            ShareFile = itemView.findViewById(R.id.SavedStatusMainViewShare);
            MenuBtn = itemView.findViewById(R.id.SavedStatusMainViewMenuBtn);
            Menu = itemView.findViewById(R.id.SavedStatusMainViewMenu);
            FileArea = itemView.findViewById(R.id.SavedStatusMainViewFileArea);
            FileThumb = itemView.findViewById(R.id.SavedStatusMainViewImageView);
            FileName = itemView.findViewById(R.id.SavedStatusMainViewFileName);
            FileType = itemView.findViewById(R.id.SavedStatusMainViewFileType);
            FileDate = itemView.findViewById(R.id.SavedStatusMainViewFileDate);
        }
    }


    private void deleteFile(File file,RelativeLayout layout,int pos){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Hapus file ?");
        alert.setMessage("Apakah anda ingin menghapus file ini ?");
        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                file.delete();
                scanFile(Uri.fromFile(file));
                data.remove(pos);
                notifyItemRemoved(pos);
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
        context.sendBroadcast(scan);
    }

    private void CheckMenu(LinearLayout menu,int pos){
        for(int i = 0 ; i < data.size() ; i++){
            if(data.get(i)!=null){
                if(i != pos){
                    if(MenuArea[i]!=null){
                        MenuArea[i].setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public void changeFileOnResult(File file,int pos){
        SavedStatusModel ndata = new SavedStatusModel(file.getName(),file,appCore.getFileType(file));
        data.set(pos,ndata);
        notifyDataSetChanged();
        if(editHolder != null){
            editHolder.setIsRecyclable(false);
            editHolder = null;
        }
    }

}
