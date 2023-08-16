package com.skillw.fightsystem.internal.feature.compat.crackshot

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.FightSystem.realizerManager
import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.feature.realizer.fight.ProjectileRealizer.cache
import com.skillw.pouvoir.api.feature.realizer.BaseRealizer
import com.skillw.pouvoir.api.feature.realizer.BaseRealizerManager
import com.skillw.pouvoir.api.feature.realizer.component.Awakeable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.cbool
import taboolib.platform.util.hasMeta
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta
import java.io.File

/**
 * @className CompatCrackShot
 *
 * @author Glom
 * @date 2023/8/7 19:00 Copyright 2023 user. All rights reserved.
 */
@AutoRegister
internal object CompatCrackShot : BaseRealizer("compat-crack-shot"), Awakeable {

    override val file: File
        get() = FightSystem.options.file!!
    override val manager: BaseRealizerManager
        get() = realizerManager

    val cache: Boolean
        get() = config["projectile-cache"].cbool

    val fightGroups: HashMap<String, String> = HashMap()
    val default: String?
        get() = fightGroups["default"]

    init {
        ignorePaths.add("compat-crack-shot.fight-groups")
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        fightGroups.clear()
        (config["fight-groups"] as? Map<String, String>?)?.let { fightGroups.putAll(it) }
    }

    private const val META_KEY = "CrackShot-Damage"

    @Awake(LifeCycle.ENABLE)
    fun filter() {
        FightAPI.addIgnoreAttack { _, defender ->
            defender.hasMeta(META_KEY)
        }
    }

    @Ghost
    @SubscribeEvent
    fun cache(event: WeaponShootEvent) {
        if (!cache || event.projectile == null) return
        val cacheData = DataCache().attacker(event.player)
        event.projectile.cache(cacheData)
    }

    @Ghost
    @SubscribeEvent
    fun damage(event: WeaponDamageEntityEvent) {
        val victim = event.victim
        if (victim !is LivingEntity) return
        (fightGroups[event.weaponTitle] ?: default)
            ?.let { fight ->
                val cache = event.damager?.cache()
                val fightData = FightData(event.player, victim) {
                    cache?.let { cacheData ->
                        it.cache.setData(cacheData)
                    }
                    it["origin"] = event.damage
                    it["backstab"] = event.isBackstab
                    it["headshot"] = event.isHeadshot
                    it["critical"] = event.isCritical
                    it["weaponTitle"] = event.weaponTitle
                }
                event.victim.setMeta(META_KEY, true)
                val result = FightAPI.runFight(fight, fightData, message = true, damage = false)
                event.victim.removeMeta(META_KEY)
                event.damage = result
            }
    }
}