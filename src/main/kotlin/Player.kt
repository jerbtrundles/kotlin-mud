import entity.EntityAttributes
import entity.EntityPosture
import item.ItemBase
import item.ItemContainer

object Player {
  var name = ""
  val attributes = EntityAttributes()
  var posture = EntityPosture.STANDING
  val inventory: Inventory = Inventory()
  var gold = 0
  val goldString
    get() = "You have $gold gold."
  val healthString
    get() = "${attributes.healthString}\n${attributes.magicString}"
  val inventoryString
    get() = if (inventory.items.isEmpty()) {
      "You aren't carrying anything."
    } else {
      "You are carrying $inventory."
    }
}