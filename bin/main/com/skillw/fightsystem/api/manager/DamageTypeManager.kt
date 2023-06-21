package com.skillw.fightsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.KeyMap

/**
 * Damage type manager
 *
 * @constructor Create empty Damage type manager
 */
abstract class DamageTypeManager : KeyMap<String, com.skillw.fightsystem.api.fight.DamageType>(), Manager
