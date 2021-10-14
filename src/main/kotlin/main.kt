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
import javax.swing.WindowConstants
import kotlin.math.*

enum class Chart
{
    NULL,
    PIE_CHART,
    HISTOGRAM,
}

val data = listOf(1f,2f,3f,4f,5f,6f,7f,8f,9f,10f,11f,12f,13f,14f,15f,16f,17f,18f,19f, 20f, 21f)
val names = listOf("1", "2", "3","4", "5", "6","7", "8", "9", "10",
    "11", "12", "13","14", "15", "16","17", "18", "19", "20", "21")
var chart = Chart.PIE_CHART

fun main() {
//    gui()
    createWindow("pf-2021-viz")
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

class Renderer(val layer: SkiaLayer): SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    val font = Font(typeface, 16f)
    val paint = Paint().apply {
        color = 0xff000000L.toInt()
        mode = PaintMode.FILL
        strokeWidth = 1f
    }

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

        val cnt = (data.size - 1)
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

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        val w = (width / contentScale).toInt()
        val h = (height / contentScale).toInt()


        // РИСОВАНИЕ
        // Круговая диаграмма
        when (chart) {
            Chart.PIE_CHART -> pieChart(data, names, canvas)
            Chart.HISTOGRAM -> histogram(data, names, canvas)
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