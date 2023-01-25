package com.skillw.fightsystem.api.manager

import com.skillw.fightsystem.api.fight.mechanic.Mechanic
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.KeyMap

/**
 * Mechanic manager
 *
 * @constructor Create empty Mechanic manager
 */
abstract class MechanicManager : KeyMap<String, Mechanic>(), Manager
