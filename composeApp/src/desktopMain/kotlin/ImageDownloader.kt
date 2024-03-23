import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import model.Whisky
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

class ImageDownloader {
    suspend fun downloadImages(whiskies: List<Whisky>): Boolean = withContext(Dispatchers.IO) {
        try {
            initDirectories()
            whiskies.map {
                async { downloadOriginImage(id = it.id, imageUrl = it.imageUrl) }
            }.awaitAll()
            removeBackGround()
            convertWebp()
            cleanDirectories()
            true
        } catch (e: Exception) {
            println("download Error : $e")
            false
        }
    }

    private fun initDirectories() {
        listOf(DIR_ORIGIN, DIR_REMOVE_GB, DIR_IMAGES)
            .forEach {
                File(it).apply {
                    deleteRecursively()
                    mkdir()
                }
            }
    }

    private fun cleanDirectories() {
        listOf(DIR_ORIGIN, DIR_REMOVE_GB)
            .forEach {
                File(it).deleteRecursively()
            }
    }

    private fun downloadOriginImage(id: Long, imageUrl: String) {
        val fileName = "${id}.webp"
        val url = URL(imageUrl)
        url.openStream().use { Files.copy(it, Paths.get("$DIR_ORIGIN/$fileName")) }
    }

    private fun removeBackGround() {
        val process = ProcessBuilder().command("rembg", "p", DIR_ORIGIN, DIR_REMOVE_GB).start()
        process.waitFor()
    }

    private suspend fun convertWebp() {
        coroutineScope {
            File(DIR_REMOVE_GB).listFiles()?.map { file ->
                async {
                    convertWebp(
                        source = file.absolutePath,
                        output = "$DIR_IMAGES/${file.nameWithoutExtension}.webp"
                    )
                }
            }?.awaitAll()
        }
    }

    private fun convertWebp(source: String, output: String) {
        val process = ProcessBuilder().command("cwebp",source,"-o",output).start()
        process.waitFor()
    }

    companion object {
        private const val DIR_ORIGIN = "origin"
        private const val DIR_REMOVE_GB = "removeBg"
        private const val DIR_IMAGES = "images"
    }

}