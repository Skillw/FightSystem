package com.skillw.fightsystem.internal.feature.listener.fight

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.FightAPI.filters
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.feature.realizer.fight.ProjectileRealizer.cache
import com.skillw.fightsystem.internal.feature.realizer.fight.ProjectileRealizer.charged
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.fightsystem.internal.manager.FSConfig.attackFightKeyMap
import com.skillw.fightsystem.internal.manager.FSConfig.debug
import com.skillw.fightsystem.internal.manager.FSConfig.eveFightCal
import com.skillw.fightsystem.internal.manager.FSConfig.isFightEnable
import com.skillw.pouvoir.util.isAlive
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.attacker

internal object Attack {

    @SubscribeEvent(priority = EventPriority.LOW)
    fun attack(event: EntityDamageByEntityEvent) {
        if (!isFightEnable) return
        //如果攻击原因不是 ENTITY_ATTACK 和 PROJECTILE 则跳过计算
        val isAttack = event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
        val isProjectile = event.cause == EntityDamageEvent.DamageCause.PROJECTILE
        if (!isAttack && !isProjectile) return


        val attacker = event.attacker ?: return
        val defender = event.entity
        //判断是否都是存活实体                                防御方为盔甲架则跳过计算
        if (!attacker.isAlive() || !defender.isAlive() || defender is ArmorStand) return
        defender as LivingEntity

        //是否是EVE (非玩家 打 非玩家)                       如果关闭EVE计算则跳过计算
        if (attacker !is Player && defender !is Player && !eveFightCal) return

        //事件取消则跳过计算
        if (event.isCancelled) return

        //如果不是原版弓/弩攻击 则跳过计算
        if (isProjectile && event.damager.charged() == null) return

        if (filters.any {
            it(attacker, defender)
        }) return
        //处理原版护甲
        if (!FSConfig.isVanillaArmor) {
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
        }
        //原伤害
        val originDamage = event.finalDamage

        debug { FightSystem.debug("Handling Damage Event...") }

        //处理战斗组id
        val fightKey =
            when {

                attacker.isOp -> FSConfig.defaultFightGroup
                else -> attackFightKeyMap.filterKeys { attacker.hasPermission(it) }.values.firstOrNull()
                    ?: FSConfig.defaultFightGroup
            }

        debug { FightSystem.debug("FightKey: $fightKey") }
        val cacheData = event.damager.cache()
        val data = FightData(attacker, defender).also {
            if (FSConfig.projectileCache && cacheData != null)
                it.cache.setData(cacheData)
        }

        //运行战斗组并返回结果
        val result = com.skillw.fightsystem.api.FightAPI.runFight(fightKey, data.also {
            //往里塞参数
            it["origin"] = originDamage
            it["event"] = event
            it["projectile"] = isProjectile.toString()
            it["fightData"] = it
        }, damage = false)


        //结果小于等于零，代表MISS 未命中
        if (result <= 0.0) {
            debug { FightSystem.debug("Cancelled because Result <= 0") }
            event.isCancelled = true
            return
        }


        //设置伤害
        event.damage = result
    }

}