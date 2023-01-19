package world

import com.beust.klaxon.Json
import java.lang.StringBuilder

class Subregion(
    @Json(name = "subregion-id")
    val id: Int,
    @Json(name = "subregion-name")
    val name: String,
    @Json(name = "subregion-rooms")
    val rooms: List<Room>
) {
    override fun toString(): String {
        return name
    }

    fun toDebugString(): String {
        val sb = StringBuilder()
        sb.appendLine("world.Subregion ID: $id")
        sb.appendLine("world.Subregion name: $name")
        rooms.forEach { room ->
            sb.append(room)
        }
        return sb.toString()
    }
}