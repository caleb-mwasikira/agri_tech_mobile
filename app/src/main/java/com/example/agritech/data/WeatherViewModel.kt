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
    private val monthlyRecommendations =
        MutableStateFlow(mutableMapOf<String, Recommendation>())
    private val plantThresholds = MutableStateFlow(mutableMapOf<String, PlantThreshold>())

    suspend fun getMonthlyWeather(month: Int): List<Weather> {
        return try {
            // Check if month weather data was already fetched
            var monthsWeather = monthlyWeatherData.value[month]
            if (monthsWeather == null) {
                monthsWeather = api.getMonthlyWeather(month)
                monthlyWeatherData.update { oldMap ->
                    oldMap[month] = monthsWeather
                    oldMap
                }
            }
            Log.d(TAG, "getMonthlyWeather(month=$month); $monthsWeather")
            monthsWeather
        } catch (e: Exception) {
            Log.e(TAG, "getMonthlyWeather(month=$month); ${e.message}")
            emptyList<Weather>()
        }
    }

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

            Log.d(TAG, "getThisWeeksWeather(month=$month, dayOfMonth=$dayOfMonth); $weeklyForecast")
            weeklyForecast
        } catch (e: Exception) {
            Log.e(TAG, "getThisWeeksWeather(month=$month, dayOfMonth=$dayOfMonth); ${e.message}")
            emptyList<Weather>()
        }
    }

    suspend fun getTodaysWeather(): Weather? {
        return try {
            val todaysWeather = api.getTodaysWeather()
            Log.d(TAG, "getTodaysWeather(); $todaysWeather")
            todaysWeather
        } catch (e: Exception) {
            Log.e(TAG, "getTodaysWeather(); ${e.message}")
            null
        }
    }

    suspend fun getThisMonthsRecommendation(month: Int, plant: String): Recommendation? {
        return try {
            // Check if month recommendations was already fetched
            val key = "$plant-$month"
            var result = monthlyRecommendations.value[key]
            if (result == null) {
                result = api.getThisMonthsRecommendation(month, plant)
                monthlyRecommendations.update { oldMap ->
                    oldMap[key] = result
                    oldMap
                }
            }

            Log.d(TAG, "getThisMonthsRecommendation(month=$month, plant=$plant); $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "getThisMonthsRecommendation(month=$month, plant=$plant); ${e.message}")
            null
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

            Log.d(TAG, "getPlantThresholds(plant=$plant); $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "getPlantThresholds(plant=$plant); ${e.message}")
            null
        }
    }
}