package com.example.agritech.data

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class WeatherViewModel : ViewModel() {
    companion object {
        const val TAG: String = "AGRI_TECH"
    }

    private val monthlyWeatherData = MutableStateFlow(mutableMapOf<Int, List<Weather>>())
    private val plantThresholds = MutableStateFlow(mutableMapOf<String, PlantThreshold>())

    suspend fun getThisWeeksWeather(month: Int, dayOfMonth: Int): List<Weather> {
        return try {
            // Check if this months weather data is already fetched
            val monthsWeather = monthlyWeatherData.value[month]

            val weeklyForecast: List<Weather> = if (monthsWeather == null) {
                api.getThisWeeksWeather(month, dayOfMonth)
            } else {
                val thisYear = LocalDate.now().year
                val startDate = LocalDate.of(thisYear, month, dayOfMonth)
                val next7Days = (1..7).map { startDate.plusDays(it.toLong()) }

                // Filter forecasts for the next 7 days
                monthsWeather.filter { weather ->
                    val forecastDate = weather.date.toLocalDate()
                    forecastDate in next7Days
                }
            }

            Log.d(TAG, "getThisWeeksWeather(month=$month, dayOfMonth=$dayOfMonth)")
            weeklyForecast
        } catch (e: Exception) {
            Log.e(TAG, "getThisWeeksWeather(month=$month, dayOfMonth=$dayOfMonth); ${e.message}")
            emptyList<Weather>()
        }
    }

    suspend fun getTodaysWeather(): Weather? {
        return try {
            val todaysWeather = api.getTodaysWeather()
            Log.d(TAG, "getTodaysWeather()")
            todaysWeather
        } catch (e: Exception) {
            Log.e(TAG, "getTodaysWeather(); ${e.message}")
            null
        }
    }

    suspend fun getWeeklyRecommendations(month: Int, day: Int, plant: String): List<String> {
        return try {
            Log.d(TAG, "getWeeklyRecommendations(month=$month, day=$day, plant=$plant)")
            val response = api.getWeeklyRecommendation(month, day, plant)
            response.recommendations
        } catch (e: Exception) {
            Log.e(
                TAG,
                "getWeeklyRecommendations(month=$month, day=$day, plant=$plant); ${e.message}"
            )
            emptyList<String>()
        }
    }

    suspend fun getPlantThresholds(plant: String): PlantThreshold? {
        return try {
            var result = plantThresholds.value[plant]
            if (result == null) {
                result = api.getPlantThresholds(plant)
                plantThresholds.update { oldMap ->
                    oldMap[plant] = result
                    oldMap
                }
            }

            Log.d(TAG, "getPlantThresholds(plant=$plant)")
            result
        } catch (e: Exception) {
            Log.e(TAG, "getPlantThresholds(plant=$plant); ${e.message}")
            null
        }
    }
}