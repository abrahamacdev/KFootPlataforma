package Utiles

import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.io.File
import java.util.concurrent.atomic.AtomicLong

object Utils {

    /**
     * Comprobamos cual es el separador que hay que
     * utilizar para las rutas del SO del cliente
     *
     * @return String: Separador que hay que utilizar en el SO
     */
    /*fun determinarSeparador(): String {

        var separador = ""

        // Recuperamos el SO que se esta ejecuutando en el cliente
        val SO = Utils.determinarSistemaOperativo()

        when {
            // Windows
            SO == com.kscrap.libreria.Utiles.Constantes.SO.WINDOWS -> {separador = "\\"}

            // Ubuntu
            SO == com.kscrap.libreria.Utiles.Constantes.SO.UBUNTU -> {separador = "/"}
        }

        return separador
    }*/

    /**
     * Obtenemos todos los .jar del directorio de plugins establecido
     *
     * @return Observable<File>?: Observable con los archivos que son .jar
     */
    fun obtenerJarsDirPlugins(): Observable<File>?{

        // Observable con todoo el contenido de un directorio
        val archivos: Observable<File> = File(Constantes.DIRECTORIO_PLUGINS).listFiles().toObservable()

        // Lista con todos los jars del directorio
        val jars: ArrayList<File> = ArrayList()

        // Filtramos por su extensión
        archivos.filter { it.isFile && it.extension.equals("jar")}
                .subscribe{
                    jars.add(it)
                }

        // Si hay '.jar's devolveremos el observable
        if (jars.size >= 0){
            return jars.toObservable()
        }

        return null
    }
}

// Permite la suma de dos números atómicos
operator fun AtomicLong.plus (otro: AtomicLong): AtomicLong = AtomicLong(this.get() + otro.get())
operator fun AtomicLong.plus (otro: Long): AtomicLong = AtomicLong(this.get() + otro)
