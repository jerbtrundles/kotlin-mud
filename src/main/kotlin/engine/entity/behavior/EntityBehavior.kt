package engine.entity.behavior

import engine.entity.EntityBase
import kotlin.random.Random

class EntityBehavior(private val preferences: List<EntityPreference>) {
    companion object {
        val defaultNpc = EntityBehavior(
            listOf(
                EntityPreference.defaultPreferenceFindAnyWeaponIfNoneEquipped,
                EntityPreference.defaultPreferenceFoundValuableItem,
                EntityPreference.defaultPreferenceSearchDeadHostile,
                EntityPreference.defaultPreferenceAttackHostile,
                EntityPreference.defaultPreferenceFindBetterWeapon,
                EntityPreference.defaultPreferenceFindBetterArmor
            )
        )

        val defaultMonster = EntityBehavior(
            listOf(
                EntityPreference.defaultPreferenceFindAnyWeaponIfNoneEquipped,
                // EntityPreference.defaultPreferenceAttackPlayer,
                EntityPreference.defaultPreferenceSearchDeadHostile,
                EntityPreference.defaultPreferenceAttackHostile,
                EntityPreference.defaultPreferenceFindBetterWeapon,
                EntityPreference.defaultPreferenceFindBetterArmor
            )
        )

        val janitor = EntityBehavior(
            listOf(
                EntityPreference.defaultPreferenceFindBetterWeapon,
                EntityPreference.defaultPreferenceFindBetterArmor,
                EntityPreference.janitorPreferenceGetItem,
                EntityPreference.defaultPreferenceSearchDeadHostile
            )
        )

        val berserker = EntityBehavior(
            listOf(
                EntityPreference.defaultPreferenceFindBetterWeapon,
                EntityPreference.defaultPreferenceAttackHostile,
                EntityPreference.defaultPreferenceAlwaysMoving,
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