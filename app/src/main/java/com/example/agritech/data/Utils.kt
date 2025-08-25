package com.example.agritech.data

import com.example.agritech.R
import com.example.agritech.remote.Weather
import java.util.Locale

fun parseLocation(location: String?): String? {
    location ?: return null

    val parts = location.split("_")
    val transformedParts = parts.map { part ->
        part.replaceFirstChar { it.uppercaseChar() }
    }
    return transformedParts.joinToString(", ")
}

fun getMostFrequentWeatherCondition(weatherList: List<Weather>): String {
    val mostFrequentWeatherCondition = weatherList.groupingBy { it.conditions }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
    var words: List<String> = mostFrequentWeatherCondition?.split("_") ?: return ""
    words = words.map { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
    return words.joinToString(" ")
}

fun getWeatherIcon(condition: String?): Int {
    return when (condition) {
        "partially_cloudy" -> R.drawable.partially_cloudy
        "rain", "rain_partially_cloudy" -> R.drawable.rain_partially_cloudy
        "clear" -> R.drawable.clear_24dp
        "rain_overcast" -> R.drawable.rain_overcast
        "overcast" -> R.drawable.dark_cloud_24dp
        else -> R.drawable.sunny_sunglasses_24dp
    }
}