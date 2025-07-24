package com.example.agritech.data

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return LocalDateTime.parse(json?.asString, formatter)
    }
}

data class Weather(
    val date: LocalDateTime,
    val conditions: String,
    val temp: Float,
    val humidity: Float,
    val precip: Float,
)

data class RecommendationResponse(
    val plant: String,
    val recommendations: List<String>
)

data class PlantThreshold(
    @SerializedName("min_temp") val minTemp: Float,
    @SerializedName("max_temp") val maxTemp: Float,
    @SerializedName("min_precip") val minPrecip: Float,
    @SerializedName("max_precip") val maxPrecip: Float,
    val humidity: Float,
)

interface WeatherApi {
    @GET("weather/{month}/{day}")
    suspend fun getThisWeeksWeather(
        @Path("month") month: Int,
        @Path("day") day: Int
    ): List<Weather>

    @GET("weather/today")
    suspend fun getTodaysWeather(): Weather

    @GET("recommendations/{month}/{day}")
    suspend fun getWeeklyRecommendation(
        @Path("month") month: Int,
        @Path("day") day: Int,
        @Query("plant") plant: String,
    ): RecommendationResponse

    @GET("plant_thresholds/{plant}")
    suspend fun getPlantThresholds(
        @Path("plant") plant: String
    ): PlantThreshold
}

val gson = GsonBuilder()
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
    .create()

val api: WeatherApi by lazy {
    Retrofit.Builder()
        .baseUrl("http://192.168.43.141:5000/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(WeatherApi::class.java)
}