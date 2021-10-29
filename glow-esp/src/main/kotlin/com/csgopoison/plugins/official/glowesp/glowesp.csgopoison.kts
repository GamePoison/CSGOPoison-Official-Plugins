package com.csgopoison.plugins.official.glowesp

val glowObjectSize = 56

private val teamColor = Color(0, 0, 255, 1.0)
private val enemyColor = Color(255, 0, 0, 1.0)

every(1024) {
	val meAddress = clientModule.uint(dwLocalPlayer)
	if (meAddress <= 0) return@every
	
	val me = player(meAddress)
	
	val myTeam = me.team
	if (myTeam == 0L) return@every
	
	val glowObject = clientModule.uint(dwGlowObject)
	val glowObjectCount = clientModule.uint(dwGlowObject + 4)
	for (glowIndex in 0..glowObjectCount) {
		val glowAddress = glowObject + (glowIndex * glowObjectSize) + 4
		val entityAddress = process.uint(glowAddress)
		if (entityAddress == 0L || entityAddress == meAddress) continue
		val entityType = entityType(entityAddress)
		if (entityType != EntityType.CCSPlayer) continue
		
		val entity = player(entityAddress)
		if (entity.dormant || entity.dead) continue
		
		val entityTeam = entity.team
		if (entityTeam == 0L) continue
		
		val color = if (myTeam == entityTeam) teamColor else enemyColor
		glow(glowAddress, color)
	}
}

fun glow(glowAddress: Long, color: Color): Boolean {
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