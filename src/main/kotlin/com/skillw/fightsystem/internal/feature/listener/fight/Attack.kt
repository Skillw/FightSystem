package com.skillw.fightsystem.internal.feature.listener.fight

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.feature.realizer.fight.projectileCharged
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.fightsystem.internal.manager.FSConfig.arrowCache
import com.skillw.fightsystem.internal.manager.FSConfig.attackFightKeyMap
import com.skillw.fightsystem.internal.manager.FSConfig.debug
import com.skillw.fightsystem.internal.manager.FSConfig.eveFightCal
import com.skillw.fightsystem.internal.manager.FSConfig.isFightEnable
import com.skillw.pouvoir.util.isAlive
import com.sucy.skill.api.skills.Skill
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.attacker
import taboolib.platform.util.getMeta

internal object Attack {
    private val isSkillAPIDamage
        get() = FSConfig.skillAPI && Skill.isSkillDamage()


    private fun Entity.cacheData(): FightData? =
        if (hasMetadata("ATTRIBUTE_SYSTEM_DATA")) getMetadata("ATTRIBUTE_SYSTEM_DATA")[0].value() as? FightData else null

    fun LivingEntity.runAttack(defender: LivingEntity) {

    }

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
        if (attacker.getMeta("doing-skill-damage").firstOrNull()?.asBoolean() == true) {
            return
        }

        //事件取消则跳过计算
        if (event.isCancelled) return

        //是 SkillAPI 的伤害则跳过计算
        if (isSkillAPIDamage) return

        //如果不是原版弓/弩攻击 则跳过计算
        if (isProjectile && event.damager.projectileCharged() == null) return

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
                //是op的话就直接"attack-damage"，不参与权限计算
                attacker.isOp -> "attack-damage"
                else -> attackFightKeyMap.filterKeys { attacker.hasPermission(it) }.values.firstOrNull()
                    ?: "attack-damage"
            }

        debug { FightSystem.debug("FightKey: $fightKey") }
        val cacheData = event.damager.cacheData()
        val data = if (arrowCache && cacheData != null) cacheData.also { it.defender = defender } else FightData(
            attacker,
            defender
        )

        //运行战斗组并返回结果
        val result = com.skillw.fightsystem.api.FightAPI.runFight(fightKey, data.also {
            //往里塞参数
            it["origin"] = originDamage
            it["event"] = event
            it["projectile"] = isProjectile.toString()
        })


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