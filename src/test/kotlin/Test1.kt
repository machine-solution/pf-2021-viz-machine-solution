import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// сравнение изображений попиксельно
fun compareImages(name1: String, name2: String):Boolean {
    val imgA: BufferedImage = ImageIO.read(File(name1))
    val imgB: BufferedImage = ImageIO.read(File(name2))

    // сравнение размеров
    if (imgA.width == imgB.width && imgA.height == imgB.height) {
        val width = imgA.width
        val height = imgA.height

        // перебор пикселей
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false
                }
            }
        }
    } else {
        return false
    }
    return true
}

// сложность тестирования отрисовки в том, что она работает асинхронно,
// поэтому между отрисовкой, сохранением и проверкой должно пройти какое-то время,
// которое нельзя угадать заранее для первого промежутка. Если его будет недостаточно -
// тест будет не пройден
internal class TestOutput {

    @Test
    fun test() {
        pieChartData = readPieChartData("example_input/example_pie_chart.txt")

        createWindow("draw area")
        chart = Chart.PIE_CHART
        repeat(10000) {println("testing")} // нужно ждать отрисовки
        needSave = true
        while (needSave) {println("testing")} // нужно ждать, пока картинка сохранится
        assertTrue(compareImages("output.png", "Documentation_description/example_picture/pie_chart.png"))
    }
}

internal class TestInput {

    @Test
    fun incorrectInput() {
        // не существующий файл
        assertEquals(PieChartData(), readPieChartData("incorrect_input_text/pie_chart_test.txt"))
        // вещественное число написано через запятую, а не точку
        assertEquals(PieChartData(), readPieChartData("incorrect_input_text/histogram_test.txt"))
        // одно число на строке
        assertEquals(DispersionChartData(), readDispersionChartData("incorrect_input_text/dispersion_chart_test.txt"))
        // разное количество чисел на строке
        assertEquals(PetalChartData(), readPetalChartData("incorrect_input_text/petal_chart_test.txt"))
        // пустая строка посреди файла == 0 чисел в строке
        assertEquals(GraphChartData(), readGraphChartData("incorrect_input_text/graph_chart_test.txt"))
    }
}
