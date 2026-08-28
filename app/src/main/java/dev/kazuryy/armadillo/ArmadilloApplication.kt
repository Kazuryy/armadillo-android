package dev.kazuryy.armadillo

import android.app.Application
import android.util.Log
import dev.kazuryy.armadillo.util.CrashHandler
import dev.kazuryy.armadillo.util.SocketManager
import dev.kazuryy.armadillo.util.StandbyDetector
import java.io.File

class ArmadilloApplication : Application(), StandbyDetector.StandbyListener {

    private val tag = "ArmadilloApplication"
    private var standbyDetector: StandbyDetector? = null

    private val standbyListeners = mutableListOf<StandbyListener>()

    lateinit var socketManager: SocketManager private set

    override fun onCreate() {
        super.onCreate()

        CrashHandler.initialize(this)

        Log.d(tag, "Armadillo application starting")

        val socketPath = File(filesDir, "armadillo.sock").absolutePath
        socketManager = SocketManager(socketPath)

        standbyDetector = StandbyDetector(this, this)
        standbyDetector?.start()
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(tag, "Armadillo application terminating")
        standbyDetector?.stop()
        standbyDetector = null
    }

    override fun onEnterStandby() {
        Log.i(tag, "Device entered standby mode, pausing background operations")
        synchronized(standbyListeners) {
            standbyListeners.forEach { it.onEnterStandby() }
        }
    }

    override fun onExitStandby() {
        Log.i(tag, "Device exited standby mode, resuming background operations")
        synchronized(standbyListeners) {
            standbyListeners.forEach { it.onExitStandby() }
        }
    }

    fun registerStandbyListener(listener: StandbyListener) {
        synchronized(standbyListeners) {
            if (!standbyListeners.contains(listener)) {
                standbyListeners.add(listener)
            }
        }
    }

    fun unregisterStandbyListener(listener: StandbyListener) {
        synchronized(standbyListeners) {
            standbyListeners.remove(listener)
        }
    }

    interface StandbyListener {
        fun onEnterStandby()
        fun onExitStandby()
    }
}
