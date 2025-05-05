package com.example.music.Intent;

public class UploadFile {

        public int file_id;
        public String song_name;
        public String original_filename;
        public String cover_image_path;
        public String description;
        public String download_url;
        public int play_count;
        public int favorite_count;
        public int like_count;
        public int duration;
        public int bitrate;
        public String file_extension;
        public String upload_time;


    // Getter 方法
    public String getOriginal_filename() { return original_filename; }
    public String getSong_name() { return song_name; }
//    public boolean isIs_public() { return is_public; }
    public String getDownload_url() { return download_url; }
//    public String getCreated_at() { return created_at; }
    public String getCreated_at() { return upload_time; }
}

