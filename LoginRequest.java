package com.example.music.Intent;

public class LoginRequest {
    private int user_account;
    private String password;

    public LoginRequest(int username, String password) {
        this.user_account = username;
        this.password = password;
    }
}

