package com.csgopoison.plugins.official.allplugins

private val teamColor = Color(0, 0, 255, 1.0)
private val enemyColor = Color(255, 0, 0, 1.0)

private val glowUpdateRateMS = 4L

//Update the available list of entities : TODO : make this part of PluginScript -v
//This is a global entity update as allEntities is stored in CSGOContext : this should be moved to an individiual location
every(4, {
	forEntities(EntityType.ccsPlayer) { entityData ->
		val color = if (entityManager.me.team == entityData.entity.team) teamColor else enemyColor
		glowEntity(entityData.glowAddress, color)
	}

	return@every
}) {
	if (entityManager.meAddress <= 0) return@every false
	if (entityManager.me.dead) return@every false

	return@every true
}

fun glowEntity(glowAddress: Long, color: Color): Boolean {
	val data = Pointer.allocate(60)
	try {
		if (!process.source(glowAddress, 60, data)) return false
		data[0x4] = color.red / 255F
		data[0x8] = color.green / 255F
		data[0xC] = color.blue / 255F
		data[0x10] = color.alpha.toFloat()
		data[0x24] = 1.toByte()
		data[0x25] = 0.toByte()
		data[0x26] = 0.toByte() // INV_GLOW_ESP
		data[0x2C] = 0.toByte() // glowType
		return process.set(glowAddress, data, 60)
	} finally {
		data.release(60)
	}
}