package com.skillw.fightsystem.internal.feature.compat.pvpmanager

import com.skillw.fightsystem.api.event.FightEvent
import me.NoChance.PvPManager.PvPManager
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent

object PreventDamage {

    @SubscribeEvent(bind = "me.NoChance.PvPManager.PvPManager")
    fun prevent(event: FightEvent.Pre) {
        val attacker = event.fightData.attacker as? Player? ?: return
        val defender = event.fightData.defender as? Player? ?: return
        event.isCancelled =
            !PvPManager.getInstance().playerHandler.get(attacker).hasPvPEnabled()
                    || !PvPManager.getInstance().playerHandler.get(defender).hasPvPEnabled()
    }
}