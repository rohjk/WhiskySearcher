import androidx.compose.runtime.key
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import model.Whisky
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchRepository {
    val gson = Gson()

    suspend fun search(keyword: String): List<Whisky> = withContext(Dispatchers.IO) {
        val url = URL("https://api.dailyshot.co/items/search/?q=${keyword}&page=1&page_size=100")
        val connection = url.openConnection() as HttpURLConnection

        val streamReader = InputStreamReader(connection.inputStream)
        val bufferedReader = BufferedReader(streamReader)

        val content = bufferedReader.readText()

        bufferedReader.close()
        connection.disconnect()

        val entities = gson.fromJson(content, ResponseDto::class.java).result

        entities.map { async { it.toWhisky() } }.awaitAll().filterNotNull()
    }

    private fun WhiskyEntity.toWhisky(): Whisky? {
        try {
            val contentUrl = URL(contentUrl)

            val connection = contentUrl.openConnection() as HttpURLConnection

            val streamReader = InputStreamReader(connection.inputStream)
            val bufferedReader = BufferedReader(streamReader)

            val content = StringBuilder()
            while (true) {
                val line = bufferedReader.readLine() ?: break
                content.append(line)
            }

            val domParser = Jsoup.parse(content.toString())
            val informations = domParser.getElementsByClass("information-row")

            var abv = ""
            var country = ""
            var category = ""
            val enName = domParser.getElementsByClass("en-name").text()
            informations.forEach {
                when(it.getElementsByClass("label").text()) {
                    "종류" -> { category = it.getElementsByClass("content").text() }
                    "도수" -> { abv = it.getElementsByClass("content").text() }
                    "국가" -> { country = it.getElementsByClass("content").text() }
                }
            }

            return Whisky(
                id = id,
                koName = name,
                enName = enName,
                abv = abv,
                country = country,
                category = category,
                imageUrl = imageUrl
            )
        } catch (e: Exception) {
            return null
        }
    }
}

data class ResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("results") val result: List<WhiskyEntity>
)

data class WhiskyEntity(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("thumbnail_image") val imageUrl: String,
    @SerializedName("web_url") val contentUrl: String,
)
