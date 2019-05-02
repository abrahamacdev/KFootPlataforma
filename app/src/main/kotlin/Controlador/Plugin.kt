package Controlador

import java.io.File
import java.lang.reflect.Method

class Plugin (val jarFile: File, val metodoCargado: Method, val metodoEjecucion: Method, val clasePrincipal: Class<*>, val nombrePlugin: String = "Desconocido"){

    init {

        Plugin.Companion.ID += 1
    }

    val ID: Long = Plugin.Companion.ID

    companion object {

        // Nos servirá para identificar a cada plugin unequívocamente
        private var ID: Long = 0
    }

    override fun equals(o: Any?): Boolean {
        if (o == null) return false
        if (this.javaClass != o.javaClass) return false
        if (this === o) return true
        val plugin = o as Plugin
        return ID === plugin.ID
    }
}