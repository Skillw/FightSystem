package com.skillw.fightsystem.util.nms

import net.minecraft.server.v1_12_R1.EnumParticle
import net.minecraft.server.v1_16_R3.ParticleParam
import taboolib.common.util.unsafeLazy
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.Packet
import kotlin.math.min


/**
 * @className NMS
 *
 * @author Glom
 * @date 2022/8/9 22:24 Copyright 2022 user. All rights reserved.
 */
class NMSImpl : NMS() {


    /**
     * PacketPlayOutWorldParticles
     *
     * field EnumParticle / ParticleParam:
     *
     * < 1.13 -> a
     *
     * >= 1.13 -> j
     *
     * field count:
     *
     * < 1.13 -> i
     *
     * >= 1.13 -> h
     */
    private val isDamageParticleFunc: (Packet, Int) -> Unit by unsafeLazy {
        if (MinecraftVersion.major <= 4) a@{ packet, max ->
            val type = packet.read<EnumParticle>("a")
            if (type?.name != "DAMAGE_INDICATOR") return@a
            val count = packet.read<Int>("i") ?: 2
            packet.write("i", min(max, count))
        } else a@{ packet, max ->
            packet as net.minecraft.server.v1_16_R2.PacketPlayOutWorldParticles
            val type = packet.getProperty<ParticleParam>("j")
            if (type?.a() != "minecraft:damage_indicator") return@a
            val count = packet.read<Int>("h") ?: 2
            packet.write("h", min(max, count))
        }
    }

    override fun isDamageParticle(packet: Packet, max: Int) = isDamageParticleFunc(packet, max)
}