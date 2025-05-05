package com.example.music.Intent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("sendcode")
    Call<SendCodeResponse> sendcode(@Body SendCodeRequest sendCodeRequest);
    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
}