import item.ItemBase

class Inventory(val items: MutableList<ItemBase> = mutableListOf()) {
  fun getItemByKeyword(word: String) = items.firstOrNull { item ->
    item.keywords.contains(word)
  }

  inline fun <reified T> getTypedItemByKeyword(word: String): T? {
    return items.firstOrNull { item ->
      item is T && item.keywords.contains(word)
    } as? T
  }

  override fun toString(): String {
    return when (items.size) {
      0 -> ""
      // one item: "a pigeon"
      1 -> items.first().nameWithIndefiniteArticle
      // two items: "an apple and an orange"
      2 -> items.joinToString(" and ") { item -> item.nameWithIndefiniteArticle }
      else -> {
        // more than two items; e.g. "a cat, a hat, a banana, and an iguana"

        // start with simple joined string: "a cat, a bagel, an apple, an orange, a hat"
        val mergedItemsString = items.joinToString(", ") { item -> item.nameWithIndefiniteArticle }
        // insert "and " before last item
        mergedItemsString.substringBeforeLast(", ") + ", and " + mergedItemsString.substringAfterLast(
          ", ")
      }
    }
  }
}