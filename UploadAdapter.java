package com.example.music.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;

import java.util.List;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {
    private List<UploadFile> uploads;

    public UploadAdapter(List<UploadFile> uploads) {
        this.uploads = uploads;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvFileName, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    @NonNull
    @Override
    public UploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload_file, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UploadFile file = uploads.get(position);
        holder.tvSongName.setText(file.getSong_name());
        holder.tvFileName.setText("文件名: " + file.getOriginal_filename());
        holder.tvDate.setText("上传时间: " + file.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }
}
