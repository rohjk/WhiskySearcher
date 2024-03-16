import model.Whisky
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class CsvGenerator {
    companion object {
        private const val SUPABASE_ID = "ghsleideklxvllufjyhc"
    }

    fun export(whiskies: List<Whisky>, storeDir: String) {
        FileOutputStream("export_whisky_${getCurrentDate()}.csv").writeCsv(whiskies, storeDir)
    }

    private fun OutputStream.writeCsv(whiskies: List<Whisky>, storeDir: String) {
        val writer = bufferedWriter(StandardCharsets.UTF_8)
        writer.write("""legacy_id,en_name,ko_name,country,category,abv,image_url""")
        writer.newLine()
        whiskies.forEach { whisky ->
            val imageUrl =
                "https://${SUPABASE_ID}.supabase.co/storage/v1/object/public/whiskies/${storeDir}/${whisky.id}.webp"
            writer.write("${whisky.id},${whisky.enName},${whisky.koName},${whisky.country},${whisky.category},${whisky.abv},${imageUrl}")
            writer.newLine()
        }
        writer.flush()
    }
}