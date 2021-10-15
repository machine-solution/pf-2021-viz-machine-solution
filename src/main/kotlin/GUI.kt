import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File
import java.lang.System.exit
import kotlin.system.exitProcess

@Composable
fun simpleButton(colorful: Boolean, name: String, onClick : () -> Unit) {
    val color = if (colorful)
        ButtonDefaults.buttonColors(backgroundColor = Color.Red, contentColor = Color.Black)
    else
        ButtonDefaults.buttonColors(backgroundColor = Color.Blue, contentColor = Color.White)
    Button(colors = color, onClick = onClick) {Text(name)}
}

fun isCorrectPath(path: String): String {
    if (path.isEmpty())
        return "The path cannot be empty..."
    if (!File(path).isFile)
        return "The path is invalid..."
    return "The path is correct"
}


fun gui() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "viz",
        state = rememberWindowState(width = 850.dp, height = 400.dp)
    ) {
        val path = mutableStateOf("")
        // фиктивная костыльная переменная
        val i = mutableStateOf(0)
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                // фиктивная кнопка никогда не выведется, зато отрисовка работает правльно
                if (i.value < 0)
                    simpleButton(true,"fiction"){}
                simpleButton(chart == Chart.PIE_CHART, "Pie Chart"){
                    chart = Chart.PIE_CHART
                    // фиктивная переменная
                    i.value = 1 - i.value
                }
                simpleButton(chart == Chart.HISTOGRAM, "Histogram"){
                    chart = Chart.HISTOGRAM
                    // фиктивная переменная
                    i.value = 1 - i.value
                }
                simpleButton(chart == Chart.DISPERSION_CHART, "Dispersion Chart"){
                    chart = Chart.DISPERSION_CHART
                    // фиктивная переменная
                    i.value = 1 - i.value
                }
                simpleButton(chart == Chart.PETAL_CHART, "Petal Chart"){
                    chart = Chart.PETAL_CHART
                    // фиктивная переменная
                    i.value = 1 - i.value
                }
                simpleButton(chart == Chart.GRAPH_CHART, "Graph Chart"){
                    chart = Chart.GRAPH_CHART
                    // фиктивная переменная
                    i.value = 1 - i.value
                }
                simpleButton(false, "Clear Screen"){
                    chart = Chart.NULL
                    // фиктивная переменная
                    i.value = 1 - i.value
                }
                simpleButton(false, "Save"){
                    needSave = true;
                }
                simpleButton(false, "Exit"){
                    exitProcess(0)
                }
            }
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp), Alignment.End) {
                if (chart != Chart.NULL) {
                    TextField(
                        value = path.value,
                        onValueChange = {
                            path.value = it
                        },
                        label = { Text("Enter a path to data file")}
                    )
                    val log = isCorrectPath(path.value)
                    if (log != "The path is correct") {
                        Text(
                            text = isCorrectPath(path.value),
                            color = Color.Red,
                        )
                    } else {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green, contentColor = Color.Black),
                            onClick = {
                                when (chart) {
                                    Chart.PIE_CHART -> pieChartData = readPieChartData(path.value)
                                    Chart.HISTOGRAM -> pieChartData = readPieChartData(path.value)
                                    Chart.DISPERSION_CHART -> dispersionChartData = readDispersionChartData(path.value)
                                    Chart.PETAL_CHART -> petalChartData = readPetalChartData(path.value)
                                    Chart.GRAPH_CHART -> graphChartData = readGraphChartData(path.value)
                                }
                                path.value = ""
                            }
                        ) {
                            Text("Paint the diagram")
                        }
                    }
                }
            }
        }
    }
}