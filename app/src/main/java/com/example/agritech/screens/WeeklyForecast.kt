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
import androidx.compose.foundation.shape.CircleShape
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
import com.example.agritech.data.Recommendation
import com.example.agritech.data.Weather
import com.example.agritech.data.WeatherViewModel
import com.example.agritech.data.daysOfWeek
import com.example.agritech.data.getCurrentDateTime
import com.example.agritech.ui.theme.OutfitFont
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyForecast(
    weatherViewModel: WeatherViewModel?,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.location_24dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Kericho, Kenya",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = OutfitFont,
                    )
                }

                val currentDateTime by remember { mutableStateOf(getCurrentDateTime()) }
                Text(
                    currentDateTime,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = OutfitFont,
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
            }

            val crops = mapOf(
                "Coffee" to R.drawable.coffee_beans_24dp,
                "Tea" to R.drawable.tea_leaves_24dp,
                "Maize" to R.drawable.corn_24dp
            )
            var selectedCrop by remember { mutableStateOf("Coffee") }
            var plantThreshold by remember { mutableStateOf<PlantThreshold?>(null) }
            var plantRecommendations by remember { mutableStateOf<Recommendation?>(null) }
            var recommendation by remember { mutableStateOf("") }

            LaunchedEffect(selectedCrop) {
                val today = LocalDate.now()
                plantRecommendations = weatherViewModel?.getThisMonthsRecommendation(
                    today.monthValue, selectedCrop,
                )
                plantThreshold = plantRecommendations?.plantThresholds
                recommendation = plantRecommendations?.recommendations?.first() ?: ""
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

            Text(
                recommendation,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = OutfitFont,
            )

            var currentDate by remember { mutableStateOf(LocalDate.now()) }
            var weeklyWeatherData by remember { mutableStateOf(listOf<Weather>()) }

            LaunchedEffect(currentDate) {
                weeklyWeatherData = weatherViewModel?.getThisWeeksWeather(
                    currentDate.monthValue, currentDate.dayOfMonth
                ) ?: emptyList()
            }

            WeeklyForecast()
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
                            painter = painterResource(R.drawable.rainy_day_24dp),
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
    weeksWeatherData: List<Weather>,
) {
    val formatter by remember {
        mutableStateOf(
            DateTimeFormatter.ofPattern("yyyy MMM EEEE")
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

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(daysOfWeek.size) { index ->
                    val day = daysOfWeek[index]

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.sunny_day_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(42.dp)
                        )
                        Text(
                            day,
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = OutfitFont,
                        )
                        Text(
                            "24 °C",
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
                )
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
                            "Light Rain Showers",
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