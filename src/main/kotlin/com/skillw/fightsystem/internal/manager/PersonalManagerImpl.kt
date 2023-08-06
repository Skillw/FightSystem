package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.api.manager.PersonalManager
import com.skillw.fightsystem.internal.feature.personal.PersonalData
import com.skillw.pouvoir.api.script.ScriptTool
import com.skillw.pouvoir.internal.feature.database.PouvoirContainer
import com.skillw.pouvoir.util.player
import org.bukkit.entity.Player
import java.util.*

object PersonalManagerImpl : PersonalManager() {
    override val key = "PersonalManager"
    override val priority: Int = 12
    override val subPouvoir = com.skillw.fightsystem.FightSystem
    override val enable: Boolean
        get() = FSConfig.isPersonalEnable

    override fun onDisable() {
        this.forEach {
            val player = it.key.player() ?: return
            pushData(player)
        }
    }

    override fun get(key: UUID): PersonalData {
        if (super.get(key) == null) {
            this[key] = PersonalData(key)
        }
        return if (!enable) {
            super.get(key)?.run {
                if (default) {
                    this
                } else {
                    default()
                    this
                }
            }!!
        } else super.get(key)!!
    }

    override fun pushData(player: Player) {
        val name = player.name
        if (enable)
            PouvoirContainer.container[name, "personal-data"] = this[player.uniqueId].toString()
    }

    override fun pullData(player: Player): PersonalData? {
        val name = player.name
        return if (enable)
            PersonalData.fromStr(PouvoirContainer.container[name, "personal-data"] ?: return null, player.uniqueId)
        else PersonalData(player.uniqueId)
    }

    override fun hasData(player: Player): Boolean {
        return (ScriptTool.get(player, "personal-data") ?: "null") != "null"
    }


}
