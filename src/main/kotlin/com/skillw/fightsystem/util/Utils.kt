package com.skillw.fightsystem.util

import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.sync

/**
 * @className Util
 *
 * @author Glom
 * @date 2023/8/6 16:18 Copyright 2023 user. All rights reserved.
 */


fun <T> syncRun(run: () -> T): T = if (!isPrimaryThread) sync { run() } else run()
fun syncTaskRun(run: () -> Unit) {
    if (!isPrimaryThread) submit { run() } else run()
}

fun asyncTaskRun(run: () -> Unit) {
    if (isPrimaryThread) submitAsync { run() } else run()
}