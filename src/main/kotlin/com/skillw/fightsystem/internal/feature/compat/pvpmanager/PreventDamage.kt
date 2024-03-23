package com.skillw.fightsystem.internal.feature.compat.pvpmanager

import com.skillw.fightsystem.api.event.FightEvent
import me.NoChance.PvPManager.PvPManager
import org.apache.logging.log4j.core.util.Loader
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent

object PreventDamage {
    private val plugin by lazy {
        !Loader.isClassAvailable("me.NoChance.PvPManager.PvPManager")
    }

    @SubscribeEvent(bind = "me.NoChance.PvPManager.PvPManager")
    fun prevent(event: FightEvent.Pre) {
        if (plugin) return
        val attacker = event.fightData.attacker as? Player? ?: return
        val defender = event.fightData.defender as? Player? ?: return
        event.isCancelled =
            !PvPManager.getInstance().playerHandler.get(attacker).hasPvPEnabled()
                    || !PvPManager.getInstance().playerHandler.get(defender).hasPvPEnabled()
    }
}