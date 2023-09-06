package engine.world

import engine.Inventory
import engine.item.template.ItemTemplate

class RoomShop(
    id: Int,
    coordinates: WorldCoordinates,
    description: String,
    connections: List<Connection>,
    inventory: Inventory = Inventory(),
    val soldItemTemplates: List<ItemTemplate>
): Room(id, coordinates, description, connections, inventory) {
    override fun toString() = "Shop: $coordinates"
}