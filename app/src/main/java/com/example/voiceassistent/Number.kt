package com.example.voiceassistent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Number: java.io.Serializable {
    @SerializedName("str")
    @Expose
    var current: String? = null


}