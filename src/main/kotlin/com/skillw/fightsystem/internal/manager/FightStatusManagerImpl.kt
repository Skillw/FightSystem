package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.event.EntityFightStatusEvent
import com.skillw.fightsystem.api.manager.FightStatusManager
import com.skillw.fightsystem.internal.feature.realizer.fight.FightStatusRealizer
import com.skillw.pouvoir.api.plugin.map.BaseMap
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.service.PlatformExecutor
import taboolib.platform.util.sendLang
import java.util.*

object FightStatusManagerImpl : FightStatusManager() {

    override val key = "FightStatusManager"
    override val priority: Int = 13
    override val subPouvoir = FightSystem

    private val fights: MutableCollection<UUID> = Collections.synchronizedCollection(HashSet<UUID>())
    private val tasks = BaseMap<UUID, PlatformExecutor.PlatformTask>()

    override fun isFighting(uuid: UUID): Boolean {
        return fights.contains(uuid)
    }

    override fun isFighting(entity: LivingEntity): Boolean {
        return isFighting(entity.uniqueId)
    }

    override fun intoFighting(uuid: UUID) {
        uuid.livingEntity()?.let { intoFighting(it) }
    }


    override fun outFighting(uuid: UUID) {
        uuid.livingEntity()?.let { outFighting(it) }
    }

    override fun intoFighting(entity: LivingEntity) {
        val uuid = entity.uniqueId
        val event = EntityFightStatusEvent.In(entity as? Player? ?: return)
        event.call()
        if (event.isCancelled) return
        if (!fights.contains(uuid)) {
            (entity as? Player?)?.sendLang("fight-in")
        }
        tasks[uuid]?.cancel()
        tasks.remove(uuid)
        fights.add(uuid)
        tasks[uuid] = submitAsync(delay = FightStatusRealizer.value(entity).toLong()) { outFighting(uuid) }
    }

    override fun outFighting(entity: LivingEntity) {
        val uuid = entity.uniqueId
        val event = EntityFightStatusEvent.Out(entity)
        event.call()
        if (event.isCancelled) return
        fights.remove(uuid)
        tasks[uuid]?.cancel()
        tasks.remove(uuid)
        (entity as? Player?)?.sendLang("fight-out")
    }

}
