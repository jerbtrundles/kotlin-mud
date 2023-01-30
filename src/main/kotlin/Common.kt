import com.beust.klaxon.Klaxon
import game.Game
import kotlinx.coroutines.delay
import kotlin.random.Random

object Common {
    private const val pollingInterval = 100L

    fun collectionString(
        itemStrings: List<String>,
        includeIndefiniteArticles: Boolean = true
    ): String {
        return when (itemStrings.size) {
            0 -> ""
            // one item: "a pigeon"
            1 -> if (includeIndefiniteArticles) {
                itemStrings.first().withIndefiniteArticle()
            } else {
                itemStrings.first()
            }
            // two items: "an apple and an orange"
            2 -> if (includeIndefiniteArticles) {
                itemStrings.joinToString(" and ") { itemString -> itemString.withIndefiniteArticle() }
            } else {
                itemStrings.joinToString(" and ")
            }

            else -> {
                // more than two items; e.g. "a cat, a hat, a banana, and an iguana"

                // start with simple joined string: "a cat, a bagel, an apple, an orange, a hat"
                val mergedItemsString = if (includeIndefiniteArticles) {
                    itemStrings.joinToString(", ") { itemString -> itemString.withIndefiniteArticle() }
                } else {
                    itemStrings.joinToString(", ")
                }

                // insert "and " before last item
                mergedItemsString.substringBeforeLast(", ") + ", and " + mergedItemsString.substringAfterLast(
                    ", "
                )
            }
        }
    }

    inline fun <reified T> parseArrayFromJson(c: Class<() -> Unit>, fileName: String): List<T> =
        Klaxon().parseArray(loadJson(c, fileName))!!

    fun loadJson(c: Class<() -> Unit>, fileName: String) =
        c.getResourceAsStream(fileName)?.bufferedReader()?.readText()!!

    suspend fun delayRandom(from: Int, to: Int) {
        val repeat = Random.nextInt(
            (from / pollingInterval).toInt(),
            (to / pollingInterval).toInt()
        )

        repeat(repeat) {
            if (Game.running) {
                delay(pollingInterval)
            }
        }
    }
}

fun String.withIndefiniteArticle(capitalized: Boolean = false): String {
    // exceptions where the string should still be "a <exception>"
    //  e.g. hard y sounds, like "a unicorn"
    val exceptions = arrayOf(
        "unicorn"
    )
    return if ("aeiou".contains(this[0]) && !exceptions.contains(this)) {
        if (capitalized) {
            "An $this"
        } else {
            "an $this"
        }
    } else {
        if (capitalized) {
            "A $this"
        } else {
            "a $this"
        }
    }
}
