package com.example.agritech.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.agritech.R
import com.example.agritech.data.Route
import com.example.agritech.ui.theme.AgriTechTheme
import com.example.agritech.ui.theme.Poppins
import kotlinx.coroutines.delay

fun gifImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context).components {
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
    }.build()
}

@Composable
fun SplashScreen(
    navigateTo: (Route) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            val context = LocalContext.current
            val imageLoader = remember { gifImageLoader(context) }
            val loadingActivities by remember {
                mutableStateOf(
                    listOf<String>(
                        "🐄 Moo-ving data from the cloud to your farm...",
                        "🌽 Planting bits... Harvesting bytes...",
                        "🐓 Counting chickens before they hatch...",
                        "🚜 Plowing through data fields...",
                        "🧑🏾‍🌾 Asking Mama Mboga for the price of tomatoes..."
                    )
                )
            }
            var currentActivity by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                while (true) {
                    delay(4000)
                    currentActivity = loadingActivities.random()
                }
            }

            LaunchedEffect(Unit) {
                delay(8000)
                navigateTo(Route.WeeklyForecast)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Agri Tech",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
//                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = Poppins,
                )
                Text(
                    "Your all in one farming solution",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center,
                )

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(R.raw.growing_plant)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = null,
                    modifier = Modifier.size(420.dp),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        currentActivity ?: "Loading...",
                        modifier = Modifier.padding(horizontal = 64.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = Poppins,
                        textAlign = TextAlign.Center,
                    )
                    LinearProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface.copy(
                            alpha = 0.6f
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewSplashScreen() {
    AgriTechTheme {
        SplashScreen(
            navigateTo = {}
        )
    }
}