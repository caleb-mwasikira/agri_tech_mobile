package com.example.agritech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agritech.data.AppViewModel
import com.example.agritech.data.Route
import com.example.agritech.screens.ForgotPasswordScreen
import com.example.agritech.screens.HomeScreen
import com.example.agritech.screens.LoginScreen
import com.example.agritech.screens.SignUpScreen
import com.example.agritech.screens.SplashScreen
import com.example.agritech.screens.ViewArticles
import com.example.agritech.ui.theme.AgriTechTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "AgriTech"

        const val SHARED_PREFERENCES: String = "SHARED_PREFERENCES"
        const val USERNAME: String = "USERNAME"
        const val EMAIL: String = "EMAIL"
        const val PIN: String = "PIN"
        const val ACCESS_TOKEN: String = "ACCESS_TOKEN"

        lateinit var instance: MainActivity
            private set
    }

    private val appViewModel = AppViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the application instance to get application context
        instance = this

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
                    composable(Route.SignUpScreen.name) {
                        SignUpScreen(
                            navigateTo = { route ->
                                navController.navigate(route.name)
                            }
                        )
                    }
                    composable(Route.LoginScreen.name) {
                        LoginScreen(
                            navigateTo = { route ->
                                navController.navigate(route.name)
                            }
                        )
                    }
                    composable(Route.ForgotPasswordScreen.name) {
                        ForgotPasswordScreen(
                            navigateTo = { route ->
                                navController.navigate(route.name)
                            },
                            goBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(Route.HomeScreen.name) {
                        HomeScreen(
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
