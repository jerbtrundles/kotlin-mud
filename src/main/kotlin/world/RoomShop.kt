package world

import Inventory
import entity.EntityBase
import item.template.ItemTemplate

class RoomShop(
    id: Int,
    coordinates: WorldCoordinates,
    description: String,
    connections: List<Connection>,
    inventory: Inventory = Inventory(),
    val soldItemTemplates: MutableList<ItemTemplate>,
    entities: MutableList<EntityBase> = mutableListOf(),
): Room(id, coordinates, description, connections, inventory, entities)