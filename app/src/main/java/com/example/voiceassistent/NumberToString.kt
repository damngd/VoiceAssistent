package com.example.voiceassistent

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

class NumberToString {
    fun getNumber(number: String?, callback: Consumer<String>) {
        val api: NumberAPI? = NumberService().getApi()
        val call: Call<Number?>? = api?.getCurrentNumber(number)
        call!!.enqueue(object : Callback<Number?> {
            override fun onResponse(call: Call<Number?>, response: Response<Number?>) {
                val result = response.body()
                if (result!=null){
                    var answer: String?
                    if(number!![0] == '-') answer = "минус " + result.current
                    else answer = result.current
                    callback.accept(answer.toString())
                } else callback.accept("Не могу перевести число")
            }
            override fun onFailure(call: Call<Number?>, t: Throwable) {
                t.message?.let { Log.w("WEATHER", it) }
            }
        })

    }
}