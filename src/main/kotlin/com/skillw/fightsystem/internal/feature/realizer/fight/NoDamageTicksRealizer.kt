package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Awakeable
import com.skillw.attsystem.api.realizer.component.sub.Switchable
import com.skillw.attsystem.api.realizer.component.sub.Valuable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.platform.util.attacker

@AutoRegister
internal object NoDamageTicksRealizer : BaseRealizer("no-damage-ticks"), Awakeable, Switchable, Valuable {

    override val fileName: String = "options.yml"
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
        disableWorlds.addAll(config.get("disable-worlds", listOf("example-world")))
    }
}