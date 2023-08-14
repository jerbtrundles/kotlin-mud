package entity.behavior

import entity.EntityBase

class EntityBehavior(private val preferences: List<EntityPreference>) {
    companion object {
        val default = EntityBehavior(
            listOf(
                EntityPreference.defaultPreferenceBeAlone,
                EntityPreference.defaultPreferenceFindBetterWeapon,
                EntityPreference.defaultPreferenceFindBetterArmor,
                EntityPreference.defaultPreferenceSit
            )
        )
    }

    fun getNextAction(entity: EntityBase) =
        preferences.firstOrNull { entity.isInSituation(it.situation) }?.action ?: EntityAction.NONE
}