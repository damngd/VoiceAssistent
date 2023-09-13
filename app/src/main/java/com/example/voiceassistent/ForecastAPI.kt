package com.example.voiceassistent

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastAPI {
    @GET("/current?access_key=628664ad413fe1906dc4935a4a9cc9c7")
    fun getCurrentWeather(@Query("query") city: String?): Call<Forecast?>?
}