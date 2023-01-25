package world.template

import com.beust.klaxon.Json
import world.Connection
import world.Room
import world.RoomShop
import world.WorldCoordinates
import world.template.ShopTemplates.templates

class RoomTemplate(
    @Json(name = "room-id")
    val id: Int,
    @Json(name = "room-coordinates")
    val coordinatesString: String,
    @Json(name = "room-description")
    val description: String,
    @Json(name = "room-connections")
    val connections: List<Connection>
) {
    @Json(ignored = true)
    val coordinates = WorldCoordinates.parseFromString(coordinatesString)

    fun toRoom(): Room {
        val template = templates.firstOrNull { shopTemplate ->
            shopTemplate.coordinates == coordinates
        }

        return if(template != null) {
            RoomShop(
                id = id,
                coordinates = coordinates,
                description = description,
                connections = connections,
                soldItemTemplates = template.soldItemTemplates.toMutableList()
            )
        } else {
            Room(
                id = id,
                coordinates = coordinates,
                description = description,
                connections = connections
            )
        }
    }
}