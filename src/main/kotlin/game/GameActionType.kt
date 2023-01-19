package game

enum class GameActionType {
    NONE,
    LOOK,
    MOVE,
    GET_ITEM,
    DROP_ITEM,
    SHOW_HANDS,
    PUT_ITEM,
    OPEN_CONTAINER,
    CLOSE_CONTAINER,
    EQUIP_ITEM,
    REMOVE_EQUIPMENT,
    SHOW_INVENTORY,
    EAT,
    DRINK,
    BUY_ITEM,
    SELL_ITEM,
    PRICE_ITEM,
    SHOW_GOLD,
    SIT,
    STAND,
    KNEEL,
    ATTACK,
    SHOW_HEALTH,
    SEARCH,
    QUIT;

    companion object {
        fun fromCommandVerbString(str: String): GameActionType {
            return when (str) {
                "look" -> LOOK
                "go", "move" -> MOVE
                "get", "take" -> GET_ITEM
                "drop" -> DROP_ITEM
                "hands" -> SHOW_HANDS
                "put" -> PUT_ITEM
                "close" -> CLOSE_CONTAINER
                "open" -> OPEN_CONTAINER
                "equip", "wear" -> EQUIP_ITEM
                "remove" -> REMOVE_EQUIPMENT
                "inventory" -> SHOW_INVENTORY
                "eat" -> EAT
                "drink", "quaff" -> DRINK
                "buy" -> BUY_ITEM
                "sell" -> SELL_ITEM
                "price" -> PRICE_ITEM
                "gold" -> SHOW_GOLD
                "sit" -> SIT
                "stand" -> STAND
                "kneel" -> KNEEL
                "attack", "kill" -> ATTACK
                "health" -> SHOW_HEALTH
                "search" -> SEARCH
                "quit", "exit" -> QUIT
                else -> NONE
            }
        }
    }
}