package com.example.ppavlik.hockeyappclient.requests

import com.example.ppavlik.hockeyappclient.data.CrachesPerDay
import com.example.ppavlik.hockeyappclient.data.CrachesPerDayTypeAdapter
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by ppavlik on 30.09.16.
 */
object RetrofitInstance {
    private const val HA_URL = "https://rink.hockeyapp.net/api/2/apps/817b00ae731c4a663272b4c4e53e4b61/"
    private var mInstance: Retrofit? = null

    fun getRetrofit(): Retrofit {
        if (mInstance == null) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            val customGson = GsonBuilder().registerTypeAdapter(CrachesPerDay::class.java, CrachesPerDayTypeAdapter()).create()
            mInstance = Retrofit.Builder().baseUrl(HA_URL)
                    .addConverterFactory(GsonConverterFactory.create(customGson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(httpClient.build())
                    .build()
        }
        return mInstance as Retrofit
    }
}