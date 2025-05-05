package com.example.music.auth;

import com.example.music.Intent.ApiService;
import com.example.music.Intent.LoginRequest;
import com.example.music.Intent.LoginResponse;
import com.example.music.Page_me_Activity;
import com.example.music.R;

import retrofit2.Call;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class LoginActivity extends AppCompatActivity {
    // 成员变量声明
    private TextView tvRegister, tvForgotPassword,tvLoginMessage;
    private Button btnLogin;
    private EditText etUsername, etPassword;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("crash_last_time", false).apply();

        // ✅ 设置内容铺满状态栏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // ✅ 状态栏设为透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        // 初始化视图
        initViews();
        // 检查是否有注册成功的账号传递过来
        handleAutoFillUsername();

        // 初始化 Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://115.29.178.160:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // 设置登录按钮点击事件
        setupLoginButton();
        setupRegisterButton();
        setupForgotPasswordButton();


    }
    private void handleAutoFillUsername() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("registered_username")) {
            String username = intent.getStringExtra("registered_username");
            etUsername.setText(username);

            // 可选：自动聚焦到密码输入框
            findViewById(R.id.etPassword).requestFocus();
        }
    }
    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvLoginMessage = findViewById(R.id.tvLoginMessage);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);


    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            // 获取输入内容
            int username;
            try {
                username = Integer.parseInt(etUsername.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的数字用户名", Toast.LENGTH_SHORT).show();
                return;
            }

            String password = etPassword.getText().toString().trim();
            if (password.isEmpty()) {
                etPassword.setError("密码不能为空");
                return;
            }

            // 创建并发送登录请求
            LoginRequest loginRequest = new LoginRequest(username, password);
            Call<LoginResponse> call = apiService.login(loginRequest);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        handleLoginSuccess(response.body());
                    } else {
                        handleHttpError(response); // 正确调用封装的方法
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    handleNetworkError(t);
                }
            });
        });
    }
    private void setupRegisterButton() {
        // 注册点击事件
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "跳转到注册页面", Toast.LENGTH_SHORT).show();
                // 实际开发中可以跳转到注册Activity
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void setupForgotPasswordButton() {
        // 忘记密码点击事件
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "跳转到找回密码页面", Toast.LENGTH_SHORT).show();
                // 实际开发中可以跳转到找回密码Activity
                // startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }
    private void handleLoginSuccess(LoginResponse response) {
        if (response.isSuccess()) {
            SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userAccount",etUsername.getText().toString()); // 假设是 string 类型
            editor.apply(); // 保存
            startActivity(new Intent(this, Page_me_Activity.class));
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            tvLoginMessage.setVisibility(View.VISIBLE);
            tvLoginMessage.setText(response.getMessage());
        }
    }

    private void handleHttpError(Response<?> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "无错误信息";

            String errorMsg = String.format("状态码: %d\n错误: %s",
                    response.code(),
                    errorBody);

            runOnUiThread(() -> {
                tvLoginMessage.setVisibility(View.VISIBLE);
                tvLoginMessage.setText(errorMsg);
            });

        } catch (IOException e) {
            runOnUiThread(() -> {
                tvLoginMessage.setVisibility(View.VISIBLE);
                tvLoginMessage.setText("请求失败: " + response.message());
            });
        }
    }

    private void handleNetworkError(Throwable t) {
        runOnUiThread(() -> {
            tvLoginMessage.setVisibility(View.VISIBLE);
            tvLoginMessage.setText("网络错误，请检查网络连接");
            Log.e("LoginError", "网络错误", t);
        });
    }
}

