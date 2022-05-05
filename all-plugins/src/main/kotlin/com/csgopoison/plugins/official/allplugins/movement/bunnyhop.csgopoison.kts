package com.csgopoison.plugins.official.allplugins.movement

every(2, {
    //TODO cursor update
    if (input.keyDown(32)) {
        csgo.clientModule[clientOffsets.dwForceJump] = 6
    }
}) {
    if (entityManager.meAddress <= 0 ||
        entityManager.me.dead ||
        !entityManager.me.onGround) return@every false


    return@every true
}