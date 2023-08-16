package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.util.nms.NMS
import com.skillw.pouvoir.api.feature.realizer.BaseRealizer
import com.skillw.pouvoir.api.feature.realizer.BaseRealizerManager
import com.skillw.pouvoir.api.feature.realizer.component.Switchable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.cint
import taboolib.module.nms.PacketSendEvent

@AutoRegister
internal object LimitDamageParticleRealizer : BaseRealizer("limit-damage-particle"), Switchable {

    override val file by lazy {
        FightSystem.options.file!!
    }

    override val manager: BaseRealizerManager
        get() = FightSystem.realizerManager

    private val max: Int
        get() = config["max"]?.cint ?: 10

    override val defaultEnable: Boolean = true

    @SubscribeEvent
    fun send(event: PacketSendEvent) {
        if (!isEnable()) return
        val packet = event.packet
        if (!packet.name.contains("PacketPlayOutWorldParticles")) return
        NMS.INSTANCE.isDamageParticle(packet, max)
    }

}