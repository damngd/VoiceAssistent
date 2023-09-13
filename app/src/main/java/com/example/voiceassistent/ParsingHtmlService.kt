package com.example.voiceassistent

import org.jsoup.Jsoup

class ParsingHtmlService {
    private val URL = "https://mirkosmosa.ru/holiday/2023"
    fun getHoliday(date: String?) :String
    {
        val document = Jsoup.connect(URL).get()
        val body = document.body()
        val elements = body.select("div.next_phase.month_row")
        for (element in elements)
        {
            val dat = element.select("span")[0].text()
            if (dat == date)
            {
                val holiday = element.select("div.month_cel").select("a").text()
                if (holiday != "")
                    return holiday
                else
                    return "Этот день без праздников"
            }
        }
        return ""
    }

}