package world

import world.template.RegionTemplate

object World {
    var regions = listOf<Region>()

    val void = Room(
        id = -1,
        coordinates = WorldCoordinates(-1, -1, -1),
        description = "A cold, dark region of nothingness.",
        connections = listOf(
            Connection(
                coordinatesString = "0, 0, 0",
                matchInputString = "go out"
            )
        )
    )

    val zero
        get() = regions[0].subregions[0].rooms[0]

    fun getRoomFromCoordinates(coordinates: WorldCoordinates): Room {
        return regions[coordinates.region].subregions[coordinates.subregion].rooms[coordinates.room]
    }

    fun getRandomRoom() = regions.random().subregions.random().rooms.random()

    fun load(c: Class<() -> Unit>) {
        val regionTemplates = Common.parseArrayFromJson<RegionTemplate>(c, "world.json")
        regions = regionTemplates.map { regionTemplate -> regionTemplate.toRegion() }
    }
}