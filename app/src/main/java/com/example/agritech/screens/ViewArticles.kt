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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.agritech.R
import com.example.agritech.data.AppViewModel
import com.example.agritech.data.Note
import com.example.agritech.data.getCurrentDateTime
import com.example.agritech.data.getWeatherIcon
import com.example.agritech.data.notes
import com.example.agritech.data.parseLocation
import com.example.agritech.remote.Weather
import com.example.agritech.ui.theme.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewArticles(
    appViewModel: AppViewModel = viewModel(),
    goBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Articles",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = Poppins
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { goBack() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24dp),
                            contentDescription = "Go Back",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.notifications_24dp),
                            contentDescription = "View Notifications",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search for articles",
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val todaysWeather by appViewModel.todaysWeather.collectAsState()
            val selectedLocation by appViewModel.selectedLocation.collectAsState()

            WeatherCard(
                location = selectedLocation,
                todaysWeather = todaysWeather,
                recommendation = null,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(12.dp),
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        ) {
                            Text(
                                "Articles",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins
                            )
                        }
                    }
                    items(notes, key = { it.id }) { note ->
                        NotesCard(note)
                    }
                }
            }
        }
    }
}


@Composable
fun WeatherCard(
    location: String?,
    todaysWeather: Weather?,
    recommendation: String?,
) {
    val currentDateTime by remember { mutableStateOf(getCurrentDateTime()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "${parseLocation(location) ?: "Unknown Location"}, $currentDateTime",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Poppins
                    )
                    Spacer(Modifier.height(12.dp))
                    Row {
                        Text(
                            "${todaysWeather?.temp ?: 0}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Poppins
                        )
                        Text(
                            "°C",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = Poppins,
                        )
                    }
                    Text(
                        "Humidity ${todaysWeather?.humidity ?: 0.0}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Poppins
                    )
                }

                val weatherIcon: Int = remember { getWeatherIcon(todaysWeather?.conditions) }

                Image(
                    painter = painterResource(weatherIcon),
                    contentDescription = "Weather",
                    modifier = Modifier.size(124.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = MaterialTheme.colorScheme.onPrimary)
            )
            Text(
                recommendation ?: "No Recommendations",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun NotesCard(
    note: Note,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = rememberAsyncImagePainter(note.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
                Text(
                    note.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Preview(
    showBackground = true, device = Devices.PIXEL_5,
)
@Composable
fun PreviewNotesCard() {
    val note = Note(
        id = 1,
        title = "From Seed to Sip. The Journey of a Tea Plant",
        description = "Explore the life cycle of a tea crop, from careful seed selection and nursery practices to the first flush of leaves ready for harvest.",
        imageUrl = R.drawable.tea
    )

    NotesCard(note)
}

@Preview(
    showBackground = true, device = Devices.PIXEL_5,
)
@Composable
fun PreviewApp() {
    ViewArticles(
        goBack = {},
    )
}