package com.example.voiceassistent

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import io.reactivex.rxjava3.core.Observable
import java.util.regex.Matcher
import java.util.regex.Pattern

class AI{
    @RequiresApi(Build.VERSION_CODES.O)
    private val map = mutableMapOf("привет" to "Привет","приветик" to "Привет" ,
        "как дела" to "Неплохо", "чем занимаешься" to "Отвечаю на вопросы",
        "а чем занимаешься" to "Отвечаю на вопросы", "как делишки" to "Хорошо")

    private val date = Regex("(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d)")
    val regexData = "\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d".toRegex()
    @RequiresApi(Build.VERSION_CODES.O)
    val datetime = LocalDateTime.now()


    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAnswer(text: String, callback: Consumer<String>) {
        val answer = ArrayList<String>()
        val lower = text.lowercase()

        var modText = text.replace("[^а-яА-я ]".toRegex(),"")
        modText = modText.trim()
        modText = modText.lowercase()
        val curDate: String? = date.find(text)?.value

        if(modText.contains("который час")) callback.accept(getHour())
        else if(modText.contains("который час")) callback.accept(getHour())
        else if(modText.contains("какой сегодня день")) callback.accept(getDay())
        else if(modText.contains("какой день недели")) callback.accept(getWeak())
        else if(modText.contains("сколько дней до")) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.u")

            val startDate: String? = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.u"))

            val startDateValue = LocalDate.parse(startDate, dateFormatter)
            val endDateValue = LocalDate.parse(curDate, dateFormatter)
            val days = ChronoUnit.DAYS.between(startDateValue, endDateValue).toString()
            callback.accept("$days дней")
        }
        else if(modText.contains("праздник")) {
            val holiday: String = lower.substring(9)
            Observable.fromCallable{
                answer.add(ParsingHtmlService().getHoliday(getDate(holiday)));
                return@fromCallable answer
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    callback.accept(answer.joinToString(", ")) }
            return
        }

        else if(modText.contains("погода в городе")){
            val cityPattern: Pattern =
                Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
            val matcher: Matcher = cityPattern.matcher(text)
            if (matcher.find()) {
                val cityName: String = matcher.group(1) as String
                ForecastToString().getForecast(cityName, callback )
            }
        }
        else if(isInteger(text)) {
            NumberToString().getNumber(text, callback)
        }
        else callback.accept(map.getOrDefault(modText, "Я не знаю как ответить"))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDay(): String {
        return "Сегодня " + LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHour():String{
        return "Сейчас " + LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeak():String{
        return "Сегодня " + LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("EEEE"))
    }

    private fun isInteger(s: String): Boolean {
        return try{
            s.toInt()
            true
        } catch(e: java.lang.NumberFormatException){
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun getDate(date : String) :String
    {
        if(date == "сегодня")
            return datetime.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
        else if (date == "завтра")
            return datetime.plusDays(1).format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
        else if(date == "вчера")
            return datetime.minusDays(1).format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
        else if (date.matches(regexData))
        {
            val s = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")).format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
            return s
        }
        else
        {
            val s = LocalDate.parse(date, DateTimeFormatter.ofPattern("d MMMM yyyy")).format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
            return s
        }

    }

}