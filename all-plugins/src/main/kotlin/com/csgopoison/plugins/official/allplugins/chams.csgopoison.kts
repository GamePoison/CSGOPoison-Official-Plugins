package com.csgopoison.plugins.official.allplugins

private val teamColor = Color(0, 0, 255, 1.0)
private val enemyColor = Color(255, 0, 0, 1.0)

private val chamsUpdateRateMS = 1024L

every(chamsUpdateRateMS, { context ->
    //TODO dwModelAmbientMin...
    forEntities(EntityType.ccsPlayer) { entityData ->
        val color = if (entityManager.me.team == entityData.entity.team) teamColor else enemyColor
        chamsEntity(entityData.entityAddress, color)
    }

    return@every
}) {
    if (entityManager.meAddress <= 0) return@every false
    if (entityManager.me.dead) return@every false


    return@every true
}

fun chamsEntity(address: Long, color: Color): Boolean {
    val data = Pointer.allocate(128)
    try {
        if (!process.source(address, 128, data)) return false
        data[0x70] = color.red.toByte()
        data[0x71] = color.green.toByte()
        data[0x72] = color.blue.toByte()
        return process.set(address, data, 128)
    } finally {
        data.release(128)
    }
}