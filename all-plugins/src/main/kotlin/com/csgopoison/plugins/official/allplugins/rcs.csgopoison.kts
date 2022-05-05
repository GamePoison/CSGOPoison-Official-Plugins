package com.csgopoison.plugins.official.allplugins

import com.csgopoison.settings.*

private val rcsUpdateRateMS = 1L

private val lastAppliedRCS = Vector()
private var lastTime = 0.0

every (rcsUpdateRateMS, { context ->
    val shotsFired = entityManager.me.shotsFired
    val punch = entityManager.me.aimPunch()

    val lastIsZero = lastAppliedRCS.isZero()
    val forceChange = (shotsFired == 0 && !lastIsZero || false/*TODO check if bullets <= 0*/)

    val curTime = globalVars.curTime
    val deltaTime = (curTime - lastTime).coerceIn(0.0, 1.0)

    if (deltaTime == 0.0) {
        return@every
    }

    if (forceChange || shotsFired > 1) {
        val tPunchX = punch.x * 2F
        val tPunchY = punch.y * 2F

        val punchToApply = Vector()

        if (RCS_TYPE == E_RCS_TYPE.STABLE) {
            if (lastIsZero) lastAppliedRCS.set(tPunchX, tPunchY)

            val realPunch = Vector(tPunchX, tPunchY, 0.0)

            punchToApply.set(realPunch - lastAppliedRCS)
            punchToApply.scl(RCS_X, RCS_Y)

            lastAppliedRCS.apply{ x += punchToApply.x; y += punchToApply.y }
        } else if (RCS_TYPE == E_RCS_TYPE.LEGACY) {
            if (lastIsZero) lastAppliedRCS.set(punch.x, punch.y)

            punchToApply.set(punch.x - lastAppliedRCS.x, punch.y - lastAppliedRCS.y, 0.0)
            punchToApply.scl(1 + RCS_X, 1 + RCS_Y)

            lastAppliedRCS.set(punch.x, punch.y)
        }

        val clientAngle = entityManager.clientState.angle()
        clientAngle.apply {
            x -= punchToApply.x
            y -= punchToApply.y
            normalizeAngle()
        }
        clientState.setAngle(clientAngle)

        if (!RCS_RETURN_AIM && forceChange) lastAppliedRCS.zeroOut()

        lastTime = curTime
    }

    return@every
}) {
    if (entityManager.meAddress <= 0) return@every false
    if (entityManager.me.dead) return@every false

    return@every true
}