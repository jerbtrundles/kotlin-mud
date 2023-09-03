import item.ItemArmor
import item.ItemBase
import item.ItemWeapon
import java.util.*

class Inventory(
    val items: MutableList<ItemBase> = Collections.synchronizedList(mutableListOf())
) {
    fun getItemByKeyword(keyword: String) = items.firstOrNull { item ->
        item.keywords.contains(keyword)
    }

    inline fun <reified T> getTypedItemByKeyword(keyword: String): T? {
        return items.firstOrNull { item ->
            item is T && item.keywords.contains(keyword)
        } as? T
    }

    override fun toString() = collectionString
    val collectionString
        get() = Common.collectionString(items.map { item -> item.name })

    fun getAndRemoveRandomItem(): ItemBase? {
        return items.randomOrNull()?.let { item ->
            items.remove(item)
            item
        }
    }

    inline fun <reified T> getRandomTypedItem() = items.filter { item -> item is T }.randomOrNull() as? T
    inline fun <reified T> getAndRemoveRandomTypedItem(): T? {
        val item = getRandomTypedItem<T>()
        item?.let { items.remove(it as ItemBase) }
        return item
    }

    fun getRandomWeapon() = getRandomTypedItem<ItemWeapon>()
    fun getAndRemoveRandomWeapon() = getAndRemoveRandomTypedItem<ItemWeapon>()
    fun getAndRemoveRandomBetterWeapon(minPower: Int) =
        items.filterIsInstance(ItemWeapon::class.java).filter { it.power >= minPower }.randomOrNull()

    fun getRandomArmor() = getRandomTypedItem<ItemArmor>()
    fun getRandomArmorOrNull(minDefense: Int = 0) =
        items.filterIsInstance(ItemArmor::class.java).filter { it.defense >= minDefense }.randomOrNull()

    fun getRandomBetterArmor(than: ItemArmor) =
        items.filterIsInstance(ItemArmor::class.java).filter { it.defense > than.defense }.randomOrNull()
    fun getBestWeapon() = items.filterIsInstance<ItemWeapon>().maxByOrNull { it.power }
    fun getBestArmor() = items.filterIsInstance<ItemArmor>().maxByOrNull { it.defense }
}