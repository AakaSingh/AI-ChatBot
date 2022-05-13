package com.aakash.chatbot.entities

import com.google.gson.annotations.SerializedName

data class AIResponse(
    @SerializedName("cnt") val message : String
){

}
