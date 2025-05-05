package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MidiUploadActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_FILE = 100;
    private static final String TAG = "UploadMidiActivity";

    private Uri selectedFileUri;
    private String selectedFilePath;
    private Button btnSelectFile, btnUpload;
    private TextView tvFileName;
    private ProgressBar progressBar;
    private TextInputEditText etSongName, etTags;
    private RadioButton rbPublic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ✅ 设置内容铺满状态栏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // ✅ 状态栏设为透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_midi_upload);

        // 初始化视图
        btnSelectFile = findViewById(R.id.btn_select_file);
        btnUpload = findViewById(R.id.btn_upload);
        tvFileName = findViewById(R.id.tv_file_name);
        progressBar = findViewById(R.id.progress_bar);
        etSongName = findViewById(R.id.et_song_name);
        etTags = findViewById(R.id.et_tags);
        rbPublic = findViewById(R.id.rb_public);

        // 选择文件按钮点击事件
        btnSelectFile.setOnClickListener(v -> openFilePicker());

        // 上传按钮点击事件
        btnUpload.setOnClickListener(v -> {
            if (selectedFileUri == null) {
                Toast.makeText(this, "请先选择文件", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadFileToServer();
        });

//        // 添加以下代码
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getContentResolver().takePersistableUriPermission(
//                    selectedFileUri,
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION
//            );
//        }
    }

    // 打开文件选择器
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/midi"); // 限制为 MIDI 文件
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    // 处理文件选择结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            selectedFilePath = getPathFromUri(selectedFileUri); // 新增此行
            String fileName = getFileName(selectedFileUri);
            tvFileName.setText(fileName);
            btnUpload.setEnabled(true);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    // 安全获取列索引
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {  // 确保列存在
                        result = cursor.getString(nameIndex);
                    } else {
                        // 备用方案：从 URI 路径提取
                        result = uri.getLastPathSegment();
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    // 获取文件的真实路径（临时复制到缓存目录）
    private String getPathFromUri(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            File tempFile = File.createTempFile("upload_", ".mid", getCacheDir());
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4 * 1024]; // 4K buffer
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                return tempFile.getAbsolutePath();
            }
        } catch (Exception e) {
            Log.e(TAG, "File copy error", e);
            runOnUiThread(() ->
                    Toast.makeText(this, "文件处理失败", Toast.LENGTH_SHORT).show());
            return null;
        }
    }

    // 修改后的上传方法
    private void uploadFileToServer() {
        if (selectedFilePath == null) {
            Toast.makeText(this, "请先选择有效文件", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        new Thread(() -> {
            try {
                SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
                String userAccount = preferences.getString("userAccount", "-1");
                File file = new File(selectedFilePath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(),
                                RequestBody.create(file, MediaType.parse("audio/midi")))
                        .addFormDataPart("songName", etSongName.getText().toString())
                        .addFormDataPart("tags", etTags.getText().toString())
                        .addFormDataPart("userAccount", userAccount)
                        .addFormDataPart("isPublic", "true")

                        .build();

                Request request = new Request.Builder()
                        .url("http://115.29.178.160:3000/upload")
                        .post(requestBody)
                        .build();

                Response response = new OkHttpClient().newCall(request).execute();
                runOnUiThread(() -> handleResponse(response));
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(MidiUploadActivity.this,
                            "上传失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void handleResponse(Response response) {
        progressBar.setVisibility(View.GONE);
        btnUpload.setEnabled(true);

        if (response.isSuccessful()) {
            Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "服务器错误: " + response.code(),
                    Toast.LENGTH_SHORT).show();
        }
    }

//    // 上传文件到服务器
//    private void uploadFileToServer() {
//        String songName = etSongName.getText().toString().trim();
//        String tags = etTags.getText().toString().trim();
//        boolean isPublic = rbPublic.isChecked();
//
//        if (songName.isEmpty()) {
//            Toast.makeText(this, "请输入歌曲名称", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//        btnUpload.setEnabled(false);
//
//        // 构建请求体
//        File file = new File(selectedFilePath);
//        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/midi"), file);
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("midiFile", file.getName(), fileBody);
//
//        RequestBody songNameBody = RequestBody.create(MediaType.parse("text/plain"), songName);
//        RequestBody tagsBody = RequestBody.create(MediaType.parse("text/plain"), tags);
//        RequestBody isPublicBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isPublic));
//        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), "123"); // 替换为实际用户ID
//
//        MultipartBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addPart(filePart)
//                .addFormDataPart("songName", songName)
//                .addFormDataPart("tags", tags)
//                .addFormDataPart("isPublic", String.valueOf(isPublic))
//                .addFormDataPart("userId", "user123")
//                .build();
//
//        // 构建请求
//        Request request = new Request.Builder()
//                .url("http://115.29.178.160:3000/upload") // 本地测试用10.0.2.2
//                .post(requestBody)
//                .build();
//
//        // 发送请求
//        new OkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
//                    btnUpload.setEnabled(true);
//                    Toast.makeText(MidiUploadActivity.this, "上传失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
//                    btnUpload.setEnabled(true);
//                    if (response.isSuccessful()) {
//                        Toast.makeText(MidiUploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                        finish(); // 关闭当前Activity
//                    } else {
//                        Toast.makeText(MidiUploadActivity.this, "服务器错误: " + response.code(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
}