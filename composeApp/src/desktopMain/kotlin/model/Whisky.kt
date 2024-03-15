package model

data class Whisky(
    val id: Long,
    val koName: String,
    val enName: String,
    val country: String,
    val category: String,
    val abv: String,
    val imageUrl: String,
) {
    override fun toString(): String = listOf(id, koName, enName, country, category, abv).joinToString(separator = " | ")

}