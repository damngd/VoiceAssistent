package com.example.voiceassistent

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForecastService {
    private var retrofit: Retrofit? = null

    fun getApi(): ForecastAPI? {
        if(retrofit==null) {

            retrofit = Retrofit.Builder()
                .baseUrl("http://api.weatherstack.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build()
        }
            return retrofit?.create(ForecastAPI::class.java) //Создание объекта, при помощи которого будут выполняться запросы
    }

}