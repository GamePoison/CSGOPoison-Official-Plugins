package com.csgopoison.plugins.official.allplugins

import com.csgopoison.settings.*
import org.gamepoison.*
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

private val linearAimTargetUpdateRateMS = 250L
private val linearAimRunSpeedMS = 1L
private val weaponFov = 40

private var bestTarget = Pair(entityManager.me, 180.0)

//TODO this will need optimization, current POC phase
//people of color :)

//Update the best target to aim at
every(linearAimTargetUpdateRateMS, {
    bestTarget = findBestAimTarget()

    return@every
})

private var startSprayTime = 0.0

every(linearAimRunSpeedMS, {
    return@every

    //Dont test my limit
    if (input.keyDown(1)) {
        if (startSprayTime <= 0.0) { //TODO check automatic
            startSprayTime = globalVars.curTime
        }

        val currentAngle = entityManager.clientState.angle()
        val destinationAngle = calculateAngle(entityManager.me, bestTarget.first.getBonePos(8))

        applySmoothType(AIM_TYPE, currentAngle, destinationAngle)

        //Shoot
        clientWriteAim(currentAngle)
    } else {
        startSprayTime = 0.0
    }

    return@every
}) {
    if (entityManager.meAddress <= 0) return@every false
    if (entityManager.me.dead) return@every false
    if (bestTarget.first.address <= 0) return@every false
    if (bestTarget.first.dead) return@every false

    return@every true
}

fun applySmoothType(smoothType: E_SMOOTH_TYPE, currentAngle: Vector, destinationAngle: Vector) {
    val shotsFiredSmooth = entityManager.me.shotsFired * SHOTS_FIRED_ADDITIVE_SMOOTH

    when (smoothType) {
        E_SMOOTH_TYPE.FLAT -> {
            currentAngle.lerpAngle(destinationAngle, (AIM_SMOOTHNESS+shotsFiredSmooth).coerceIn(0.0, 1.0)) //fuck u eston lemme coerce that ass
        }

        E_SMOOTH_TYPE.FLAT_RAMP -> {
            val smoothTime = (globalVars.curTime - startSprayTime) / LINEAR_RAMP_TIME
            val linearRamp = linearRampUp(MIN_AIM_SMOOTHNESS, MAX_AIM_SMOOTHNESS, smoothTime)

            currentAngle.lerpAngle(destinationAngle, linearRamp+shotsFiredSmooth)
        }

        E_SMOOTH_TYPE.INV_FLAT_RAMP -> {
            val smoothTime = (globalVars.curTime - startSprayTime) / LINEAR_RAMP_TIME
            val linearRamp = linearRampDown(MIN_AIM_SMOOTHNESS, MAX_AIM_SMOOTHNESS, smoothTime)

            currentAngle.lerpAngle(destinationAngle, linearRamp+shotsFiredSmooth)
        }
    }
}

fun clientWriteAim(destinationAngle: Vector) {
    entityManager.clientState.setAngle(destinationAngle)
}

fun linearRampUp(min: Double, max: Double, time: Double): Double {
    return min + (max - min) * time.coerceIn(0.0, 1.0)
}

fun linearRampDown(min: Double, max: Double, time: Double): Double {
    return max - (max - min) * time.coerceIn(0.0, 1.0)
}

//Find the best target to aim at, return entity & fov
fun findBestAimTarget(): Pair<Player, Double> {
    var lowestFov = Double.MAX_VALUE
    var closestFovEntity: Player = player(0) //TODO how to ...

    forEntities(EntityType.ccsPlayer) {
        val entity = it.entity as Player

        if (entityManager.me.address <= 0 || entity.address <= 0 || entity.address == entityManager.me.address) {
            return@forEntities
        }

        if (entity.dormant || entity.dead) {
            return@forEntities
        }

        val tmpFov = getStaticFov(entityManager.me, entity.getBonePos(8))

        if (tmpFov < lowestFov) {
            lowestFov = tmpFov
            closestFovEntity = it.entity as Player
        }
    }

    //TODO bruh
    return Pair(closestFovEntity, lowestFov)
}

fun getStaticFov(fromEntity: Player, position: Vector): Double {
    //val calcAng = calculateAngle(me, entity)
    val calcAng = calculateAngle(fromEntity, position)

    val currentAngle = entityManager.clientState.angle()

    val delta = currentAngle - calcAng
    delta.z = 0.0

    val fov = sqrt(delta.x.pow(2.0) + delta.y.pow(2.0))

    return round(fov * 1000.0) / 1000.0
}

//TODO add punch

//Calculate the angle from player eyes to destinationPos
fun calculateAngle(fromEntity: Player, destinationPos: Vector): Vector {
    val entityPos = fromEntity.eyePosition()

    val deltaVector = destinationPos - entityPos

    val hyp = sqrt(deltaVector.x * deltaVector.x + deltaVector.y * deltaVector.y)

    var angPitch = toDegrees(atan2(-deltaVector.z, hyp))
    var angYaw = toDegrees(atan2(deltaVector.y, deltaVector.x))

    //Recoil
    val entityPunch = fromEntity.aimPunch()
    angPitch -= entityPunch.x * 2F
    angYaw -= entityPunch.y * 2F

    return Vector(angPitch, angYaw, 0.0).normalizeAngle()
}