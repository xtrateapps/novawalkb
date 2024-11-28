package com.novaservices.nova.utils

import com.novaservices.lotonovabanklot.data.api.APIService
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.Part
import java.util.concurrent.TimeUnit


object RetrofitInstance {



    var client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: APIService by lazy {
        Retrofit.Builder()
            .baseUrl(Utils.PRODBASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(APIService::class.java)
    }
}