package com.example.agritech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.agritech.data.WeatherViewModel
import com.example.agritech.screens.WeeklyForecast
import com.example.agritech.ui.theme.AgriTechTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriTechTheme {
                WeeklyForecast(
                    weatherViewModel = WeatherViewModel()
                )
            }
        }
    }
}
