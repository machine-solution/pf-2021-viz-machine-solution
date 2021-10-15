import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import org.jetbrains.skiko.toBufferedImage
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.io.File
import javax.imageio.ImageIO
import javax.swing.WindowConstants
import kotlin.math.*


enum class Chart
{
    NULL,
    PIE_CHART,
    HISTOGRAM,
    DISPERSION_CHART,
    PETAL_CHART,
    GRAPH_CHART,
}

// глобальные данные к графикам
// pieChartData содержит данные и для histogram
var pieChartData = PieChartData()
var dispersionChartData = DispersionChartData()
var petalChartData = PetalChartData()
var graphChartData = GraphChartData()
var chart = Chart.NULL
var needSave = false

// сохраняет картинку в указанный png файл
fun savePicture(fileName: String, layer: SkiaLayer)
{
    val bitmap = layer.screenshot() ?: return
    val image = bitmap.toBufferedImage()
    val outputFile = File(fileName)
    ImageIO.write(image, "png", outputFile)
}

data class Rose(val values: List<Float> = listOf())

data class Graph(val arguments: List<Float> = listOf(), val values: List<Float> = listOf())

data class PieChartData(val values: List<Float> = listOf(), val names: List<String> = listOf())

data class DispersionChartData(val x: List<Float> = listOf(), val y: List<Float> = listOf())

data class PetalChartData(val roses: List<Rose> = listOf())

data class GraphChartData(val graphs: List<Graph> = listOf())

// считывание данных из файла
fun readPieChartData(path: String):PieChartData
{
    val strings = File(path).readLines()
    val values = mutableListOf<Float>()
    val names = mutableListOf<String>()
    var i = 1
    strings.forEach{
        val s = it.split(" ")
        values.add(s[0].toFloat())
        if (s.size > 1)
            names.add(it.substring(s[0].length))
        else
            names.add("unnamed data #${i++}")
    }
    return PieChartData(values, names)
}

fun readDispersionChartData(path: String):DispersionChartData
{
    val strings = File(path).readLines()
    val x = mutableListOf<Float>()
    val y = mutableListOf<Float>()
    strings.forEach{
        val s = it.split(" ")
        x.add(s[0].toFloat())
        y.add(s[1].toFloat())
    }
    return DispersionChartData(x, y)
}

fun readPetalChartData(path: String):PetalChartData
{
    val strings = File(path).readLines()
    val roses = mutableListOf<Rose>()
    strings.forEach{
        val s = it.split(" ")
        val values = s.map{ i -> i.toFloat() }
        roses.add(Rose(values))
    }
    return PetalChartData(roses)
}

fun readGraphChartData(path: String):GraphChartData
{
    val strings = File(path).readLines()
    val graphs = mutableListOf<Graph>()
    var i = 0
    while(i + 1 < strings.size){
        val sArgs = strings[i].split(" ")
        val arguments = sArgs.map{ it.toFloat() }
        val sValues = strings[i + 1].split(" ")
        val values = sValues.map{ it.toFloat() }
        graphs.add(Graph(arguments, values))
        i += 2
    }
    return GraphChartData(graphs)
}

fun main() {

    pieChartData = readPieChartData("example_pie_chart.txt")
    dispersionChartData = readDispersionChartData("example_dispersion.txt")
    petalChartData = readPetalChartData("example_petal_chart.txt")
    graphChartData = readGraphChartData("example_graph_chart.txt")

    createWindow("draw area")

    gui()
}

fun createWindow(title: String) = runBlocking(Dispatchers.Swing) {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer)
    window.layer.addMouseMotionListener(MyMouseMotionAdapter)

    window.preferredSize = Dimension(800, 600)
    window.minimumSize = Dimension(100,100)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true
}

class Renderer(private val layer: SkiaLayer): SkiaRenderer {
    private val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    private val font = Font(typeface, 16f)

