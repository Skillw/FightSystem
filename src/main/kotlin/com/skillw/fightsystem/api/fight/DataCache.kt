package com.skillw.fightsystem.api.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.attribute
import com.skillw.attsystem.api.AttrAPI.getAttrData
import com.skillw.attsystem.api.AttrAPI.hasData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.feature.compat.pouvoir.AttributePlaceHolder
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.util.getDisplayName
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.console
import taboolib.module.lang.sendWarn

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
    var attackerData: AttributeDataCompound? = null
    var defenderData: AttributeDataCompound? = null
    var attackerName: String = FSConfig.defaultAttackerName
    var defenderName: String = FSConfig.defaultDefenderName

    fun setData(other: DataCache) {
        other.attackerData?.let { attackerData = it }
        other.defenderData?.let { defenderData = it }
    }

    fun attacker(entity: LivingEntity?): DataCache {
        entity ?: return this
        if (!entity.hasData())
            AttributeSystem.attributeSystemAPI.update(entity)
        attackerData = entity.getAttrData()!!.clone()
        attackerName = entity.getDisplayName()
        data ?: return this
        data!!["attacker"] = entity
        data!!["attacker-name"] = attackerName
        return this
    }

    fun defender(entity: LivingEntity?): DataCache {
        entity ?: return this
        if (!entity.hasData())
            AttributeSystem.attributeSystemAPI.update(entity)
        defenderData = entity.getAttrData()!!.clone()
        defenderName = entity.getDisplayName()
        data ?: return this
        data!!["defender"] = entity
        data!!["defender-name"] = defenderName
        return this
    }

    fun attackerData(attKey: String, params: List<String>): String {
        val attribute = attribute(attKey)
        attribute ?: console().sendWarn("invalid-attribute", attKey)
        attribute ?: return "0.0"
        return attackerData?.let { AttributePlaceHolder.get(it, attribute, params) } ?: "0.0"
    }

    fun defenderData(attKey: String, params: List<String>): String {
        val attribute = attribute(attKey)
        attribute ?: console().sendWarn("invalid-attribute", attKey)
        attribute ?: return "0.0"
        return defenderData?.let { AttributePlaceHolder.get(it, attribute, params) } ?: "0.0"
    }
}