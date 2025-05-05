package com.example.music.Intent;

public class RegisterRequest {
    private String email;
    private String password;
    private String user_nickname;
    private String code;

    public RegisterRequest(String username,String email, String password, String code) {
        this.user_nickname = username;
        this.email = email;
        this.password = password;
        this.code = code;
    }
}