    private fun paint(id: Int): Paint
    {
        return when (id)
        {
            0 -> Paint().setARGB(255,255,0,0)
            1 -> Paint().setARGB(255,0,255,0)
            2 -> Paint().setARGB(255,0,0,255)

            3 -> Paint().setARGB(255,255,0,255)
            4 -> Paint().setARGB(255,255,255,0)
            5 -> Paint().setARGB(255,0,255,255)

            6 -> Paint().setARGB(255,255,127,127)
            7 -> Paint().setARGB(255,127,255,127)
            8 -> Paint().setARGB(255,127,127,255)

            9 -> Paint().setARGB(255,255,127,0)
            10 -> Paint().setARGB(255,127,255,0)
            11 -> Paint().setARGB(255,0,127,255)

            12 -> Paint().setARGB(255,255,0,127)
            13 -> Paint().setARGB(255,0,255,127)
            14 -> Paint().setARGB(255,127,0,255)

            15 -> Paint().setARGB(255,127,0,0)
            16 -> Paint().setARGB(255,0,127,0)
            17 -> Paint().setARGB(255,0,0,127)

            18 -> Paint().setARGB(255,127,0,127)
            19 -> Paint().setARGB(255,127,127,0)
            20 -> Paint().setARGB(255,0,127,127)

            else -> Paint().setARGB(255,0,0,0)
        }
    }

    private fun toAlpha(paint: Paint): Paint
    {
        return paint.setAlpha(17 * 6)
    }

    private fun pieChart(data: PieChartData, canvas: Canvas)
    {
        // константы расположения рисунка
        val spread = 5f
        val leftBound = 100f
        val rightBound = 300f
        val upBound = 100f
        val downBound = 300f
        val xDescription = 350f
        val yDescription = 25f
        val sizeDescription = 10f
        val sizeDescriptionBound = 3f
        val yOffset = 25f

        var i = 0
        val sumData = data.values.sum()
        var currentSum = 0f
        canvas.drawCircle((leftBound + rightBound) * 0.5f, (upBound + downBound) * 0.5f,
            (rightBound - leftBound) * 0.5f + spread * 2f,
            paint(-1).setStrokeWidth(0.5f * spread))
        data.values.forEach{
            // отрисовка диаграммы
            canvas.drawArc(leftBound + spread * cos((currentSum + 0.5f * it)/sumData * PI * 2f).toFloat(),
                upBound + spread * sin((currentSum + 0.5f * it)/sumData * PI * 2f).toFloat(),
                rightBound + spread * cos((currentSum + 0.5f * it)/sumData * PI * 2f).toFloat(),
                downBound + spread * sin((currentSum + 0.5f * it)/sumData * PI * 2f).toFloat(),
                currentSum/sumData * 360f,
                it/sumData * 360f,true,
                paint(i))

            // отрисовка подписей
            val rectBound = Rect(xDescription - sizeDescriptionBound,
                yDescription + i * yOffset - sizeDescriptionBound,
                xDescription + sizeDescription + sizeDescriptionBound,
                yDescription + sizeDescription + i * yOffset + sizeDescriptionBound)
            canvas.drawRect(rectBound, paint(-1))
            val rect = Rect(xDescription, yDescription + i * yOffset,
                xDescription + sizeDescription, yDescription + sizeDescription + i * yOffset)
            canvas.drawRect(rect, paint(i))

            canvas.drawString("${data.names[i]} - ${"%.${2}f".format(it / sumData * 100f)}%",
                xDescription + sizeDescription * 2f,
                yDescription + sizeDescription + i * yOffset,
                font, paint(-1))
            currentSum += it
            i++
        }

    }

    private fun histogram(data: PieChartData, canvas: Canvas)
    {
        // константы расположения рисунка
        val leftBound = 100f
        val rightBound = 400f
        val upBound = 30f
        val bold = 3f
        val yOffset = 25f
        val height = 15f
        val indent = 15f

        val maxData = data.values.maxOrNull() ?: 0f
        val sumData = data.values.sum()
        var i = 0

        data.values.forEach{
            val rectBound = Rect(
                leftBound - bold,
                upBound + yOffset * i - bold,
                leftBound + (rightBound - leftBound) * (it / maxData) + bold,
                upBound + yOffset * i + height + bold
            )
            canvas.drawRect(rectBound, paint(-1))
            val rect = Rect(
            leftBound,
            upBound + yOffset * i,
            leftBound + (rightBound - leftBound) * (it / maxData),
            upBound + yOffset * i + height
            )
            canvas.drawRect(rect, paint(i))

            canvas.drawString("${data.names[i]} - ${"%.${2}f".format(it / sumData * 100f)}%",
                leftBound + (rightBound - leftBound) * (it / maxData) + indent,
                upBound + yOffset * i + height,
                font, paint(-1))

            ++i
        }
    }

