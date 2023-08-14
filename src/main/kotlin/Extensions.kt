import item.ItemBase

const val vowels: String = "aeiou"
fun isVowel(c: Char) = vowels.contains(c)

fun String.withIndefiniteArticle(capitalized: Boolean = false): String {
    // some words start with vowels but would still be "a <exception>"
    //  e.g. hard y sounds, like "a unicorn"
    val exceptions = arrayOf(
        "unicorn"
    )

    return if (isVowel(this[0]) && !exceptions.contains(this)) {
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
