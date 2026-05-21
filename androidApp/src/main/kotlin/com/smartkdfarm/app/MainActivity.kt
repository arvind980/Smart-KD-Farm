package com.smartkdfarm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.smartkdfarm.app.auth.AuthAppScreen
import com.smartkdfarm.app.di.SharedContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val viewModel = SharedContainer().authViewModel()
        setContent {
            AuthAppScreen(viewModel = viewModel)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
