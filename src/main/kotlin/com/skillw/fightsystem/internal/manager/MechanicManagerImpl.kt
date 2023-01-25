package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.event.MechanicRegisterEvent
import com.skillw.fightsystem.api.fight.mechanic.Mechanic
import com.skillw.fightsystem.api.manager.MechanicManager

object MechanicManagerImpl : MechanicManager() {
    override val key = "MechanicManager"
    override val priority: Int = 11
    override val subPouvoir = com.skillw.fightsystem.FightSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }

    override fun register(key: String, value: Mechanic) {
        val event = com.skillw.fightsystem.api.event.MechanicRegisterEvent(value)
        event.call()
        if (event.isCancelled) return
        put(key, value)
    }
}
