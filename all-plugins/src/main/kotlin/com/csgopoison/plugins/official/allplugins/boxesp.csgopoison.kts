package com.csgopoison.plugins.official.allplugins

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.csgopoison.plugins.boxesp.*
import com.csgopoison.settings.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs
import kotlin.math.sign
import kotlin.reflect.*

var boxFlags = BoxFlags.ALL
val currentBoxData = CopyOnWriteArrayList<BoxData>()

draw ({
    currentBoxData.forEach {
        val entity = it.entity as Player

        if (entity.address <= 0 || entity.address == entityManager.me.address || entity.dormant || entity.dead || entity.team == entityManager.me.team) {
            //Die
        } else {
            val bbox = BoundingBox2D()
            bbox.setFakeBBox(entity, EntityType.CCSPlayer)

            shapes.rect(bbox.left.toFloat(), bbox.top.toFloat(), bbox.width(), bbox.height())
        }
    }
}) {
    if (!BOX_ESP_ENABLED) return@draw false

    return@draw true
}

every(250, {
    //TODO settings class, on settings reinit/reflection
    constructFlagData()
    constructBoxData()
}) {
    if (!BOX_ESP_ENABLED) return@every false
    if (entityManager.meAddress <= 0 || entityManager.me.dead) return@every false

    return@every true
}

fun constructBoxData() {
    forEntities(EntityType.ccsPlayer) { entityData ->
        val entity = entityData.entity as Player

        if (entity.address <= 0 || entity.address == entityManager.meAddress
            || entity.dormant   || entity.dead
            || entity.team == entityManager.me.team) {

            val lastValue = currentBoxData.firstOrNull { it.entity == entity }
            if (lastValue != null) {
                currentBoxData.remove(lastValue)
            }

            return@forEntities
        }

        if (currentBoxData.firstOrNull { it.entity == entity } == null) {
            currentBoxData.add(BoxData(entity, boxFlags))
        }
    }
}

fun constructFlagData() {
    boxFlags.clear()

    BoxFlags.ALL.forEach {
        if (it.enabled) {
            boxFlags.add(it)
        }
    }
}

fun BoundingBox2D.setFakeBBox(ent: Entity, type: EntityType) = apply {
    var entity: Entity

    lateinit var vTop: com.csgopoison.Vector
    lateinit var vBottom: com.csgopoison.Vector

    if (type == EntityType.CCSPlayer) {
        entity = ent as Player

        val headBone = entity.getBonePos(8)
        val feetPos = entity.absPosition()

        headBone.z += 10
        feetPos.z -= 10

        vTop = worldToScreen(headBone)
        vBottom = worldToScreen(feetPos)

        if (vTop.z > 0 && vBottom.z > 0) {
            createFakeBBox(vTop, vBottom, this)
        }
    } else {

    }
}

fun createFakeBBox(vTop: com.csgopoison.Vector, vBottom: com.csgopoison.Vector, bbox: BoundingBox2D): BoundingBox2D {
    val vMiddleY = (vTop.y + vBottom.y) / 2.0
    var boxH = vBottom.y - vTop.y

    val sW = abs(((boxH / 5.0) * 2.0) / 2.0)
    val sH = 2.0

    val midX = abs(abs(vTop.x) - abs(vBottom.x))
    if (abs(boxH) < sW + midX) {
        boxH = ((sW + midX) * sign(boxH))
    }

    if (vBottom.x > vTop.x) {
        bbox.left = (vBottom.x + sW)
        bbox.right = (vTop.x - sW)
    } else {
        bbox.left = (vTop.x + sW)
        bbox.right = (vBottom.x - sW)
    }

    bbox.top = (vMiddleY - boxH / 2.0 + sH)
    bbox.bottom = (vMiddleY + boxH / 2.0 + sH)

    return bbox
}