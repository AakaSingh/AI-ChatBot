package com.aakash.chatbot.network

import com.aakash.chatbot.entities.AIResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


private const val BASE_URL = "http://api.brainshop.ai/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface BrainShopApiService{
    @GET
    fun getAiResponse(@Url url: String) : Call<AIResponse>
}

object BrainShopApi{
    val retrofitService: BrainShopApiService by lazy {
        retrofit.create(BrainShopApiService::class.java)
    }
}
