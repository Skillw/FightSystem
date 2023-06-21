package com.skillw.fightsystem.api.event

import com.skillw.fightsystem.api.fight.mechanic.Mechanic
import taboolib.platform.type.BukkitProxyEvent

/**
 * Mechanic register event
 *
 * @constructor Create empty Mechanic register event
 * @property mechanic 机制
 */
class MechanicRegisterEvent(val mechanic: Mechanic) : BukkitProxyEvent()
