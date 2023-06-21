package com.skillw.fightsystem.internal.feature.personal

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import java.util.*

class PersonalData(override val key: UUID) : Registrable<UUID> {
    var attacking = FSConfig.defaultAttackMessageType
    var defensive = FSConfig.defaultDefendMessageType
    var regainHolo = FSConfig.defaultRegainHolo

    val default: Boolean
        get() = attacking == FSConfig.defaultAttackMessageType &&
                defensive == FSConfig.defaultDefendMessageType &&
                regainHolo == FSConfig.defaultRegainHolo


    fun default() {
        attacking = FSConfig.defaultAttackMessageType
        defensive = FSConfig.defaultDefendMessageType
        regainHolo = FSConfig.defaultRegainHolo
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String, uuid: UUID): PersonalData? {
            val array = json.split(";")
            if (array.isEmpty() || array.size < 3) return null
            val personalData = PersonalData(uuid)
            personalData.attacking = array[0]
            personalData.defensive = array[1]
            personalData.regainHolo = array[2].toBoolean()
            return personalData
        }
    }

    override fun toString(): String {
        return "${attacking};${defensive};$regainHolo"
    }

    override fun register() {
        com.skillw.fightsystem.FightSystem.personalManager.register(this)
    }
}
