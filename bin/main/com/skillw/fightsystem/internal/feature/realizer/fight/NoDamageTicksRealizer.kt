package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.pouvoir.api.feature.realizer.BaseRealizer
import com.skillw.pouvoir.api.feature.realizer.component.Awakeable
import com.skillw.pouvoir.api.feature.realizer.component.Switchable
import com.skillw.pouvoir.api.feature.realizer.component.Valuable
import com.skillw.fightsystem.FightSystem
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.platform.util.attacker

@AutoRegister
internal object NoDamageTicksRealizer : BaseRealizer("no-damage-ticks"), Awakeable, Switchable, Valuable {

    override val file by lazy {
        FightSystem.options.file!!
    }
    override val defaultEnable: Boolean
        get() = true
    override val defaultValue: String
        get() = "5"

    private val disableWorlds = HashSet<String>()

    private fun LivingEntity.noDamageTicks() {
        if (isEnable() && world.name !in disableWorlds) {
            val ticks = value(this).toInt()
            submit {
                noDamageTicks = ticks
            }
        }
    }

    @SubscribeEvent
    fun damage(event: EntityDamageByEntityEvent) {
        event.attacker?.noDamageTicks()
        (event.entity as? LivingEntity)?.noDamageTicks()
    }


    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        disableWorlds.clear()
        disableWorlds.addAll(config.getOrDefault("disable-worlds", listOf("example-world")) as List<String>)
    }
}