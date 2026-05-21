package om.smartkdfarm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import om.smartkdfarm.app.auth.AuthAppScreen
import om.smartkdfarm.app.di.SharedContainer

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
