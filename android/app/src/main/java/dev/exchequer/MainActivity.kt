package dev.exchequer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.exchequer.ui.ExchequerApp
import dev.exchequer.ui.ExchequerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExchequerTheme {
                ExchequerApp()
            }
        }
    }
}
