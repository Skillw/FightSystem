package com.skillw.fightsystem.internal.feature.personal

import com.skillw.fightsystem.api.manager.PersonalManager.Companion.pullData
import com.skillw.fightsystem.api.manager.PersonalManager.Companion.pushData
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

private object Personal {
    @SubscribeEvent
    fun join(event: PlayerJoinEvent) {
        (event.player.pullData() ?: PersonalData(event.player.uniqueId)).register()
    }

    @SubscribeEvent
    fun quit(event: PlayerQuitEvent) {
        event.player.pushData()
    }
}