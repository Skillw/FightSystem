package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.DamageType
import com.skillw.fightsystem.api.manager.DamageTypeManager
import com.skillw.pouvoir.util.loadMultiply
import java.io.File

object DamageTypeManagerImpl : DamageTypeManager() {
    override val key = "DamageTypeManager"
    override val priority: Int = 10
    override val subPouvoir = FightSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.clear()
        loadMultiply(
            File(FightSystem.plugin.dataFolder, "damage_type"),
            DamageType::class.java
        ).forEach {
            it.key.register()
        }
    }

}
