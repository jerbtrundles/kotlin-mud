package entity

import item.ItemBase

class EntityInventory(
    var gold: Int = 0,
    val items: MutableList<ItemBase> = mutableListOf()
) {

}