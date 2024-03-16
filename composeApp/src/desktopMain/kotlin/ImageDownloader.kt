import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import model.Whisky
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

class ImageDownloader {
    suspend fun downloadImages(whiskies: List<Whisky>): Boolean = withContext(Dispatchers.IO) {
        try {
            whiskies.map {
                async { downloadOriginImage(id = it.id, imageUrl = it.imageUrl) }
            }.awaitAll()
            removeBackGround()
            convertWebp()
            true
        } catch (e: Exception) {
            println("download Error : ${e}")
            false
        }
    }

    private fun downloadOriginImage(id: Long, imageUrl: String) {
        File("origin").mkdir()
        val fileName = "${id}.webp"
        val url = URL(imageUrl)
        url.openStream().use { Files.copy(it, Paths.get("origin/$fileName")) }
        println("download done")
    }

    private fun removeBackGround() {
        val process = ProcessBuilder().command("rembg", "p", "origin", "removeBg").start()
        process.inputStream.reader(Charsets.UTF_8).use {
            println(it.readText())
        }
        process.waitFor()
    }

    private fun convertWebp() {
        File("images").mkdir()
        val process = ProcessBuilder().command("sh","convert_png_toWebp.sh").start()
        process.inputStream.reader(Charsets.UTF_8).use {
            println(it.readText())
        }
        process.waitFor()
    }
}