package com.example.music.Intent;

public class SendCodeResponse {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return message.equals("验证码已发送");
    }

    public String getMessage() {
        return message;
    }
}