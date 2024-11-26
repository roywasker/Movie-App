package com.example.movie_app.Data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiService {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    //Create an HttpLoggingInterceptor object to debug and analyze performance
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //Creating an OkHttpClient object to send HTTP requests
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(2, TimeUnit.SECONDS) //Login time
        .readTimeout(3, TimeUnit.SECONDS)   //read time
        .build()

    //Create a Retrofit object with all the settings needed to receive the requests
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    inline fun <reified T> createApi(): T = retrofit.create(T::class.java)
}