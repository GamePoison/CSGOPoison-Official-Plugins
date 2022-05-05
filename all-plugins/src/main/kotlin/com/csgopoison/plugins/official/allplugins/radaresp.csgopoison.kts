package com.csgopoison.plugins.official.allplugins

private val radarUpdateRateMS = 1024L

every(radarUpdateRateMS, {
    forEntities(EntityType.ccsPlayer) { entityData ->
        radarPlayer(entityData.entityAddress, true)
    }

    return@every
}) {
    if (entityManager.meAddress <= 0) return@every false
    if (entityManager.me.dead) return@every false

    return@every true
}

fun radarPlayer(playerAddress: Long, show: Boolean): Boolean {
    return process.set(playerAddress + netVars.bSpotted, show)
}