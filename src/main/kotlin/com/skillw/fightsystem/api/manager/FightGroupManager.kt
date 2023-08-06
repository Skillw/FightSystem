package com.skillw.fightsystem.api.manager

import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.core.fight.FightGroup
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.KeyMap

/**
 * Fight group manager
 *
 * @constructor Create empty Fight group manager
 */
abstract class FightGroupManager : KeyMap<String, FightGroup>(), Manager {
    abstract fun runFight(key: String, data: FightData, message: Boolean = true, damage: Boolean = true): Double
}
