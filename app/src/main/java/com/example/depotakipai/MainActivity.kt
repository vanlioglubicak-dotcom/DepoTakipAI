package com.depotakipai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.depotakipai.ui.splash.SplashScreen
import com.example.depotakipai.ui.theme.DepoTakipAITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DepoTakipAITheme {

                SplashScreen(
                    onSplashFinished = {
                        // Şimdilik boş.
                        // Splash ekranını test ediyoruz.
                    }
                )
            }
        }
    }
}