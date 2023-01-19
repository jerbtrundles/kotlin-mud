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

  override fun toString() = Common.collectionString(items.map { item -> item.name })
}