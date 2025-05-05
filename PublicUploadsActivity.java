package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music.Intent.UploadAdapter;
import com.example.music.Intent.UploadFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PublicUploadsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UploadAdapter adapter;
    private List<UploadFile> uploadList = new ArrayList<>();
    private static final String PUBLIC_URL = "http://115.29.178.160:3000/api/public_uploads";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_public_uploads);

        recyclerView = findViewById(R.id.recycler_uploads);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UploadAdapter(uploadList);
        recyclerView.setAdapter(adapter);

        loadPublicUploads();

    }
    private void loadPublicUploads() {
        String url = "http://118.31.107.191:3000/api/public_uploads";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(PublicUploadsActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Type type = new TypeToken<List<UploadFile>>() {}.getType();
                    List<UploadFile> result = new Gson().fromJson(json, type);

                    runOnUiThread(() -> {
                        uploadList.clear();
                        uploadList.addAll(result);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }
}
