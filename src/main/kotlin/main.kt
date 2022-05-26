import kotlinx.coroutines.*
import java.nio.file.Path
import kotlin.io.path.*


const val MAX_SIZE_BYTES = 128 * 1024
const val filePath = "C:\\Users\\petro\\IdeaProjects\\os_practice_6\\src"
const val MAX_LINE_SIZE = 120
const val imageHeight = 12
const val imageWidth = 32

var isWorking = true

@ObsoleteCoroutinesApi
fun main() {
    println("Вариант 8\n")

    CoroutineScope(Dispatchers.IO).launch {
        println("Для остановки программы введите q")
        while (readLine() != "q")
            continue

        isWorking = false
    }

    runProgram()
}


fun runProgram() {
    var countFiles = 0
    val help = Path(filePath, "test", "resources", "Help.txt")

    while (isWorking) {
        countFiles++
        val fileName = "$countFiles-й файл"
        val file = getFile(workingFileName(countFiles))
        file.createIfNotExists(countFiles)

        println("$fileName создан и находится в работе")
        file.writeText("")

        var lineSize = 0
        var currentLine = 0

        while (file.fileSize() < MAX_SIZE_BYTES) {
            if (lineSize + imageWidth + 1 > MAX_LINE_SIZE) {
                file.appendText("\n")
                lineSize = 0
                currentLine++
            }

            val text = help.readLines()[currentLine % imageHeight] + "⠄"
            file.appendText(text)
            lineSize += text.length
        }

        file.renameFile("$countFiles-й файл (В ожидании)")
        println("$fileName заполнился до предела и теперь находится в ожидании")
    }

    runBlocking {
        for (i in 1..countFiles) {
            withContext(Dispatchers.IO) {
                getFile("$i-й файл (В ожидании)").deleteIfExists()
                println("$i-й файл удалён")
            }
            delay(1000)
        }
    }


}


fun Path.renameFile(newName: String) {
    val base = parent
    moveTo(Path(base.pathString, "$newName.txt"))
}

fun getFile(fileName: String) =
    Path(filePath, "main", "resources", "$fileName.txt")

fun Path.createIfNotExists(index: Int) {
    val file = getFile(waitingFileName(index))
    if (file.exists())
        file.renameFile(workingFileName(index))
    else
        createFile()
}

fun waitingFileName(index: Int) = "$index-й файл (В ожидании)"
fun workingFileName(index: Int) = "$index-й файл (В работе)"

