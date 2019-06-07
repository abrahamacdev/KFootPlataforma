package Controlador

import Modelo.Plugin
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*

/**
 * El [Supervisor] se encarga de gestionar toddo lo relacionado con
 * la ejecucion de los plugins y de la correcta ejecucion de estos
 */
class Supervisor {

    private val plugins: ArrayList<Plugin> = ArrayList()
    private var estaEjecutando: Boolean = false

    companion object {

        // Instancia del [Supervisor]
        val instancia: Supervisor = Supervisor()
    }

    private constructor()


    /**
     * Añadimos el plugin pasado por parámetro a la
     * lista de plugins que se ejecutarán
     *
     * @param plugin: Plugin a añadir a la lista
     *
     * @return Boolean: Si ha podido añadirse el plugin a la lista
     */
    fun anadirPlugin(plugin: Plugin): Boolean{

        // Evitamos añadir plugins cuando se están ejecutando
        if (!estaEjecutando){

            // Comprobamos que el plugin cumpla los requisitos necesarios
            if (comprobacionesPreAnadido(plugin)){

                // Añadimos el plugin a la lista
                plugins.add(plugin)
                return true
            }

        }
        return false
    }

    /**
     * Añadimos la lista de plugins pasados
     * por parámetros lista de plugins que se ejecutarán
     *
     * @param listaPlugins: LIsta de plugins que se añadirán a la lista
     *
     * @return List<Plugin>: Lista con los plugins que no pueden añadirse a la lista
     */
    fun anadirListaPlugin(listaPlugin: List<Plugin>): List<Plugin>{

        // Lista con los plugins no validos
        val noValidos = ArrayList<Plugin>()

        // Evitamos añadir plugins cuando ya hay ejecutandose
        if (!estaEjecutando){

            // Recorremos cada uno de los plugins que quieren
            // añadirse a la lista
            listaPlugin.forEach {

                // Si no se ha podido añadir el plugin a la lista de los que se
                // ejecutara, lo añadimos a la lista de los no validos
                if (!anadirPlugin(it)){
                    noValidos.add(it)
                }
            }
        }

        return noValidos
    }

    /**
     * Eliminamos de la lista el plugin que se
     * encuentra en la posición pasada por parámetro
     *
     * @param indice: Posición del plugin a eliminar
     *
     * @return Boolean: Si se ha eliminado
     */
    fun eliminarPlugin(indice: Int): Boolean{

        if (!estaEjecutando){

            // Comprobamos que el índice solicitado sea válido
            if (indice >= 0 && indice < plugins.size){
                plugins.removeAt(indice)
                return true
            }
        }

        return false
    }

    /**
     * Eliminamos de la lista al plugin
     * que tenga el mismo identificador
     * que el pasado por parámetro
     *
     * @param plugin: Plugin que se buscara en el array
     */
    fun eliminarPlugin(plugin: Plugin): Boolean{

        if (!estaEjecutando){

            // Buscamos la posición del plugin dentro de la lista
            val posicion = plugins.indexOfFirst {
                it.equals(plugin)
            }

            if (posicion >= 0){
                plugins.removeAt(posicion)
                return true
            }
        }

        return false
    }



    /**
     * Desde aquí ejecutaremos todos los plugins
     * válidos que hallan sido encontrados en el directorio
     * de plugins establecido
     */
    suspend fun lanzarPlugins(){

        // Bloqueamos la modificación de los plugins existentes
        estaEjecutando = true

        // Activamos todos los plugins de la lista
        plugins.forEach { plugin ->

            // Ejecutamos los plugins en una coroutina dedicada
            plugin.activar()
        }
    }

    /**
     * Esperamos a que todos los plugins hallan terminado
     * de ejecutarse
     */
    suspend fun esperarFinalizacionPlugins(){

        // Esperamos a que se terminen de ejecutar los plugins
        plugins.forEach {
            it.job.join()
        }

        // Acciones post-ejecucion plugins
        ejecucionFinalizada()
    }

    /**
     * Forzamos el fin de la ejecucion de los
     * plugins que esten activos
     */
    fun forzarFinEjecucionPlugins(){

        plugins.forEach {
            it.forzarAcabadoPlugin()
        }
    }

    /**
     * Esta funcion se ejecutara cada
     * vez que todos los plugins hallan finalizado
     */
    fun ejecucionFinalizada(){

        if (estaEjecutando){

            // Desbloqueamos el Supervisor
            estaEjecutando = false

            // Eliminamos de la lista de plugins a ejecutar
            // los que ya se han ejecutado
            plugins.clear()
        }
    }



    /**
     * Comprobamos que el plugin cumpla los requisitos
     * necesarios antes de ser añadido a la lista de plugins
     * que se ejecutara
     *
     * @param plugin: Plugin a comprobar
     * @return Boolean: Si el plugin pasa
     */
    private fun comprobacionesPreAnadido(plugin: Plugin): Boolean{

        // Comprobamos si el plugin ya esta en la lista
        val yaAnadido = plugins.firstOrNull{ it.equals(plugin) }

        // El plugin no esta aun en la lista
        if (yaAnadido == null){

            // Comprobamos que la ruta del plugin no sea igual
            // a la de otro ya añadido
            val rutaRepetida = plugins.firstOrNull{
                it.jarFile.canonicalPath.equals(plugin.jarFile.canonicalPath)
            }

            // No hay ningun plugin que apunte a la misma ruta
            if (rutaRepetida == null){

                // Obtenemos la ruta del archivo en el que se guardaran
                // los datos del nuevo plugin
                val rutaArchivoNuevo = plugin.obtenerRutaArchivoGuardado()

                val modificaMismoArchivo = plugins.firstOrNull{
                    println(it.obtenerRutaArchivoGuardado().toString() + " = " + rutaArchivoNuevo.toString())
                    it.obtenerRutaArchivoGuardado() == rutaArchivoNuevo
                }

                // No hay ningun plugin que guarda los datos
                // en el mismo archivo
                if (modificaMismoArchivo == null) {

                    return true
                }
            }
        }

        return false
    }

    /**
     * Comprobamos si los plugins han terminado
     * de scrapear los datos
     *
     * @return Boolean si han terminado todos o no
     */
    fun comprobarTodosPluginsFinalizados(): Boolean{
        return plugins.all { it.haTerminado() }
    }
}
