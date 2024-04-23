package com.skillw.fightsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.feature.realizer.fight.ProjectileRealizer.cache
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.util.isAlive
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.mobs.ActiveMob
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

object AttackIV {


    private fun isClassAvailable(className: String): Boolean {
        return runCatching {
            Class.forName(className)
        }.isSuccess
    }

    private val isMythMobIV: Boolean by lazy {
        isClassAvailable("io.lumine.xikage.mythicmobs.MythicMobs")
    }

    @Awake(LifeCycle.ENABLE)
    fun filter() {
        if (!isMythMobIV) return
        FightAPI.addIgnoreAttack { zxy, _ ->
            if(!zxy.isMythicMob()) return@addIgnoreAttack false
            return@addIgnoreAttack MythicMobs.
            inst().
            apiHelper.
            getMythicMobInstance(zxy).
            type.
            config.
            getStringList("fightGroup").isNotEmpty()
        }
    }

    private fun Entity.isMythicMob(): Boolean {
        return MythicMobs.inst().apiHelper.isMythicMob(this)
    }
    private fun Entity.getInstance(): ActiveMob {
        return MythicMobs.inst().apiHelper.getMythicMobInstance(this)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        if (!isMythMobIV) return
        if (!FSConfig.isFightEnable) return
        if (!event.attacker?.isMythicMob()!!) return
        val mob = event.attacker!!.getInstance()
        if (!event.attacker!!.isAlive() || !event.entity.isAlive() || event.entity is ArmorStand) return

        if (!FSConfig.isVanillaArmor) {
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
        }
        val entity = event.entity as LivingEntity
        val ids = mob.type.config.getStringList("fightGroup")
        if (ids.isEmpty()) return
        val isProjectile = event.cause == EntityDamageEvent.DamageCause.PROJECTILE
        val origin = event.finalDamage
        val cacheData = event.damager.cache()

        val data = FightData(event.attacker, entity).also {
            if (FSConfig.projectileCache && cacheData != null)
                it.cache.setData(cacheData)
        }

        val result = run<Double> {
            var result = 0.0
            ids.forEach { fg ->
                val calc = FightAPI.runFight(fg, data.also {
                    //往里塞参数
                    it["origin"] = origin
                    it["projectile"] = isProjectile.toString()
                    it["event"] = event
                    it["ActiveMob"] = mob
                    it["MobType"] = mob.type!!
                    it["fightData"] = data
                }, damage = false)
                result += calc
            }
            return@run result
        }

        if (result <= 0.0){
            FSConfig.debug { FightSystem.debug("Cancelled because Result <= 0") }
            event.isCancelled = true
            return
        }

        event.damage = result
    }
}