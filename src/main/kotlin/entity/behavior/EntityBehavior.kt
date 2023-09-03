package entity.behavior

import entity.EntityBase
import kotlin.random.Random

class EntityBehavior(private val preferences: List<EntityPreference>) {
    companion object {
        val default = EntityBehavior(
            listOf(
                EntityPreference.defaultPreferenceFindAnyWeaponIfNoneEquipped,
                EntityPreference.defaultPreferenceSearchDeadHostile,
                EntityPreference.defaultPreferenceAttackHostile,
                EntityPreference.defaultPreferenceFindBetterWeapon,
                EntityPreference.defaultPreferenceFindBetterArmor
            )
        )

        private val idleActions = listOf(
            EntityAction.GET_RANDOM_ITEM,
            EntityAction.SIT,
            EntityAction.KNEEL,
            EntityAction.CHATTER
        )

        fun randomIdleAction() = when(Random.nextInt(100)) {
            in 0..80 -> EntityAction.MOVE
            else -> idleActions.random()
        }
    }

    fun getNextAction(entity: EntityBase) =
        preferences.firstOrNull { preference ->
            preference.situations.all { situation -> entity.isInSituation(situation) }
        }?.action ?: randomIdleAction()
}