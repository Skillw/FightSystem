package com.skillw.fightsystem.api.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.attribute
import com.skillw.attsystem.api.AttrAPI.getAttrData
import com.skillw.attsystem.api.AttrAPI.hasData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.feature.compat.pouvoir.AttributePlaceHolder
import com.skillw.fightsystem.internal.manager.FSConfig
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.module.lang.sendWarn
import taboolib.module.nms.getI18nName

/**
 * @className DataCache
 *
 * @author Glom
 * @date 2023/1/22 18:00 Copyright 2023 user. All rights reserved.
 */
class DataCache(data: FightData? = null) {
    var data: FightData? = data
        set(value) {
            field = value
            field ?: return
            field!!["attacker-name"] = attackerName
            field!!["defender-name"] = defenderName
        }
    var attackerData: AttributeDataCompound = AttributeDataCompound()
    var defenderData: AttributeDataCompound = AttributeDataCompound()
    var attackerName: String = FSConfig.defaultAttackerName
    var defenderName: String = FSConfig.defaultDefenderName

    fun attacker(entity: LivingEntity?) {
        entity ?: return
        if (!entity.hasData())
            AttributeSystem.attributeSystemAPI.update(entity)
        attackerData = entity.getAttrData()!!.clone()
        attackerName = (entity as? Player)?.displayName ?: entity.getI18nName()
        data ?: return
        data!!["attacker"] = entity
        data!!["attacker-name"] = attackerName
    }

    fun defender(entity: LivingEntity?) {
        entity ?: return
        if (!entity.hasData())
            AttributeSystem.attributeSystemAPI.update(entity)
        defenderData = entity.getAttrData()!!.clone()
        defenderName = (entity as? Player)?.displayName ?: entity.getI18nName()
        data ?: return
        data!!["defender"] = entity
        data!!["defender-name"] = defenderName
    }

    fun attackerData(attKey: String, params: List<String>): String {
        val attribute = attribute(attKey)
        attribute ?: console().sendWarn("invalid-attribute", attKey)
        attribute ?: return "0.0"
        return AttributePlaceHolder.get(attackerData, attribute, params)
    }

    fun defenderData(attKey: String, params: List<String>): String {
        val attribute = attribute(attKey)
        attribute ?: console().sendWarn("invalid-attribute", attKey)
        attribute ?: return "0.0"
        return AttributePlaceHolder.get(defenderData, attribute, params)
    }
}