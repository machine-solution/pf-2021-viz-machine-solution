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
import java.lang.System.exit
import kotlin.system.exitProcess

fun gui() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "viz",
        state = rememberWindowState(width = 300.dp, height = 400.dp)
    ) {
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        chart = Chart.PIE_CHART
                    }
                ) {
                    Text("Pie Chart")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        chart = Chart.HISTOGRAM
                    }
                ) {
                    Text("Histogram")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        chart = Chart.DISPERSION_CHART
                    }
                ) {
                    Text("Dispersion Chart")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        chart = Chart.PETAL_CHART
                    }
                ) {
                    Text("Petal Chart")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        chart = Chart.GRAPH_CHART
                    }
                ) {
                    Text("Graph Chart")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        chart = Chart.NULL
                    }
                ) {
                    Text("Reset")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        exitProcess(0)
                    }
                ) {
                    Text("Exit")
                }
            }
        }
    }
}