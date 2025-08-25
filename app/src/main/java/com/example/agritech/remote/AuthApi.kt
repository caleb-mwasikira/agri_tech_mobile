package com.example.agritech.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class MessageResponse(
    val msg: String,
)

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val msg: String,
    @SerializedName("access_token") val accessToken: String,
)

data class ForgotPasswordRequest(
    val email: String,
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    @SerializedName("new_password") val newPassword: String,
    @SerializedName("confirm_new_password") val confirmNewPassword: String,
)

interface AuthApi {
    @POST("auth/register")
    suspend fun createAccount(@Body request: SignUpRequest): Response<MessageResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>
}

val authApi: AuthApi by lazy {
    Retrofit.Builder()
        .baseUrl("http://$BASE_IP_ADDRESS/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(AuthApi::class.java)
}