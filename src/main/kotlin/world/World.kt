package world

import com.beust.klaxon.Klaxon

object World {
    fun getRoomFromCoordinates(coordinates: WorldCoordinates): Room {
        return regions[coordinates.region].subregions[coordinates.subregion].rooms[coordinates.room]
    }

    fun getRandomRoom() = regions.random().subregions.random().rooms.random()

    fun load(c: Class<() -> Unit>) {
        val json = c.getResourceAsStream("world.json")?.bufferedReader()?.readText()!!
        regions = Klaxon().parseArray(json)!!
    }

    var regions = listOf<Region>()
}