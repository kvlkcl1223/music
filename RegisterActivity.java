package com.example.music.auth;

//import android.support.v7.app.AppCompatActivity;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.music.BuildConfig;
import com.example.music.Intent.ApiService;
import com.example.music.Intent.RegisterRequest;
import com.example.music.Intent.RegisterResponse;
import com.example.music.Intent.SendCodeRequest;
import com.example.music.Intent.SendCodeResponse;


import com.example.music.R;
import okhttp3.Callback;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;

    private EditText etUsername, etEmail, etPassword, etConfirmPassword, etCode;
    private Button btnRegister, btnSendCode;
    private ImageView ivAvatar;
    private ProgressBar progressBar;
    private TextView tvMessage;
    private ApiService apiService;
    private Uri avatarUri;
    private File avatarFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // ✅ 设置内容铺满状态栏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // ✅ 状态栏设为透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // 初始化视图
        initViews();

        // 设置头像点击事件
        ivAvatar.setOnClickListener(v -> checkPermissionAndPickImage());

        // 注册按钮点击事件
        btnRegister.setOnClickListener(v -> attemptRegister());

        // 发送验证码按钮点击事件
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etUseremail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etPassword_second);
        etCode = findViewById(R.id.etCode);
        btnRegister = findViewById(R.id.btnRegister);
        btnSendCode = findViewById(R.id.btnSendCode);
        ivAvatar = findViewById(R.id.ivAvatar);
        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            openImageChooser();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择头像"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            try {
                // 显示预览
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), avatarUri);
                ivAvatar.setImageBitmap(bitmap);

                // 创建临时文件
                avatarFile = createTempImageFile();
                compressAndSaveImage(avatarUri, avatarFile);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "图片处理失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void compressAndSaveImage(Uri uri, File outputFile) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        }
    }

    private void attemptRegister() {
        if (!validateForm()) {
            return;
        }

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        if (username.isEmpty()) {
            String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder randomUsername = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 8; i++) {
                randomUsername.append(chars.charAt(random.nextInt(chars.length())));
            }
            username = randomUsername.toString();
        }

        // 创建Multipart请求
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_nickname", username)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .addFormDataPart("code", code)
                .addFormDataPart("avatar", avatarFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), avatarFile))
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url("http://115.29.178.160:3000/register")
                .post(requestBody)
                .build();

        // 发送请求
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    tvMessage.setText("网络错误: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    try {
                        JSONObject json = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            // 注册成功逻辑
                            String userAccount = json.optString("user_account", "");
                            String message = json.optString("message", "注册成功");
                            // 显示对话框
                            showAccountDialog(userAccount, () -> {
                                navigateToLogin(userAccount);
                            });


                        } else {
                            // 注册失败逻辑
                            String errorMsg = json.optString("error", "注册失败");
                            if (errorMsg.isEmpty()) {
                                errorMsg = response.message(); // 使用HTTP状态消息作为备选
                            }
                            tvMessage.setText(errorMsg);

                            // 可选：特定错误码特殊处理
                            int code = response.code();
                            if (code == 400) {
                                tvMessage.setTextColor(Color.RED);
                            }
                        }
                    } catch (JSONException e) {
                        // JSON解析错误处理
                        tvMessage.setText("响应解析错误: " + e.getMessage());
                        Log.e("Register", "JSON解析错误", e);

                        // 尝试直接显示原始响应（调试用）
                        if (BuildConfig.DEBUG) {
                            tvMessage.append("\n原始响应: " + responseData);
                        }
                    } catch (Exception e) {
                        // 其他未知错误
                        tvMessage.setText("处理响应时发生错误");
                        Log.e("Register", "处理响应错误", e);
                    }
                });
            }


            // 修改后的对话框显示方法（添加回调）
            private void showAccountDialog(String account, Runnable onDismiss) {
                AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("注册成功")
                        .setMessage("您的账号是: " + account)
                        .setPositiveButton("确定", (d, which) -> {
                            if (onDismiss != null) onDismiss.run();
                        })
                        .setCancelable(false)
                        .create();

                dialog.setOnDismissListener(d -> {
                    if (onDismiss != null) onDismiss.run();
                });

                dialog.show();
            }
            // 跳转到登录页面
            private void navigateToLogin( String account) {
                // 创建Intent时携带账号参数
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("registered_username", account);
                startActivity(intent);
                finish(); // 结束当前注册页面
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String code = etCode.getText().toString().trim();

//        if (username.isEmpty()) {
//            etUsername.setError("请输入用户名");
//            valid = false;
//        }

        if (email.isEmpty()) {
            etEmail.setError("请输入邮箱");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("邮箱格式不正确");
            valid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("请输入密码");
            valid = false;
        } else if (password.length() < 6) {
            etPassword.setError("密码至少6位");
            valid = false;
        }

        if (!confirmPassword.equals(password)) {
            etConfirmPassword.setError("两次密码不一致");
            valid = false;
        }

        if (code.isEmpty()) {
            etCode.setError("请输入验证码");
            valid = false;
        }

        return valid;
    }

    private void sendVerificationCode() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("请输入有效邮箱");
            return;
        }
        // 初始化 Retrofit
        retrofit2.Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://115.29.178.160:3000/") // 替换为你的 API 地址
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        // 发送验证码请求
        retrofit2.Call<SendCodeResponse> call = apiService.sendcode(new SendCodeRequest(email));
        call.enqueue(new retrofit2.Callback<SendCodeResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SendCodeResponse> call, retrofit2.Response<SendCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SendCodeResponse sendCodeResponse = response.body();
                    if (sendCodeResponse.isSuccess()) {
                        tvMessage.setText("验证码已发送，请查收邮箱" );
                    } else {
                        tvMessage.setText("发送失败: " + sendCodeResponse.getMessage());
                    }
                } else {
                    // HTTP请求失败（状态码非200-299）
                    try {
                        // 尝试解析错误响应体
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "无错误信息";

                        // 显示HTTP状态码和错误信息
                        String errorMsg = String.format("状态码: %d\n错误: %s",
                                response.code(),
                                errorBody);

                        tvMessage.setText(errorMsg);
                        Toast.makeText(RegisterActivity.this,errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        // 解析错误体时出错
                        tvMessage.setText("请求失败: " + response.message());
                        Toast.makeText(RegisterActivity.this,response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SendCodeResponse> call, Throwable t) {
                tvMessage.setText("网络错误: " + t.getMessage());
            }
        });

    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        // 清理临时文件
        if (avatarFile != null && avatarFile.exists()) {
            avatarFile.delete();
        }
        super.onDestroy();
    }
}