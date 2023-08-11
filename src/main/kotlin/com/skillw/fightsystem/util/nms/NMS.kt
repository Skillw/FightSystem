package com.skillw.fightsystem.util.nms

import taboolib.module.nms.Packet
import taboolib.module.nms.nmsProxy

/**
 * @className NMS
 *
 * @author Glom
 * @date 2022/8/9 22:24 Copyright 2022 user. All rights reserved.
 */
abstract class NMS {

    companion object {

        val INSTANCE by lazy {
            nmsProxy<NMS>()
        }
    }

    abstract fun isDamageParticle(packet: Packet, max: Int)
}