package com.example.agritech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agritech.data.Route
import com.example.agritech.data.AppViewModel
import com.example.agritech.screens.ViewArticles
import com.example.agritech.screens.SplashScreen
import com.example.agritech.screens.WeeklyForecast
import com.example.agritech.ui.theme.AgriTechTheme

class MainActivity : ComponentActivity() {
    private val appViewModel = AppViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriTechTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Route.SplashScreen.name) {
                    composable(Route.SplashScreen.name) {
                        SplashScreen(
                            navigateTo = { route ->
                                navController.navigate(route.name)
                            }
                        )
                    }
                    composable(Route.WeeklyForecast.name) {
                        WeeklyForecast(
                            appViewModel = appViewModel,
                            navigateTo = { route ->
                                navController.navigate(route.name)
                            }
                        )
                    }
                    composable(Route.ViewArticles.name) {
                        ViewArticles(
                            appViewModel = appViewModel,
                            goBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
