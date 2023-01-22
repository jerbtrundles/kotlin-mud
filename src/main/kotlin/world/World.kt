package world

import com.beust.klaxon.Klaxon

object World {
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
        val json = c.getResourceAsStream("world.json")?.bufferedReader()?.readText()!!
        regions = Klaxon().parseArray(json)!!
    }

    var regions = listOf<Region>()
}