package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.Awakeable
import com.skillw.attsystem.util.StringUtils.material
import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.event.FightEvent
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getName
import taboolib.platform.util.sendLang
import java.util.*

/**
 * @className BaseAttributeRealizer
 *
 * @author Glom
 * @date 2023/1/6 7:05 Copyright 2022 user. All rights reserved.
 */
@AutoRegister
internal object DisableAttackMaterialRealizer : BaseRealizer("disable-attack-types"), Awakeable {

    override val file by lazy {
        FightSystem.options.file!!
    }
    val values: List<String>
        get() = config.getOrDefault("values", emptyList<String>()) as List<String>

    private val disableDamageTypes = ArrayList<Material>()
    override fun onEnable() {
        onReload()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun disableMaterialAttack(event: FightEvent.Pre) {
        val attacker = event.fightData.attacker ?: return
        if (attacker !is Player || event.fightData["projectile"] == "true" || event.key != "attack-damage") return
        val material = attacker.inventory.itemInMainHand.type.name.material() ?: return
        if (attacker.hasPermission("as.damage_type.${material.name.lowercase()}")) return
        if (disableDamageTypes.contains(material)) {
            event.isCancelled = true
            attacker.sendLang("disable-damage-type", attacker.inventory.itemInMainHand.getName())
            return
        }
    }

    override fun onReload() {
        disableDamageTypes.clear()
        for (material in values) {
            val xMaterial = XMaterial.matchXMaterial(material)
            if (xMaterial.isPresent) {
                disableDamageTypes.add(xMaterial.get().parseMaterial() ?: continue)
            } else {
                val materialMC = Material.matchMaterial(material)
                disableDamageTypes.add(materialMC ?: continue)
            }
        }
    }

    init {
        defaultConfig["values"] = listOf("BOW", "CROSSBOW")
    }


}