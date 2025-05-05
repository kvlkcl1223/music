package com.example.music;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.example.music.MusicService;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.music.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // 如果上次崩溃，清理
        if (prefs.getBoolean("crash_last_time", false)) {
            prefs.edit().clear().apply();  // 清除所有缓存
        }

        // 启动时打上“可能崩”的标记
        prefs.edit().putBoolean("crash_last_time", true).apply();


        super.onCreate(savedInstanceState);

        // ✅ 设置内容铺满状态栏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // ✅ 状态栏设为透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_main);
        showUserAgreementDialog();
        // 启动 MusicService
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止 MusicService
//        Intent intent = new Intent(this, MusicService.class);
//        stopService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //goNextPage(); // 跳到下个页面
    }

    // 跳到下个页面
    private void goNextPage() {
        TextView tv_hello = findViewById(R.id.tv_hello);
        tv_hello.setText("欢迎来到你的世界");
        // 延迟3秒（3000毫秒）后启动任务mGoNext
        new Handler(Looper.myLooper()).postDelayed(mGoNext, 1000);
    }

    private Runnable mGoNext = new Runnable() {
        @Override
        public void run() {
            // 活动页面跳转，从MainActivity跳到
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            //startActivity(new Intent(MainActivity.this, MidiUploadActivity.class));
            //startActivity(new Intent(MainActivity.this, MidiPlayerActivity.class));
            startActivity(new Intent(MainActivity.this, PublicUploadsActivity.class));
            finish();
        }
    };
    private void showUserAgreementDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.user_agreement_title) // 设置标题
                .setMessage(R.string.user_agreement_message) // 设置内容
                .setPositiveButton(R.string.agree, (dialog, which) -> {
                    // 跳到下个页面
                    goNextPage();
                    // 用户点击“同意”
                    // 可以在这里执行一些逻辑，例如记录用户已同意协议
                })
                .setNegativeButton(R.string.disagree, (dialog, which) -> {
                    // 用户点击“不同意”
                    finish(); // 关闭应用
                })
                .setCancelable(false) // 禁止用户点击弹窗外关闭弹窗
                .show();
    }
}

//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.File;
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int FILE_SELECT_CODE = 1;
//    private Button btnSelectFile, btnUpload;
//    private TextView tvSelectedFile, tvUploadStatus;
//    private ProgressBar progressBar;
//    private Uri selectedFileUri;
//    private String selectedFilePath;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        btnSelectFile = findViewById(R.id.btnSelectFile);
//        btnUpload = findViewById(R.id.btnUpload);
//        tvSelectedFile = findViewById(R.id.tvSelectedFile);
//        tvUploadStatus = findViewById(R.id.tvUploadStatus);
//        progressBar = findViewById(R.id.progressBar);
//
//        btnSelectFile.setOnClickListener(v -> selectFile());
//        btnUpload.setOnClickListener(v -> uploadFile());
//    }
//
//    private void selectFile() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");  // 所有文件类型
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, FILE_SELECT_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                selectedFileUri = data.getData();
//                selectedFilePath = FileUtils.getPath(this, selectedFileUri);
//
//                if (selectedFilePath != null) {
//                    tvSelectedFile.setText("已选择: " + selectedFilePath);
//                    btnUpload.setEnabled(true);
//                } else {
//                    tvSelectedFile.setText("无法获取文件路径");
//                    btnUpload.setEnabled(false);
//                }
//            }
//        }
//    }
//
//    private void uploadFile() {
//        if (selectedFilePath == null) {
//            Toast.makeText(this, "请先选择文件", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        File file = new File(selectedFilePath);
//        if (!file.exists()) {
//            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//        tvUploadStatus.setText("上传中...");
//        FileUploader.uploadFile(file, "user123", true);

//        // 使用OkHttp进行文件上传
//        OkHttpClient client = new OkHttpClient();
//
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", file.getName(),
//                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
//                .build();
//
//        // 替换为你的服务器URL
//        Request request = new Request.Builder()
//                .url("http://47.97.87.59:3000/upload")
//                .post(requestBody)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
//                    tvUploadStatus.setText("上传失败: " + e.getMessage());
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String responseStr = response.body().string();
//                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
//                    if (response.isSuccessful()) {
//                        tvUploadStatus.setText("上传成功: " + responseStr);
//                    } else {
//                        tvUploadStatus.setText("上传失败: " + responseStr);
//                    }
//                });
//            }
//        });
//    }
//}





