    private fun dispersionChart(data: DispersionChartData, canvas: Canvas)
    {
        val leftBound = 50f
        val rightBound = 750f
        val upBound = 50f
        val downBound = 520f
        val broad = 20f
        val streak = 4.5f

        val minX = data.x.minOrNull() ?: 0f
        val maxX = data.x.maxOrNull() ?: 0f
        val minY = data.y.minOrNull() ?: 0f
        val maxY = data.y.maxOrNull() ?: 0f

        fun scaleX(x: Float): Float
        {
            return leftBound + (rightBound - leftBound)*( (x - minX) / (maxX - minX))
        }

        fun scaleY(y: Float): Float
        {
            return downBound + (upBound - downBound)*( (y - minY) / (maxY - minY))
        }
// граница
        canvas.drawLine(leftBound - broad, upBound - broad,
            leftBound - broad, downBound + broad, paint(0))
        canvas.drawLine(leftBound - broad, downBound + broad,
            rightBound + broad, downBound + broad, paint(0))
// единичные отрезки для наглядность отношения масштабов по координатам
        val lengthX = maxX - minX
        val lengthY = maxY - minY
        val c = 15f
        val step = sqrt(lengthX * lengthY / c)
        var x = minX
        var y = minY
        while (x <= maxX + step)
        {
            canvas.drawLine(scaleX(x) - broad, downBound + streak + broad,
                scaleX(x) - broad, downBound - streak + broad, paint(2).setStrokeWidth(2.5f))
            x += step
        }
        while (y <= maxY + step)
        {
            canvas.drawLine(leftBound + streak - broad, scaleY(y) + broad,
                leftBound - streak - broad,  scaleY(y) + broad, paint(2).setStrokeWidth(2.5f))
            y += step
        }

        for (it in data.x.indices)
        {
            canvas.drawPoint(scaleX(data.x[it]), scaleY(data.y[it]), paint(-1).setStrokeWidth(3f))
        }
    }

    private fun petalChart(data: PetalChartData, canvas: Canvas)
    {
        val leftBound = 175f
        val rightBound = 625f
        val upBound = 50f
        val downBound = 500f

        // отрисовка каркаса розы
        val centerX = (leftBound + rightBound) / 2
        val centerY = (upBound + downBound) / 2
        val radius = centerX - leftBound
        val radiusEnd = radius * 0.9f
// а если в файле разное количество значенией?
        val count = data.roses[0].values.size
        for (i in 0 until count)
        {
            canvas.drawLine(centerX, centerY,
            centerX + cos(i * 2 * PI / count).toFloat() * radius,
            centerY + sin(i * 2 * PI / count).toFloat() * radius,
            paint(-1).setStrokeWidth(3f))
        }

        val maxData = MutableList(count){ -1e9f }
        data.roses.forEach{
            for (i in 0 until count)
                maxData[i] = max(maxData[i], it.values[i])
        }

        var id = 0
        data.roses.forEach{
            for (i in 0 until count)
            {
                canvas.drawLine(
                    centerX + cos(i * 2 * PI / count).toFloat() * radiusEnd * it.values[i] / maxData[i],
                    centerY + sin(i * 2 * PI / count).toFloat() * radiusEnd * it.values[i] / maxData[i],
                    centerX + cos((i + 1) * 2 * PI / count).toFloat() * radiusEnd
                            * it.values[(i + 1) % count] / maxData[(i + 1) % count],
                    centerY + sin((i + 1) * 2 * PI / count).toFloat() * radiusEnd
                            * it.values[(i + 1) % count] / maxData[(i + 1) % count],
                    paint(id).setStrokeWidth(2f))
                val triangle = arrayOf(
                    Point(
                        centerX,centerY
                    ),
                    Point(
                        centerX + cos(i * 2 * PI / count).toFloat() * radiusEnd * it.values[i] / maxData[i],
                        centerY + sin(i * 2 * PI / count).toFloat() * radiusEnd * it.values[i] / maxData[i]
                    ),
                    Point(
                        centerX + cos((i + 1) * 2 * PI / count).toFloat() * radiusEnd
                                * it.values[(i + 1) % count] / maxData[(i + 1) % count],
                        centerY + sin((i + 1) * 2 * PI / count).toFloat() * radiusEnd
                                * it.values[(i + 1) % count] / maxData[(i + 1) % count]
                    )
                )
                canvas.drawTriangles(triangle, null, toAlpha(paint(id)))
                canvas.drawPoint(
                    centerX + cos(i * 2 * PI / count).toFloat() * radiusEnd * it.values[i] / maxData[i],
                    centerY + sin(i * 2 * PI / count).toFloat() * radiusEnd * it.values[i] / maxData[i],
                    paint(id).setStrokeWidth(8f))
            }
            id++
        }
    }

