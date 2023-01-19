object Common {
    fun collectionString(items: List<String>): String {
        return when (items.size) {
            0 -> ""
            // one item: "a pigeon"
            1 -> items.first().withIndefiniteArticle()
            // two items: "an apple and an orange"
            2 -> items.joinToString(" and ") { item -> item.withIndefiniteArticle() }
            else -> {
                // more than two items; e.g. "a cat, a hat, a banana, and an iguana"

                // start with simple joined string: "a cat, a bagel, an apple, an orange, a hat"
                val mergedItemsString = items.joinToString(", ") { item -> item.withIndefiniteArticle() }
                // insert "and " before last item
                mergedItemsString.substringBeforeLast(", ") + ", and " + mergedItemsString.substringAfterLast(
                    ", "
                )
            }
        }
    }
}

fun String.withIndefiniteArticle(): String {
    return if ("aeiou".contains(this[0])) {
        "an $this"
    } else {
        "a $this"
    }
}
