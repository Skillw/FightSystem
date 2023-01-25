package com.skillw.fightsystem.internal.feature.compat.magic

import com.elmakers.mine.bukkit.action.ActionFactory
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object MagicRegister {
    @Awake(LifeCycle.ENABLE)
    fun reg() {
        if (Bukkit.getPluginManager().isPluginEnabled("Magic")) {
            ActionFactory.registerActionClass("att_damage", AttDamage::class.java)
            ActionFactory.registerActionClass("run_fight", AttDamage::class.java)
        }
    }
}