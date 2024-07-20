package com.skillw.fightsystem.api.event

import com.skillw.fightsystem.api.fight.mechanic.Mechanic
import taboolib.platform.type.BukkitProxyEvent
import java.util.Optional

/**
 * Mechanic register event
 *
 * @constructor Create empty Mechanic register event
 * @property mechanic 机制
 */
class MechanicLoadEvent(val key:String,var mechanic: Optional<Mechanic> = Optional.empty()) : BukkitProxyEvent()