    private fun graphChart(data: GraphChartData, canvas: Canvas)
    {
        val leftBound = 50f
        val rightBound = 750f
        val upBound = 50f
        val downBound = 520f
        val broad = 20f
        val streak = 4.5f

        var minX = 1e9f
        var maxX = -1e9f
        var minY = 1e9f
        var maxY = -1e9f

        data.graphs.forEach{
            minX = min(minX, it.arguments.minOrNull()!!)
            minY = min(minY, it.values.minOrNull()!!)
            maxX = max(maxX, it.arguments.maxOrNull()!!)
            maxY = max(maxY, it.values.maxOrNull()!!)
        }

        fun scaleX(x: Float): Float
        {
            return leftBound + (rightBound - leftBound)*( (x - minX) / (maxX - minX))
        }

        fun scaleY(y: Float): Float
        {
            return downBound + (upBound - downBound) * ( (y - minY) / (maxY - minY))
        }
// граница
        canvas.drawLine(leftBound - broad, upBound - broad,
            leftBound - broad, downBound + broad, paint(-1))
        canvas.drawLine(leftBound - broad, downBound + broad,
            rightBound + broad, downBound + broad, paint(-1))
// единичные отрезки для наглядность отношения масштабов по координатам
        val lengthX = maxX - minX
        val lengthY = maxY - minY
        val c = 15f
        val step = sqrt(lengthX * lengthY / c)
        var x = minX
        var y = minY
        while (x <= maxX + step)
        {
            canvas.drawLine(scaleX(x) - broad, downBound + streak + broad,
                scaleX(x) - broad, downBound - streak + broad, paint(-1).setStrokeWidth(2.5f))
            x += step
        }
        while (y <= maxY + step)
        {
            canvas.drawLine(leftBound + streak - broad, scaleY(y) + broad,
                leftBound - streak - broad,  scaleY(y) + broad, paint(-1).setStrokeWidth(2.5f))
            y += step
        }

        var id = 0
        data.graphs.forEach{
            for (i in 0 .. (it.values.size - 2)) {
                canvas.drawLine(scaleX(it.arguments[i]), scaleY(it.values[i]),
                scaleX(it.arguments[i + 1]), scaleY(it.values[i + 1]),
                paint(id).setStrokeWidth(3f))
            }

            id++
        }
    }

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        val w = (width / contentScale).toInt()
        val h = (height / contentScale).toInt()


        // РИСОВАНИЕ
        canvas.drawRect(Rect(0f,0f,w * 1f,h * 1f), Paint().setARGB(255,255,255,255))
        // Отрисовка диаграмм
        when (chart) {
            Chart.PIE_CHART -> pieChart(pieChartData, canvas)
            Chart.HISTOGRAM -> histogram(pieChartData, canvas)
            Chart.DISPERSION_CHART -> dispersionChart(dispersionChartData, canvas)
            Chart.PETAL_CHART -> petalChart(petalChartData, canvas)
            Chart.GRAPH_CHART -> graphChart(graphChartData, canvas)
            Chart.NULL -> Unit
        }
        if (needSave)
        {
            savePicture("output.png", layer)
            needSave = false
        }

        //

        layer.needRedraw()
    }
}

object State {
    var mouseX = 0f
    var mouseY = 0f
}

object MyMouseMotionAdapter : MouseMotionAdapter() {
    override fun mouseMoved(event: MouseEvent) {
        State.mouseX = event.x.toFloat()
        State.mouseY = event.y.toFloat()
    }
}