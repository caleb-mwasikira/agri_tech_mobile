package com.example.agritech.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritech.R
import com.example.agritech.data.Route
import com.example.agritech.data.Weather
import com.example.agritech.data.AppViewModel
import com.example.agritech.data.CropThreshold
import com.example.agritech.data.formatter
import com.example.agritech.data.getCurrentDateTime
import com.example.agritech.data.getWeatherIcon
import com.example.agritech.data.parseLocation
import com.example.agritech.ui.theme.AgriTechTheme
import com.example.agritech.ui.theme.Poppins
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyForecast(
    appViewModel: AppViewModel = viewModel(),
    navigateTo: (Route) -> Unit,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val context = LocalContext.current
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            appViewModel.errors.collect {
                error = it
                delay(4000)
                error = null
            }
        }

        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG)
                .show()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.map2_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                var displaySelectLocation: Boolean by remember { mutableStateOf(false) }
                val locations: List<String> by appViewModel.locations.collectAsState()
                val selectedLocation: String? by appViewModel.selectedLocation.collectAsState()

                Column(
                    modifier = Modifier.clickable(
                        onClick = { displaySelectLocation = true },
                    )
                ) {
                    Text(
                        parseLocation(selectedLocation) ?: "Unknown Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Poppins,
                    )

                    val currentDateTime by remember { mutableStateOf(getCurrentDateTime()) }
                    Text(
                        currentDateTime,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = Poppins,
                    )
                }

                if (displaySelectLocation) {
                    SelectLocation(
                        locations = locations,
                        onDismissRequest = {
                            displaySelectLocation = false
                        },
                        selectLocation = {
                            displaySelectLocation = false
                            appViewModel.updateSelectedLocation(it)
                        }
                    )
                }
            }

            val selectedCrop by appViewModel.selectedCrop.collectAsState()
            val cropThreshold by appViewModel.cropThreshold.collectAsState()
            val recommendations by appViewModel.recommendations.collectAsState()
            val currentDate by appViewModel.currentDate.collectAsState()
            val weeklyWeatherData by appViewModel.weeklyWeatherData.collectAsState()
            val nextWeeksWeatherCondition by appViewModel.nextWeeksWeatherCondition.collectAsState()
            val suitableCrops by appViewModel.suitableCrops.collectAsState()

            SelectCrop(
                crops = suitableCrops,
                selectedCrop = selectedCrop ?: "No Crop Selected",
                onSelectCrop = {
                    appViewModel.updateSelectedCrop(it)
                }
            )

            CropThresholds(
                crop = selectedCrop ?: "No Crop Selected",
                temperature = cropThreshold?.minTemp?.toInt() ?: 0,
                precipitation = cropThreshold?.minPrecip?.toInt() ?: 0,
            )

            // Recommendation
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        "Recommendations",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Poppins,
                    )
                    Text(
                        currentDate.format(formatter),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = Poppins,
                    )
                }

                val recommendation = if (recommendations.isEmpty()) {
                    "No current recommendations"
                } else {
                    recommendations.first()
                }

                TextButton(
                    onClick = {
                        navigateTo(Route.ViewArticles)
                    },
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        recommendation,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = Poppins,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }

            WeeklyForecastCard(
                currentDate = currentDate,
                selectCurrentDate = { newDate ->
                    appViewModel.updateCurrentDate(newDate)
                },
                weatherData = weeklyWeatherData,
                nextWeeksWeatherCondition = nextWeeksWeatherCondition ?: ""
            )
        }
    }
}

@Composable
fun SelectLocation(
    locations: List<String>,
    onDismissRequest: () -> Unit,
    selectLocation: (String) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.scrim,
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.map_24dp),
                        contentDescription = "Select Location",
                        modifier = Modifier.size(56.dp),
                    )
                    Text(
                        "Select Location",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                if (locations.isEmpty()) {
                    Text(
                        "No locations available",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    return@Column
                }

                locations.forEach { location ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clickable(
                                onClick = { selectLocation(location) }
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.location_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            parseLocation(location) ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectCrop(
    crops: List<CropThreshold>,
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
//                val selectedCropIcon = crops.filter { it.crop == selectedCrop.crop }
//                selectedCropIcon?.let {
//                    Image(
//                        painter = painterResource(it),
//                        contentDescription = "Selected Crop",
//                        modifier = Modifier.size(24.dp),
//                    )
//                }

                Text(
                    selectedCrop,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Poppins,
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
            crops.forEach { crop ->
                if (selectedCrop != crop.name) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                crop.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Poppins,
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelectCrop(crop.name)
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
            .padding(vertical = 12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Conditions for growing $crop:",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Poppins,
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
                    fontFamily = Poppins,
                )
                Column {
                    Text(
                        "°C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = Poppins,
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
                            fontFamily = Poppins,
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
                    fontFamily = Poppins,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyForecastCard(
    currentDate: LocalDate,
    selectCurrentDate: (LocalDate) -> Unit,
    weatherData: List<Weather>,
    nextWeeksWeatherCondition: String,
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        "Weekly Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Poppins,
                    )
                    Text(
                        currentDate.format(formatter),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = Poppins,
                    )
                }

                var displayCalendar by remember { mutableStateOf(false) }
                IconButton(
                    onClick = {
                        displayCalendar = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Select Date"
                    )
                }

                val datePickerState = rememberDatePickerState()
                val context = LocalContext.current

                if (displayCalendar) {
                    Dialog(
                        onDismissRequest = {
                            displayCalendar = false
                            val timestamp: Long? = datePickerState.selectedDateMillis
                            if (timestamp == null) {
                                Toast.makeText(context, "Please Select A Date", Toast.LENGTH_LONG)
                                    .show()
                                return@Dialog
                            }

                            val selectedDate = Instant.ofEpochMilli(timestamp)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            selectCurrentDate(selectedDate)
                            return@Dialog

                        },
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .padding(12.dp)
                        ) {
                            DatePicker(
                                state = datePickerState,
                            )
                        }
                    }
                }
            }

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
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = Poppins,
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
                            fontFamily = Poppins,
                        )
                        Text(
                            "${weatherToday.temp} °C",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            fontFamily = Poppins,
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
                    selectCurrentDate(nextWeek)
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
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            fontFamily = Poppins,
                        )
                        Text(
                            nextWeeksWeatherCondition,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = Poppins,
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
        navigateTo = {},
    )
}

@Preview(
    showBackground = true, device = Devices.PIXEL_7,
)
@Composable
fun PreviewSelectLocation() {
    AgriTechTheme {
        SelectLocation(
            locations = listOf("Nariobi", "Tokyo", "Oslo", "Denver"),
            onDismissRequest = {},
            selectLocation = {}
        )
    }
}