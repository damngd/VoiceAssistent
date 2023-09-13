package com.example.voiceassistent

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NumberAPI {
    @GET("/api/convert/num2str?api_key=02c7211d9e4e0f00b43cc68e9db1b530&json&dec=0")
    fun getCurrentNumber(@Query("num") number: String?): Call<Number?>?
}