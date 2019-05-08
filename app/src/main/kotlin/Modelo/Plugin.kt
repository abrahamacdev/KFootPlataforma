package Modelo

import Utiles.plus
import java.io.File
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicLong

class Plugin (val jarFile: File, val metodoCargado: Method, val metodoEjecucion: Method, val clasePrincipal: Class<*>, val nombrePlugin: String = "Desconocido"){

    init {
        Companion.ID += 1
    }

    val ID: AtomicLong = Companion.ID

    companion object {

        // Nos servirá para identificar a cada plugin unequívocamente
        private var ID: AtomicLong = AtomicLong(0)
    }

    override fun equals(o: Any?): Boolean {
        if (o == null) return false
        if (this.javaClass != o.javaClass) return false
        if (this === o) return true
        val plugin = o as Plugin
        return ID === plugin.ID
    }
}