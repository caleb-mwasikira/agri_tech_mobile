package com.example.agritech.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agritech.R
import com.example.agritech.data.PlantThreshold
import com.example.agritech.data.Weather
import com.example.agritech.data.WeatherViewModel
import com.example.agritech.data.getCurrentDateTime
import com.example.agritech.ui.theme.OutfitFont
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getMostFrequentWeatherCondition(weatherList: List<Weather>): String? {
    val mostFrequentWeatherCondition = weatherList.groupingBy { it.conditions }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
    return mostFrequentWeatherCondition
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

@Composable
fun WeeklyForecast(
    weatherViewModel: WeatherViewModel?,
) {
    val scrollState = rememberScrollState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.location_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        "Kericho, Kenya",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = OutfitFont,
                    )

                    val currentDateTime by remember { mutableStateOf(getCurrentDateTime()) }
                    Text(
                        currentDateTime,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = OutfitFont,
                    )
                }
            }

            val crops = mapOf(
                "Coffee" to R.drawable.coffee_beans_24dp,
                "Tea" to R.drawable.tea_leaves_24dp,
                "Maize" to R.drawable.corn_24dp
            )
            var selectedCrop by remember { mutableStateOf("Coffee") }
            var plantThreshold by remember { mutableStateOf<PlantThreshold?>(null) }
            var recommendation by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(selectedCrop) {
                val today = LocalDate.now()
                plantThreshold = weatherViewModel?.getPlantThresholds(selectedCrop)
                val plantRecommendations = weatherViewModel?.getThisMonthsRecommendation(
                    today.monthValue, selectedCrop,
                )
                recommendation = plantRecommendations?.recommendations?.first()
            }

            SelectCrop(
                crops = crops,
                selectedCrop = selectedCrop,
                onSelectCrop = {
                    selectedCrop = it
                }
            )

            CropThresholds(
                crop = selectedCrop,
                temperature = plantThreshold?.minTemp?.toInt() ?: -1,
                precipitation = plantThreshold?.minPrecip?.toInt() ?: 0,
            )

            var currentDate by remember { mutableStateOf(LocalDate.now()) }
            var weeklyWeatherData by remember { mutableStateOf(listOf<Weather>()) }
            var nextWeeksWeatherCondition by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(currentDate) {
                weeklyWeatherData = weatherViewModel?.getThisWeeksWeather(
                    currentDate.monthValue, currentDate.dayOfMonth
                ) ?: emptyList()

                val nextWeek = currentDate.plusDays(7)
                val nextWeeksWeatherData = weatherViewModel?.getThisWeeksWeather(
                    nextWeek.monthValue, nextWeek.dayOfMonth
                ) ?: emptyList()
                nextWeeksWeatherCondition = getMostFrequentWeatherCondition(nextWeeksWeatherData)
            }

            // Recommendation
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "Recommendation",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = OutfitFont,
                )

                Text(
                    recommendation ?: "No Recommendations",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = OutfitFont,
                )
            }

            WeeklyForecast(
                currentDate = currentDate,
                onSelectCurrentDate = { newDate ->
                    currentDate = newDate
                },
                weatherData = weeklyWeatherData,
                nextWeeksWeatherCondition = nextWeeksWeatherCondition ?: ""
            )
        }
    }
}

@Composable
fun SelectCrop(
    crops: Map<String, Int>,
    selectedCrop: String,
    onSelectCrop: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.padding(vertical = 12.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier.clickable {
                expanded = true
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp),
            ) {
                val selectedCropIcon = crops[selectedCrop]
                selectedCropIcon?.let {
                    Image(
                        painter = painterResource(it),
                        contentDescription = "Selected Crop",
                        modifier = Modifier.size(24.dp),
                    )
                }

                Text(
                    selectedCrop,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = OutfitFont,
                )
                Icon(
                    painter = painterResource(R.drawable.dropdown_icon_24dp),
                    contentDescription = "Select Different Crop",
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            crops.keys.forEach { crop ->
                if (selectedCrop != crop) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                crop,
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = OutfitFont,
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelectCrop(crop)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CropThresholds(
    crop: String,
    temperature: Int,
    precipitation: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Conditions for growing $crop:",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = OutfitFont,
                modifier = Modifier
                    .width(320.dp)
                    .padding(bottom = 20.dp)
            )
            Row {
                Text(
                    "$temperature",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 148.sp
                    ),
                    fontFamily = OutfitFont,
                )
                Column {
                    Text(
                        "°C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = OutfitFont,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.rain_overcast),
                            contentDescription = "Rainfall",
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            "$precipitation mm",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = OutfitFont,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .rotate(-90f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.cloud_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "Partly Cloudy",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFont,
                )
            }
        }
    }
}

@Composable
fun WeeklyForecast(
    currentDate: LocalDate,
    onSelectCurrentDate: (LocalDate) -> Unit,
    weatherData: List<Weather>,
    nextWeeksWeatherCondition: String,
) {
    val formatter by remember {
        mutableStateOf(
            DateTimeFormatter.ofPattern("dd MMM uuuu")
        )
    }

    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Text(
                "Weekly Forecast",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = OutfitFont,
            )

            Text(
                currentDate.format(formatter),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = OutfitFont,
            )

            val dayFormatter by remember {
                mutableStateOf(
                    DateTimeFormatter.ofPattern("EEEE")
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (weatherData.isEmpty()) {
                    item {
                        Text(
                            "No weather data found for this week",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = OutfitFont,
                        )
                    }
                    return@LazyRow
                }

                items(weatherData.size) { index ->
                    val weatherToday = weatherData[index]
                    val drawable = getWeatherIcon(weatherToday.conditions)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(drawable),
                            contentDescription = null,
                            modifier = Modifier.size(42.dp)
                        )
                        Text(
                            weatherToday.date.format(dayFormatter),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = OutfitFont,
                        )
                        Text(
                            "${weatherToday.temp} °C",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            fontFamily = OutfitFont,
                        )
                    }
                }
            }

            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                ),
                colors = CardDefaults.cardColors().copy(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.clickable {
                    val nextWeek = currentDate.plusDays(7)
                    onSelectCurrentDate(nextWeek)
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    IconButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cloud_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.scrim,
                        )
                    }

                    Column {
                        Text(
                            "Next Week",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            fontFamily = OutfitFont,
                        )
                        Text(
                            nextWeeksWeatherCondition,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = OutfitFont,
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true, device = Devices.PIXEL_7,
)
@Composable
fun PreviewWeeklyForecast() {
    WeeklyForecast(
        weatherViewModel = null
    )
}