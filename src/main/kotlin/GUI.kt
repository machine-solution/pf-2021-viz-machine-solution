import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun gui() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "viz",
        state = rememberWindowState(width = 1000.dp, height = 700.dp)
    ) {
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
//
            }
        }
    }
}