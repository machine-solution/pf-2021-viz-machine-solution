import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.io.File
import javax.swing.WindowConstants
import kotlin.math.*
import kotlin.random.*

enum class Chart
{
    NULL,
    PIE_CHART,
    HISTOGRAM,
    DISPERSION_CHART,
    PETAL_CHART,
    GRAPH_CHART,
}

val data = listOf(1f,2f,3f,4f,5f,6f,7f,8f,9f,10f,11f,12f,13f,14f,15f,16f,17f,18f,19f, 20f, 21f)
val names = listOf("1", "2", "3","4", "5", "6","7", "8", "9", "10",
    "11", "12", "13","14", "15", "16","17", "18", "19", "20", "21")
val dataX = mutableListOf<Float>()
val dataY = mutableListOf<Float>()

val dataRose = mutableListOf(
    Rose(listOf(1f,2f,3f,4f,4f)),
    Rose(listOf(3f,1f,2f,5f,5f)),
    Rose(listOf(2f,3f,1f,6f,6f)),
)

val dataGraph = mutableListOf(
    Graph(listOf(1f,2f,3f), listOf(1f,1f,1f)),
    Graph(listOf(1f,2f,3f), listOf(1f,0f,1f)),
    Graph(listOf(1f,2f,3f), listOf(2f,4f,6f))
)
var chart = Chart.NULL

data class Rose(val values: List<Float>)

data class Graph(val arguments: List<Float>, val values: List<Float>)

fun main() {

    val lines = File("dispersion.txt").readLines()
    for (i in 1..1000) {
//        File("dispersion.txt").appendText("${Random.nextFloat()} ${Random.nextFloat()}\n")
        val xy = lines[i - 1].split(" ")
        dataX.add(xy[0].toFloat())
        dataY.add(xy[1].toFloat())
    }

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

    private fun pieChart(data: List<Float>, names: List<String>, canvas: Canvas)
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
        val sumData = data.sum()
        var currentSum = 0f
        canvas.drawCircle((leftBound + rightBound) * 0.5f, (upBound + downBound) * 0.5f,
            (rightBound - leftBound) * 0.5f + spread * 2f,
            paint(-1).setStrokeWidth(0.5f * spread))
        data.forEach{
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

            canvas.drawString("${names[i]} - ${"%.${2}f".format(it / sumData * 100f)}%",
                xDescription + sizeDescription * 2f,
                yDescription + sizeDescription + i * yOffset,
                font, paint(-1))
            currentSum += it
            i++
        }

    }

    private fun histogram(data: List<Float>, names: List<String>, canvas: Canvas)
    {
        // константы расположения рисунка
        val leftBound = 100f
        val rightBound = 400f
        val upBound = 30f
        val bold = 3f
        val yOffset = 25f
        val height = 15f
        val indent = 15f

        val maxData = data.maxOrNull() ?: 0f
        val sumData = data.sum()
        var i = 0

        data.forEach{
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

            canvas.drawString("${names[i]} - ${"%.${2}f".format(it / sumData * 100f)}%",
                leftBound + (rightBound - leftBound) * (it / maxData) + indent,
                upBound + yOffset * i + height,
                font, paint(-1))

            ++i
        }
    }

    private fun dispersionChart(dataX: List<Float>, dataY: List<Float>, canvas: Canvas)
    {
        val leftBound = 50f
        val rightBound = 750f
        val upBound = 50f
        val downBound = 520f
        val broad = 20f
        val streak = 4.5f

        val minX = dataX.minOrNull() ?: 0f
        val maxX = dataX.maxOrNull() ?: 0f
        val minY = dataY.minOrNull() ?: 0f
        val maxY = dataY.maxOrNull() ?: 0f

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

        for (it in dataX.indices)
        {
            canvas.drawPoint(scaleX(dataX[it]), scaleY(dataY[it]), paint(-1).setStrokeWidth(3f))
        }
    }

    private fun petalChart(count: Int, data: List<Rose>, canvas: Canvas)
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
        for (i in 0 until count)
        {
            canvas.drawLine(centerX, centerY,
            centerX + cos(i * 2 * PI / count).toFloat() * radius,
            centerY + sin(i * 2 * PI / count).toFloat() * radius,
            paint(-1).setStrokeWidth(3f))
        }

        val maxData = MutableList(count){ -1e9f }
        data.forEach{
            for (i in 0 until count)
                maxData[i] = max(maxData[i], it.values[i])
        }

        var id = 0
        data.forEach{
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

    private fun graphChart(count: Int, data: List<Graph>, canvas: Canvas)
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

        data.forEach{
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
        data.forEach{
            for (i in 0 .. (it.values.size - 2)) {
                canvas.drawLine(scaleX(it.arguments[i]), scaleY(it.values[i]),
                scaleX(it.arguments[i + 1]), scaleY(it.values[i + 1]),
                paint(id))
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
        // Отрисовка диаграмм
        when (chart) {
            Chart.PIE_CHART -> pieChart(data, names, canvas)
            Chart.HISTOGRAM -> histogram(data, names, canvas)
            Chart.DISPERSION_CHART -> dispersionChart(dataX, dataY, canvas)
            Chart.PETAL_CHART -> petalChart(dataRose[0].values.size, dataRose, canvas)
            Chart.GRAPH_CHART -> graphChart(dataGraph[0].values.size, dataGraph, canvas)
            Chart.NULL -> Unit
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