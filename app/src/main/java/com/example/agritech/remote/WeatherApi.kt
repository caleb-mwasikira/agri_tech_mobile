package com.example.agritech.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val localDateTimeAdapter = JsonDeserializer<LocalDateTime> { json, typeOfT, context ->
    LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

/**
 * {
 *     "date": "2025-07-16T00:00:00.000",
 *     "tempmax": 27.2,
 *     "tempmin": 14.18,
 *     "temp": 21.04,
 *     "humidity": 70.9,
 *     "precip": 1.04,
 *     "windspeed": 16.9,
 *     "conditions": "partially_cloudy"
 *   }
 */
data class Weather(
    val day: String = "",
    val date: LocalDateTime,
    @SerializedName("tempmax") val tempMax: Float,
    @SerializedName("tempmin") val tempMin: Float,
    @SerializedName("temp") val temp: Float,
    val humidity: Float,
    val precip: Float,
    @SerializedName("windspeed") val windSpeed: Float,
    val conditions: String,
)

data class RecommendationResponse(
    val crop: String,
    val recommendations: List<String>
)

/**
 * {
 *     "crop": "tea",
 *     "icon": "🌱",
 *     "max_humidity": 90,
 *     "max_precip": 800,
 *     "max_solarradiation": 20,
 *     "max_temp": 25,
 *     "min_humidity": 70,
 *     "min_precip": 400,
 *     "min_solarradiation": 10,
 *     "min_temp": 13
 *   }
 */
data class CropThreshold(
    val name: String,
    val icon: String,
    @SerializedName("min_temp") val minTemp: Float,
    @SerializedName("max_temp") val maxTemp: Float,
    @SerializedName("min_precip") val minPrecip: Float,
    @SerializedName("max_precip") val maxPrecip: Float,
    @SerializedName("min_humidity") val minHumidity: Float,
    @SerializedName("max_humidity") val maxHumidity: Float,
    @SerializedName("min_solarradiation") val minSolarRadiation: Float,
    @SerializedName("max_solarradiation") val maxSolarRadiation: Float,
)

interface WeatherApi {
    @GET("all-crops")
    suspend fun getAllCrops(): List<CropThreshold>

    @GET("all-locations")
    suspend fun getAllLocations(): List<String>

    @GET("crop_thresholds/{crop}")
    suspend fun getCropThresholds(
        @Path("crop") crop: String,
    ): CropThreshold

    @GET("suitable_crops/{location}")
    suspend fun getSuitableCrops(
        @Path("location") location: String,
    ): List<CropThreshold>

    @GET("weather/today/{location}")
    suspend fun getTodaysWeather(
        @Path("location") location: String
    ): Weather

    @GET("weather/{location}/{month}/{day}")
    suspend fun getThisWeeksWeather(
        @Path("location") location: String,
        @Path("month") month: Int,
        @Path("day") day: Int,
    ): List<Weather>

    @GET("weather/{location}/{month}")
    suspend fun getThisMonthsWeather(
        @Path("location") location: String,
        @Path("month") month: Int,
    ): List<Weather>

    @GET("recommendations/{location}/{month}/{day}")
    suspend fun getWeeklyRecommendations(
        @Path("location") location: String,
        @Path("month") month: Int,
        @Path("day") day: Int,
        @Query("crop") crop: String,
    ): RecommendationResponse
}

val gson: Gson = GsonBuilder()
    .registerTypeAdapter(LocalDateTime::class.java, localDateTimeAdapter)
    .create()

val weatherApi: WeatherApi by lazy {
    Retrofit.Builder()
        .baseUrl("http://$BASE_IP_ADDRESS/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(WeatherApi::class.java)
}