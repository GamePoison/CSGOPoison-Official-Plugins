package com.csgopoison.plugins.official.allplugins

import com.csgopoison.Console.log
import com.csgopoison.plugins.sendPackets
import com.csgopoison.settings.USER_CMD_ENABLED
import com.csgopoison.usercmd.UserCMD
import java.awt.event.KeyEvent
import java.lang.Thread.sleep
import kotlin.io.path.Path
import kotlin.io.path.appendLines

//TODO netchannel last command is 1 faster than dwclientstatelastoutgoingcommand... could cause issues?
//val pNetChannel = csgo.process.uint(clientState + csgo.engineOffsets.dwClientStateNetChannel)
//    val lastCommand = csgo.process.int(pNetChannel + 0x18)


var lastProcessedCommand = 0
private var oldUserCMD = Pointer.allocate(100)
private var lastCMDTime = 0.0

every(1, {
    //get sillied

   // println("total missed: " + totalMissed)


    return@every
}) {
    if (!USER_CMD_ENABLED) return@every false
    if (entityManager.meAddress <= 0) return@every false
    if (entityManager.me.dead) return@every false
    //inBackground

    return@every true
}

Runtime.getRuntime().addShutdownHook(
//    Thread {
//        sleep(100)
//        sendPackets(true)
//        sleep(100)
//    }
    Thread {
        sleep(100)
        sendPackets(true)
        sleep(100)
    }
)