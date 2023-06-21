package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.message.MessageData
import com.skillw.fightsystem.api.manager.FightGroupManager
import com.skillw.fightsystem.internal.core.fight.FightGroup
import com.skillw.pouvoir.util.loadMultiply
import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync
import taboolib.common5.mirrorNow
import java.io.File

object FightGroupManagerImpl : FightGroupManager() {

    override val key = "FightGroupManager"
    override val priority: Int = 13
    override val subPouvoir = FightSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        clear()
        loadMultiply(
            File(FightSystem.plugin.dataFolder, "fight_group"), FightGroup::class.java
        ).forEach {
            it.key.register()
        }
    }

    override fun runFight(key: String, data: FightData, message: Boolean): Double {

        if (!FightSystem.fightGroupManager.containsKey(key)) return -1.0

        val fightData = data.apply {
            addNamespaces(*namespaceNames())
            this.putIfAbsent("projectile", false)
            this["type"] = when {
                attacker is Player && defender is Player -> "PVP"
                attacker is Player && defender !is Player -> "PVE"
                attacker !is Player && defender !is Player -> "EVE"
                else -> "EVE"
            }
            calMessage = message
        }

        val messageData = MessageData()
        return mirrorNow("fight-$key-cal") {

            val pre = com.skillw.fightsystem.api.event.FightEvent.Pre(key, fightData)
            pre.call()
            if (pre.isCancelled) return@mirrorNow -0.1

            var eventFightData = pre.fightData
            val result = FightSystem.fightGroupManager[key]!!.run(eventFightData)

            val process = com.skillw.fightsystem.api.event.FightEvent.Process(key, eventFightData)
            process.call()

            eventFightData = process.fightData
            val post = com.skillw.fightsystem.api.event.FightEvent.Post(key, eventFightData)
            post.call()

            eventFightData = post.fightData
            if (post.isCancelled) return@mirrorNow -0.1

            if (message) {
                eventFightData.calMessage()
                messageData.addAll(eventFightData.messageData)
                submitAsync {
                    messageData.send(fightData.attacker as? Player?, fightData.defender as? Player?)
                }
            }

            result.apply(eventFightData)
        }
    }
}
