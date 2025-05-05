package com.example.music.Intent;

public class RegisterResponse {
    private String message;
    private String token;

    public boolean isSuccess() {
        return message.equals("注册成功");
    }

    public String getMessage() {
        return message;
    }
}
