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

    private fun pieChart(data: List<Float>, names: List<String>, canvas: Canvas)
    {
        // константы расположения рисунка
        val spread = 5f
        val leftBound = 100f
        val rightBound = 300f
        val upBound = 100f
        val downBound = 300f
        val xDescription = 350f
        val yDescription = 50f
        val sizeDescription = 10f
        val sizeDescriptionBound = 3f
        val yOffset = 25f

        val cnt = (data.size - 1)
        var i = 0
        val sumData = data.sum()
        var currentSum = 0f
        canvas.drawCircle((leftBound + rightBound) * 0.5f, (upBound + downBound) * 0.5f,
            (rightBound - leftBound) * 0.5f + spread * 2f,
            Paint().setARGB(255,0,0,0).setStrokeWidth(0.5f * spread))
        data.forEach{
            // отрисовка диаграммы
            canvas.drawArc(leftBound + spread * cos((currentSum + 0.5f * it)/sumData * PI * 2f).toFloat(),
                upBound + spread * sin((currentSum + 0.5f * it)/sumData * PI * 2f).toFloat(),
                rightBound + spread * cos((currentSum + 0.5f * it)/sumData * PI * 2f).toFloat(),
                downBound + spread * sin((currentSum + 0.5f * it)/sumData * PI * 2f).toFloat(),
                currentSum/sumData * 360f,
                it/sumData * 360f,true,
                Paint().setARGB(255,255 * i / cnt,255 * (cnt - i) / cnt,85*(i % 4)))

            // отрисовка подписей
            val rectBound = Rect(xDescription - sizeDescriptionBound,
                yDescription + i * yOffset - sizeDescriptionBound,
                xDescription + sizeDescription + sizeDescriptionBound,
                yDescription + sizeDescription + i * yOffset + sizeDescriptionBound)
            canvas.drawRect(rectBound, Paint().setARGB(255,0,0,0))
            val rect = Rect(xDescription, yDescription + i * yOffset,
                xDescription + sizeDescription, yDescription + sizeDescription + i * yOffset)
            canvas.drawRect(rect, Paint().setARGB(255,255 * i / cnt,255 * (cnt - i) / cnt,85*(i % 4)))

            canvas.drawString("${names[i]} - ${"%.${2}f".format(it / sumData * 100f)}%",
                xDescription + sizeDescription * 2f,
                yDescription + sizeDescription + i * yOffset,
                font, Paint().setARGB(255,0,0,0))
            currentSum += it
            i++
        }

    }

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        val w = (width / contentScale).toInt()
        val h = (height / contentScale).toInt()


        // РИСОВАНИЕ
        // Круговая диаграмма
        pieChart(listOf(1f,2f,3f,4f,5f,6f,7f,8f,9f,10f,11f,12f,13f,14f,15f,16f,17f,18f,19f),
            listOf("1", "2", "3","4", "5", "6","7", "8", "9", "10", "11", "12", "13","14", "15", "16","17", "18", "19"),
            canvas)

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