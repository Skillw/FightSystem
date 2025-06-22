package com.skillw.fightsystem.internal.feature.compat.mythicmobs.common

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.feature.realizer.fight.ProjectileRealizer.cache
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.fightsystem.internal.manager.FSConfig.isVanillaArmor
import com.skillw.pouvoir.util.isAlive
import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.attacker
import taboolib.platform.util.hasMeta

object AttackV {

    private fun isClassAvailable(className: String): Boolean {
        return runCatching {
            Class.forName(className)
        }.isSuccess
    }

    private val isMythMobV: Boolean by lazy {
        isClassAvailable("io.lumine.mythic.bukkit.MythicBukkit")
    }

    @Awake(LifeCycle.ENABLE)
    fun filter() {
        if (!isMythMobV) return
        FightAPI.addIgnoreAttack { zxy, _ ->
            if(!zxy.isMythicMob()) return@addIgnoreAttack false
            val mob = MythicBukkit.inst().apiHelper.getMythicMobInstance(zxy)
            return@addIgnoreAttack mob.type.config.getStringList("fightGroup").isNotEmpty()
        }
    }

    private fun Entity.isMythicMob(): Boolean {
        return MythicBukkit.inst().apiHelper.isMythicMob(this)
    }
    private fun Entity.getInstance(): io.lumine.mythic.core.mobs.ActiveMob? {
        return MythicBukkit.inst().apiHelper.getMythicMobInstance(this)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        if (event.entity.hasMeta("skill-damage")) return
        if (!isMythMobV) return
        if (!FSConfig.isFightEnable) return
        if (!event.attacker?.isMythicMob()!!) return
        if (!event.attacker!!.isAlive() || !event.entity.isAlive() || event.entity is ArmorStand) return

        val mob = MythicBukkit.inst().apiHelper.getMythicMobInstance(event.attacker)
        val groups = run {
            if(mob.type.config.getStringList("fightGroup").isEmpty()) return
            return@run mob.type.config.getStringList("fightGroup")
        }
        val entity = event.entity as LivingEntity
        if (!FSConfig.isVanillaArmor) {
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
            event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE,0.0)
        }
        val isProjectile = event.cause == EntityDamageEvent.DamageCause.PROJECTILE
        val origin = event.finalDamage
        val cacheData = event.damager.cache()
        mob.type.config.getStringList("fightGroup")
        val data = FightData(event.attacker, entity).also {
            if (FSConfig.projectileCache && cacheData != null)
                it.cache.setData(cacheData)
        }

        val result = FightAPI.runFight(groups, data.also {
            it["origin"] = origin
            it["projectile"] = isProjectile.toString()
            it["event"] = event
            it["ActiveMob"] = mob
            it["MobType"] = mob.type!!
            it["fightData"] = data
        }, damage = false)

        if (result <= 0.0){
            FSConfig.debug { FightSystem.debug("Cancelled because Result <= 0") }
            event.isCancelled = true
            return
        }

        if (isVanillaArmor)
            event.damage = result
        else
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, result)
    }
}