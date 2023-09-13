package com.example.voiceassistent

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.function.Consumer

class ForecastToString {
    fun getForecast(city: String?, callback: Consumer<String>) {
        val api: ForecastAPI? = ForecastService().getApi()
        val call: Call<Forecast?>? = api?.getCurrentWeather(city)
        call!!.enqueue(object : Callback<Forecast?> {
            override fun onResponse(call: Call<Forecast?>, response: Response<Forecast?>) {
                val result = response.body()
                if (result!=null){
                    val temperature = result.current?.temperature
                    val answer: String = "Сейчас где-то " + temperature + " " +
                            declination(temperature) + " и " + result.current?.weather_descriptions?.get(0)
                callback.accept(answer)
                } else callback.accept("Не могу узнать погоду")
            }
            override fun onFailure(call: Call<Forecast?>, t: Throwable) {
                t.message?.let { Log.w("WEATHER", it) }
            }
        })

    }
     private fun declination(degrees: Int?): String? {
         if (degrees != null) {
             if(degrees%100 == 11) return "градусов"
             else if(degrees%100 == 12) return "градусов"
             else if(degrees%100 == 13) return "градусов"
             else if(degrees%100 == 14) return "градусов"
             else if(degrees%10 == 1 ||
                 degrees%10 == 2 ||
                 degrees%10 == 3 ||
                 degrees%10 == 4) return "градуса"
             else return "градусов"
         }
         return null
     }
}