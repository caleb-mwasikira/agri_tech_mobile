package com.example.agritech.remote

import android.content.Context
import android.util.Log
import com.example.agritech.MainActivity
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response

const val BASE_IP_ADDRESS: String = "192.168.167.46:5000"

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()

        // Fetch access token from sharedPreferences
        val context = MainActivity.instance.applicationContext
        val sharedPreferences =
            context.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(MainActivity.ACCESS_TOKEN, "")

        // Add request headers to request
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(newRequest)
    }
}

val client = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .build()

fun <T> Response<T>.extractErrorMessage(): String? {
    try {
        Log.e(MainActivity.TAG, "$this")

        val errorBody: String = this.errorBody()?.string() ?: return null
        val gson = Gson()
        val messageResponse =
            gson.fromJson<MessageResponse>(errorBody, MessageResponse::class.java)
        return messageResponse.msg

    } catch (e: Exception) {
        Log.e(MainActivity.TAG, "extractErrorMessage() => ${e.message}")
        return null
    }
}
