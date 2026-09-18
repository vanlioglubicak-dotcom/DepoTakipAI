package com.depotakipai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.depotakipai.ui.navigation.AppNavigation
import com.example.depotakipai.ui.splash.SplashScreen
import com.example.depotakipai.ui.theme.DepoTakipAITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DepoTakipAITheme {

                var showSplash by remember {
                    mutableStateOf(true)
                }

                if (showSplash) {

                    SplashScreen(
                        onSplashFinished = {
                            showSplash = false
                        }
                    )

                } else {

                    AppNavigation()
                }
            }
        }
    }
}