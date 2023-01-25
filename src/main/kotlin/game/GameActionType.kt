package game

enum class GameActionType {
    NONE,
    LOOK,
    MOVE,
    GET_ITEM,
    DROP_ITEM,
    SHOW_EQUIPMENT,
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
        fun fromString(str: String): GameActionType {
            return when (str) {
                "look", "l" -> LOOK
                "go", "move" -> MOVE
                "get", "take" -> GET_ITEM
                "drop" -> DROP_ITEM
                "equipment" -> SHOW_EQUIPMENT
                "put" -> PUT_ITEM
                "close" -> CLOSE_CONTAINER
                "open" -> OPEN_CONTAINER
                "equip", "wear" -> EQUIP_ITEM
                "remove", "unequip" -> REMOVE_EQUIPMENT
                "inventory", "i" -> SHOW_INVENTORY
                "eat" -> EAT
                "drink", "quaff" -> DRINK
                "buy" -> BUY_ITEM
                "sell" -> SELL_ITEM
                "assess", "price" -> PRICE_ITEM
                "gold" -> SHOW_GOLD
                "sit" -> SIT
                "stand" -> STAND
                "kneel" -> KNEEL
                "attack", "kill" -> ATTACK
                "health" -> SHOW_HEALTH
                "search" -> SEARCH
                "quit", "exit", "q", "x" -> QUIT
                else -> NONE
            }
        }
    }
}