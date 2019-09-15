package Controlador.Supervisor

import KFoot.DEBUG
import KFoot.Logger
import Modelo.Plugin.EstadosPlugin
import Modelo.Plugin.Plugin
import Utiles.esIgual
import lib.Plugin.IPlugin
import java.lang.Exception
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.ArrayList

/**
 * El [Supervisor] se encarga de gestionar toddo lo relacionado con
 * la ejecucion de los plugins y de la correcta ejecucion de estos
 */
class Supervisor: ISupervisor, ISupervisor.onPluginEjecutado {

    // Lista con los plugins a ejecutar
    private val plugins: ArrayList<Plugin> = ArrayList()

    companion object {

        private val instancia = Supervisor()

        fun getInstance(): Supervisor{
            return instancia
        }
    }

    private constructor()


    override fun anadirPlugin(plugin: Plugin): Boolean{

        // Comprobamos si hay otro plugin con el mismo
        val existeCopia = plugins.firstOrNull(){

            // Comprobamos que los objetos no sean iguales
            val sonCopia = plugin.equals(it)

            // Comprobamos que la ruta de los plugins sea diferente (puede
            // que sean objetos diferentes pero se refieren al mismo archivo jar)
            val mismoJar = plugin.jar.canonicalPath.equals(it.jar.canonicalPath)

            sonCopia || mismoJar
        }

        // Si no hay ningun valor significa que podemos añadir el plugin
        if (existeCopia == null){
            // Añadimos el plugin a la lista
            plugins.add(plugin)
            return true
        }

        return false
    }


    override fun anadirListaPlugins(listaPlugins: List<Plugin>): ArrayList<Plugin>{

        // Lista con los plugins no validos
        val noValidos = ArrayList<Plugin>()


        // Recorremos cada uno de los plugins que quieren
        // añadirse a la lista
        listaPlugins.forEach {

                // Si no se ha podido añadir el plugin a la lista de los que se
                // ejecutara, lo añadimos a la lista de los no validos
                if (!anadirPlugin(it)){
                    noValidos.add(it)
                }
            }

        return noValidos
    }



    override fun eliminarPlugin(pos: Int): Boolean{

        // TODO: Comprobar primero que el plugin no se este ejecutando

        // Comprobamos que el índice solicitado sea válido
        if (pos >= 0 && pos < plugins.size){
            plugins.removeAt(pos)
            return true
        }

        return false
    }

    override fun eliminarPlugin(plugin: Plugin): Boolean{

        // TODO: Comprobar primero que el plugin no se este ejecutando

        // Buscamos la posición del plugin dentro de la lista
        val posicion = plugins.indexOfFirst {
            it.equals(plugin)
        }

        if (posicion >= 0){
            plugins.removeAt(posicion)
            return true
        }

        return false
    }



    override fun obtenerPluginsCargados(): ArrayList<Plugin> {
        return plugins
    }



    /*override fun ejecutarPlugins() {

        // Activamos todos los plugins que estén inactivos
        plugins.filter { it.getEstadoActual() == EstadosPlugin.INACTIVO }.forEach{ it.activar(null,this) }
    }*/

    override fun ejecutarPlugin(id: AtomicLong, onResultadoInicioListener: IPlugin.onResultadoAccionListener?){

        // Buscamos el plugin en la lista de plugins que se han encontrado
        val plugin = plugins.firstOrNull{ it.ID.esIgual(id)}

        var errorMsg: String? = null

        // Comprobamos que haya un plugin en la lista con el id solicitado
        if (plugin != null ){

            val estadoPlugin = plugin.getEstadoActual()

            when {

                // Si el plugin está inactivo Ó
                // ha completado su ejecución anteriormente Ó
                // tuvo un error cuando se ejecutó anteriormente,
                // lo ejecutamos
                estadoPlugin == EstadosPlugin.INACTIVO || estadoPlugin == EstadosPlugin.COMPLETADO  || estadoPlugin == EstadosPlugin.ERROR -> {

                    plugin.activar(onResultadoInicioListener, this)
                }

                // Si está actualmente en activo no hacemos nada
                estadoPlugin == EstadosPlugin.ACTIVO -> {errorMsg = "El plugin se está ejecutando actualmente"}
            }
        }

        // Si no se ha podido ejecutar el plugin, pasamos el error
        if (errorMsg != null){
            if (onResultadoInicioListener != null){
                onResultadoInicioListener.onError(Exception(errorMsg))
            }
        }
    }



    override fun pausarPlugins(listener: IPlugin.onResultadoAccionListener?) {

        val pausados = LinkedList<Plugin>()

        val avisoFinal = {

            // Comprobamos que la cantidad de pausados sea igual
            // a la cantidad de plugins disponibles
            if (pausados.size == plugins.size && listener != null){
                listener.onCompletado()
            }
        }

        plugins.forEach {
            it.pausar(object : IPlugin.onResultadoAccionListener {
                override fun onCompletado() {
                    pausados.add(it)

                    // Comprobamos que sea el último plugin que se pausa para
                    // transmitir la finalización a través del listener
                    avisoFinal.invoke()
                }

                override fun onError(e: Exception) {
                    pausados.add(it)

                    // Comprobamos que sea el último plugin que se pausa para
                    // transmitir la finalización a través del listener
                    avisoFinal.invoke()
                }
            })
        }
    }



    override fun cancelarPlugins(listener: IPlugin.onResultadoAccionListener?) {
        val cancelados = LinkedList<Plugin>()

        // No hay plugins que cancelar
        if (plugins.size == 0){
            listener!!.onCompletado()
        }

        val block: (plugin: Plugin) -> Unit = {

            // Añadimos el plugin a la lista de los cancelados
            cancelados.add(it)

            // Comprobamos que la cantidad de pausados sea igual
            // a la cantidad de plugins disponibles
            if (cancelados.size == plugins.size && listener != null){
                listener.onCompletado()
            }
        }

        plugins.forEach {
            it.cancelar(object : IPlugin.onResultadoAccionListener {
                override fun onCompletado() {
                    block.invoke(it)
                }

                override fun onError(e: Exception) {
                    block.invoke(it)
                }
            })
        }
    }



    override fun onEjecutadoCorrectamente(plugin: Plugin) {
        //pluginsCompletados.add(plugin)
    }

    // TODO: Completar el funcionamiento del método
    override fun onErrorEnEjecucion(plugin: Plugin) {
        //pluginsCompletados.add(plugin)
    }
}
