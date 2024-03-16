import java.text.SimpleDateFormat
import java.util.Date

val dateFormat = "yyyyMMdd"

fun getCurrentDate(): String {
    val date = Date(System.currentTimeMillis())
    val simpleDateFormat = SimpleDateFormat(dateFormat)

    return simpleDateFormat.format(date)
}