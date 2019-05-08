package Utiles

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import java.util.concurrent.atomic.AtomicLong

object Utils {

    /**
     * Logueamos los mensajes pasados por parámetro según el "nivel de debug" necesitado
     * y el establecido para la sesión actual.
     *
     * @param Constantes.DEBUG nivelRequerido: Nivel de debug requerido para mostrar el mensaje
     * @param String mensaje: Texto a mostrar
     * @param Color color: Color con el que se mostrará el mensaje
     */
    fun debug(nivelRequerido: Constantes.DEBUG, mensaje: String, color: Color = Color.BLACK){
        // Comprobamos que queremos loguear
        if (Constantes.DEBUG.DEBUG_LEVEL.value != Constantes.DEBUG.DEBUG_NONE.value){

            // El mensaje es de un test y estamos en el nivel de "Test"
            if (nivelRequerido.value == Constantes.DEBUG.DEBUG_LEVEL.value && Constantes.DEBUG.DEBUG_LEVEL.value == Constantes.DEBUG.DEBUG_TEST.value){
                println(Kolor.foreground(mensaje,color))
            }

            // Ej: Si el nivel actual es 'Avanzado', todos los de nivel 'Simple' también se mostrarán
            else if (nivelRequerido.value <= Constantes.DEBUG.DEBUG_LEVEL.value && nivelRequerido.value != Constantes.DEBUG.DEBUG_NONE.value){
                println(Kolor.foreground(mensaje,color))
            }
        }
    }
}

// Permite la suma de dos números atómicos
operator fun AtomicLong.plus (otro: AtomicLong): AtomicLong = AtomicLong(this.get() + otro.get())
operator fun AtomicLong.plus (otro: Long): AtomicLong = AtomicLong(this.get() + AtomicLong(otro).get())
