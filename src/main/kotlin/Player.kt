import entity.EntityAttributes
import entity.EntityPosture
import item.ItemArmor
import item.ItemWeapon
import world.World
import world.WorldCoordinates

object Player {
    var name = ""
    val attributes = EntityAttributes()
    var coordinates = WorldCoordinates(0, 0, 0)
    var level = 1
    var experience = 0
    val currentRoom
        get() = World.getRoomFromCoordinates(coordinates)
    var posture = EntityPosture.STANDING
    val inventory: Inventory = Inventory()
    var weapon: ItemWeapon? = null
    var armor: ItemArmor? = null

    var gold = 0
    val goldString
        get() = "You have $gold gold."
    var bankAccountBalance = 100
    val bankAccountBalanceString
        get() = "Your balance is $bankAccountBalance gold."


    val healthString
        get() = "${attributes.healthString}\n${attributes.magicString}"
    val inventoryString: String
        get() = if (inventory.items.isEmpty()) {
            "You aren't carrying anything."
        } else {
            "You are carrying ${inventory.collectionString}."
        }
}