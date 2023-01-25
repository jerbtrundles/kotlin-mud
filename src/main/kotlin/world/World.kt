package world

import com.beust.klaxon.Klaxon
import world.template.RegionTemplate

object World {
    var regions = listOf<Region>()

    val zero
        get() = regions[0].subregions[0].rooms[0]
    fun getRoomFromCoordinates(coordinates: WorldCoordinates): Room {
        return regions[coordinates.region].subregions[coordinates.subregion].rooms[coordinates.room]
    }

    fun getRoomFromCoordinates(region: Int, subregion: Int, room: Int): Room {
        return regions[region].subregions[subregion].rooms[room]
    }

    fun getRandomRoom() = regions.random().subregions.random().rooms.random()

    fun load(c: Class<() -> Unit>) {
        try {
            val json = c.getResourceAsStream("world.json")?.bufferedReader()?.readText()!!
            val regionTemplates = Klaxon().parseArray<RegionTemplate>(json)!!
            regions = regionTemplates.map { regionTemplate -> regionTemplate.toRegion() }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}