package com.skillw.fightsystem.util.legacy

import taboolib.module.nms.MinecraftVersion


//给JS用的
object Version {


    @JvmStatic
    val major = MinecraftVersion.major


    @JvmStatic
    val supportedVersion = MinecraftVersion.supportedVersion

    @JvmStatic
    val minecraftVersion = MinecraftVersion.minecraftVersion

    @JvmStatic
    val runningVersion = MinecraftVersion.runningVersion

    @JvmStatic
    val majorLegacy = MinecraftVersion.majorLegacy

    @JvmStatic
    val minor = MinecraftVersion.minor

    @JvmStatic
    val mapping = MinecraftVersion.mapping

}