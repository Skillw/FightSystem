package com.skillw.fightsystem.api.event

import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class EntityFightStatusEvent {

    /**
     * 实体进入战斗
     *
     * @property entity 实体
     */
    class In(val entity: LivingEntity) : BukkitProxyEvent()

    /**
     * 实体退出战斗
     *
     * @property entity 实体
     */
    class Out(val entity: LivingEntity) : BukkitProxyEvent()
}