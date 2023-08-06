package com.skillw.fightsystem.util

import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.util.sync

/**
 * @className Util
 *
 * @author Glom
 * @date 2023/8/6 16:18 Copyright 2023 user. All rights reserved.
 */


fun <T> syncRun(run: () -> T): T = if (!isPrimaryThread) sync { run() } else run()