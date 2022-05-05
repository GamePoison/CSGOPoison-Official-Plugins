package com.csgopoison.plugins.official.allplugins.movement

import com.csgopoison.settings.AUTOSTRAFE_KEY
import com.csgopoison.settings.AUTOSTRAFE_ON_KEY
import com.csgopoison.settings.AUTOSTRAFE_TOGGLED
import java.awt.event.KeyEvent

private var lastYaw = 0.0
every(2, {
    val currentYaw = entityManager.clientState.angle().y
    val grounded = entityManager.me.onGround

    if (!grounded) {
        //If we don't need autostrafe key, or it's already being pressed
        if (!AUTOSTRAFE_ON_KEY || input.keyDown(AUTOSTRAFE_KEY)) {
            //If user isn't strafing
            if (!input.keyDown(KeyEvent.VK_A) && !input.keyDown(KeyEvent.VK_D)) {
                if (currentYaw > lastYaw) {
                    input.inputRobot.keyPress(KeyEvent.VK_A)
                    input.inputRobot.keyRelease(KeyEvent.VK_A)
                } else if (currentYaw < lastYaw) {
                    input.inputRobot.keyPress(KeyEvent.VK_D)
                    input.inputRobot.keyRelease(KeyEvent.VK_D)
                }
            }
        }
    }

    lastYaw = currentYaw
}) { //Prechecks
    if (!AUTOSTRAFE_TOGGLED) return@every false
    if (entityManager.meAddress <= 0) return@every false
    if (entityManager.me.dead) return@every false

    return@every true
}