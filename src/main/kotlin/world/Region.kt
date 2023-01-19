package world

import com.beust.klaxon.Json

class Region(
    @Json(name = "region-id")
    val id: Int,
    @Json(name = "region-name")
    val name: String,
    @Json(name = "region-subregions")
    val subregions: List<Subregion>
) {
    override fun toString(): String {
        return name
    }

    fun toDebugString(): String {
        val sb = StringBuilder()
        sb.appendLine("world.Region ID: $id")
        sb.appendLine("world.Region name: $name")
        subregions.forEach { subregion ->
            sb.appendLine(subregion)
        }
        return sb.toString()
    }
}