//package com.example.music;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.documentfile.provider.DocumentFile;
//import org.apache.commons.io.FileUtils;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.ref.WeakReference;
//
//public class MainActivity extends AppCompatActivity {
//    private static final int PICK_MP3_FILE = 1;
//    private static final int PICK_SAVE_DIRECTORY = 2;
//    private Uri mp3Uri;
//    private String saveDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
//    private ProgressBar progressBar;
//    private TextView tvStatus;
//    private TextView tvSavePath;
//    private Button btnSelect;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        progressBar = findViewById(R.id.progress_bar);
//        tvStatus = findViewById(R.id.tv_status);
//        tvSavePath = findViewById(R.id.tv_save_path);
//        btnSelect = findViewById(R.id.btn_select);
//
//        tvSavePath.setText("保存位置: " + saveDirPath);
//        btnSelect.setOnClickListener(v -> selectMp3File());
//    }
//
//    private void selectMp3File() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("audio/mpeg");
//        startActivityForResult(intent, PICK_MP3_FILE);
//    }
//
//    private void selectSaveDirectory() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
//                Intent.FLAG_GRANT_READ_URI_PERMISSION |
//                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//        startActivityForResult(intent, PICK_SAVE_DIRECTORY);
//    }
//
//    private void showSaveLocationDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("选择保存位置")
//                .setMessage("要使用默认的音乐目录，还是选择其他位置？")
//                .setPositiveButton("选择目录", (dialog, which) -> selectSaveDirectory())
//                .setNegativeButton("使用默认目录", (dialog, which) -> {
//                    Toast.makeText(this, "将保存到默认目录: " + saveDirPath, Toast.LENGTH_SHORT).show();
//                    convertMP3ToMIDI(mp3Uri);
//                }).setNeutralButton("取消", null)
//                .show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && data != null) {
//            if (requestCode == PICK_MP3_FILE) {
//                mp3Uri = data.getData();
//                showSaveLocationDialog();
//            } else if (requestCode == PICK_SAVE_DIRECTORY) {
//                Uri treeUri = data.getData();
//                getContentResolver().takePersistableUriPermission(
//                        treeUri,
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION |
//                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                );
//
//                DocumentFile docFile = DocumentFile.fromTreeUri(this, treeUri);
//                if (docFile != null && docFile.canWrite()) {
//                    saveDirPath = docFile.getUri().toString();
//                    tvSavePath.setText("保存位置: " + docFile.getName());
//                    Toast.makeText(this, "已选择: " + docFile.getName(), Toast.LENGTH_SHORT).show();
//                    convertMP3ToMIDI(mp3Uri);
//                } else {
//                    Toast.makeText(this, "无法写入该目录", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//
//    public void convertMP3ToMIDI(Uri mp3Uri) {
//        new ConvertTask(this, mp3Uri, saveDirPath).execute();
//    }
//
//    private static class ConvertTask extends android.os.AsyncTask<Void, Void, File> {
//        private final WeakReference<MainActivity> activityReference;
//        private final Uri mp3Uri;
//        private final String saveDirPath;
//
//        ConvertTask(MainActivity activity, Uri mp3Uri, String saveDirPath) {
//            this.activityReference = new WeakReference<>(activity);
//            this.mp3Uri = mp3Uri;
//            this.saveDirPath = saveDirPath;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            MainActivity activity = activityReference.get();
//            if (activity != null) {
//                activity.progressBar.setVisibility(View.VISIBLE);
//                activity.tvStatus.setText("正在转换...");
//                activity.btnSelect.setEnabled(false);
//            }
//        }
//
//        @Override
//        protected File doInBackground(Void... voids) {
//            try {
//                MainActivity activity = activityReference.get();
//                if (activity == null || mp3Uri == null || saveDirPath == null) return null;
//
//                // 1. 获取MP3文件路径并解码
//                String mp3Path = activity.getPathFromUri(mp3Uri);
//                if (mp3Path == null) throw new IOException("无法获取MP3文件路径");
//                float[] pcmData = AudioDecoder.decodeMP3ToPCM(mp3Path);
//
//                // 2. 分析音符
//                AudioAnalyzer analyzer = new AudioAnalyzer(2048, 44100);
//                float[][] noteEnergies = analyzer.analyze(pcmData);
//
//                // 3. 准备输出MIDI文件
//                DocumentFile saveDir = DocumentFile.fromTreeUri(activity, Uri.parse(saveDirPath));
//                if (saveDir == null) throw new IOException("无法访问保存目录");
//
//                String baseName = new File(mp3Path).getName().replaceFirst("[.][^.]+$", "");
//                String midiName = baseName + ".mid";
//
//                // 删除已存在的同名文件
//                DocumentFile existingFile = saveDir.findFile(midiName);
//                if (existingFile != null) existingFile.delete();
//
//                // 创建新MIDI文件
//                DocumentFile newMidiFile = saveDir.createFile("audio/midi", midiName);
//                if (newMidiFile == null) throw new IOException("无法创建MIDI文件");
//
//                // 4. 生成MIDI并写入（使用修改后的MidiGenerator）
//                try (OutputStream os = activity.getContentResolver().openOutputStream(newMidiFile.getUri())) {
//                    if (os == null) throw new IOException("无法打开输出流");
//
//                    // 修改后的MIDI生成方法
//                    generateMidiToStream(noteEnergies, os);
//                }
//
//                // 5. 返回临时文件（如果需要File对象）
//                File tempFile = new File(activity.getCacheDir(), midiName);
//                try (InputStream is = activity.getContentResolver().openInputStream(newMidiFile.getUri());
//                     FileOutputStream fos = new FileOutputStream(tempFile)) {
//                    byte[] buffer = new byte[1024];
//                    int len;
//                    while ((len = is.read(buffer)) > 0) {
//                        fos.write(buffer, 0, len);
//                    }
//                }
//                return tempFile;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        // 新增的MIDI生成方法（适配OutputStream）
//        private void generateMidiToStream(float[][] noteEnergies, OutputStream os) throws IOException {
//            final int VELOCITY = 100;
//            final int TICKS_PER_QUARTER_NOTE = 480;
//
//            // MIDI文件头
//            byte[] header = {
//                    0x4D, 0x54, 0x68, 0x64, // "MThd"
//                    0x00, 0x00, 0x00, 0x06, // 头部长度
//                    0x00, 0x01,             // 格式类型（单轨）
//                    0x00, 0x01,             // 轨道数
//                    (byte)(TICKS_PER_QUARTER_NOTE >> 8),
//                    (byte)(TICKS_PER_QUARTER_NOTE & 0xFF)
//            };
//            os.write(header);
//
//            // 轨道数据
//            ByteArrayOutputStream trackData = new ByteArrayOutputStream();
//
//            // 乐器设置（钢琴）
//            writeMidiEvent(trackData, 0x00, 0xC0, 0, 0);
//
//            // 添加音符事件
//            for (int time = 0; time < noteEnergies.length; time++) {
//                int deltaTime = time * TICKS_PER_QUARTER_NOTE / 4;
//                for (int note = 0; note < noteEnergies[time].length; note++) {
//                    if (noteEnergies[time][note] > 0.1f) {
//                        int midiNote = note + 24;
//                        writeMidiEvent(trackData, deltaTime, 0x90, midiNote, VELOCITY);
//                        writeMidiEvent(trackData, TICKS_PER_QUARTER_NOTE / 4, 0x80, midiNote, 0);
//                    }
//                }
//            }
//
//            // 结束轨道
//            writeMidiEvent(trackData, 0x00, 0xFF, 0x2F, 0x00);
//
//            // 写入轨道头
//            byte[] trackHeader = {
//                    0x4D, 0x54, 0x72, 0x6B, // "MTrk"
//                    (byte)((trackData.size() >> 24) & 0xFF),
//                    (byte)((trackData.size() >> 16) & 0xFF),
//                    (byte)((trackData.size() >> 8) & 0xFF),
//                    (byte)(trackData.size() & 0xFF)
//            };
//            os.write(trackHeader);
//            os.write(trackData.toByteArray());
//            os.close();
//        }
//
//        private void writeMidiEvent(ByteArrayOutputStream out, int deltaTime, int status, int data1, int data2) {
//            // 与原MidiGenerator相同
//            if (deltaTime < 0x80) {
//                out.write(deltaTime);
//            } else {
//                out.write(0x80 | (deltaTime >> 7));
//                out.write(deltaTime & 0x7F);
//            }
//            out.write(status);
//            out.write(data1);
//            if (data2 >= 0) out.write(data2);
//        }
//
//
//        @Override
//        protected void onPostExecute(File midiFile) {
//            MainActivity activity = activityReference.get();
//            if (activity == null || activity.isFinishing()) return;
//
//            activity.progressBar.setVisibility(View.GONE);
//            activity.btnSelect.setEnabled(true);
//
//            if (midiFile != null) {
//                activity.tvStatus.setText("转换成功: " + midiFile.getName());
//                Toast.makeText(activity,
//                        "MIDI文件已保存到:\n" + midiFile.getAbsolutePath(),
//                        Toast.LENGTH_LONG).show();
//            } else {
//                activity.tvStatus.setText("转换失败");
//                Toast.makeText(activity, "转换失败，请检查日志", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private String getPathFromUri(Uri uri) throws IOException {
//        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
//            File tempFile = File.createTempFile("temp", ".mp3", getCacheDir());
//            FileUtils.copyInputStreamToFile(inputStream, tempFile);
//            return tempFile.getAbsolutePath();
//        }
//    }
//}