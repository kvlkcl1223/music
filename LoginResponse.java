package com.example.music.Intent;

public class LoginResponse {
    //private boolean success;
    private String message;
    private String token;

    public boolean isSuccess() {
        return message.equals("登录成功");
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
