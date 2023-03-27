package com.playplelx.network

import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import com.playplelx.util.Constants
import com.playplelx.util.PrefManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class Apiclient {

    private var context: Context? = null
    private var retrofit: Retrofit? = null
    private val baseUrl = "https://pospayplex.com/api/"
    private var prefManager: PrefManager? = null

    constructor(context: Context) {
        this.context = context;
    }


    fun getClient(): Retrofit? {
        val gson = GsonBuilder().setLenient().create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer " + PrefManager(context!!).getValue(Constants.ACCESS_TOKEN).toString()
                )
                .addHeader(
                    "X-Requested-With",
                    "XMLHttpRequest"
                )
                .addHeader(
                    "Accept",
                    "application/json"
                )
                .build()
            Log.d(
                "Authorization",
                "=" + PrefManager(context!!).getValue(Constants.ACCESS_TOKEN)
            )
            chain.proceed(newRequest)
        }/*.addInterceptor(
                ChuckerInterceptor.Builder(context!!).collector(ChuckerCollector(context!!))
                    .maxContentLength(250000L).redactHeaders("Auth-Token", "Bearer")
                    .alwaysReadResponseBody(true).build()
            )*/
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }
}