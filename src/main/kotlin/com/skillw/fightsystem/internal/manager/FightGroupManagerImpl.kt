package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.event.FightEvent
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.message.MessageData
import com.skillw.fightsystem.api.manager.FightGroupManager
import com.skillw.fightsystem.internal.core.fight.FightGroup
import com.skillw.fightsystem.util.asyncTaskRun
import com.skillw.fightsystem.util.syncRun
import com.skillw.pouvoir.util.loadMultiply
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common5.mirrorNow
import taboolib.platform.util.getMeta
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta
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

    @Awake(LifeCycle.ENABLE)
    private fun addIgnore() {
        FightAPI.addIgnoreAttack { _, defender ->
            defender.getMeta("doing-skill-damage").firstOrNull()?.asBoolean() == true
        }

    }

    private fun FightData.doingDamage(result: Double): Double {
        defender?.setMeta("skill-damage", true)
        defender?.damage(result, attacker)
        defender?.removeMeta("skill-damage")
        return result
    }

    override fun runFight(key: String, data: FightData, message: Boolean, damage: Boolean): Double {

        if (!FightSystem.fightGroupManager.containsKey(key)) return -1.0

        val fightData = data.apply {
            addNamespaces(*namespaceNames())
            putIfAbsent("projectile", false)
            putIfAbsent("origin", 0.0)
            putIfAbsent("force", 1.0)
            putIfAbsent("charge", 1.0)
            this["type"] = when {
                attacker is Player && defender is Player -> "PVP"
                attacker is Player && defender !is Player -> "PVE"
                else -> "EVE"
            }
            calMessage = message
        }

        val messageData = MessageData()

        val before = FightEvent.Pre(key, fightData)
        before.call()
        if (before.isCancelled) return -0.1

        var eventFightData = before.fightData
        val result = mirrorNow("fight-$key-cal") {
            FightSystem.fightGroupManager[key]!!.run(eventFightData)
        }
        val process = FightEvent.Process(key, eventFightData)
        process.call()

        eventFightData = process.fightData
        val post = FightEvent.Post(key, eventFightData)
        post.call()

        eventFightData = post.fightData
        if (post.isCancelled) return -0.1

        if (message) {
            eventFightData.calMessage()
            messageData.addAll(eventFightData.messageData)
            asyncTaskRun {
                messageData.send(fightData.attacker as? Player?, fightData.defender as? Player?)
            }
        }

        val damageValue = result.apply(eventFightData)
        return if (damage)
            syncRun { data.doingDamage(damageValue) }
        else damageValue
    }
}
