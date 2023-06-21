package com.skillw.fightsystem.api.manager

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.internal.feature.personal.PersonalData
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.KeyMap
import org.bukkit.entity.Player
import java.util.*

/**
 * Personal manager
 *
 * @constructor Create empty Personal manager
 */
abstract class PersonalManager : KeyMap<UUID, PersonalData>(), Manager {

    /** Enable */
    abstract val enable: Boolean

    /**
     * Push data
     *
     * @param player
     */
    abstract fun pushData(player: Player)

    /**
     * Pull data
     *
     * @param player
     * @return
     */
    abstract fun pullData(player: Player): PersonalData?

    /**
     * Has data
     *
     * @param player
     * @return
     */
    abstract fun hasData(player: Player): Boolean

    companion object {
        internal fun Player.pushData() {
            com.skillw.fightsystem.FightSystem.personalManager.pushData(this)
        }

        internal fun Player.pullData(): PersonalData? =
            com.skillw.fightsystem.FightSystem.personalManager.pullData(this)

        internal fun Player.hasData(): Boolean = com.skillw.fightsystem.FightSystem.personalManager.hasData(this)
    }
}
