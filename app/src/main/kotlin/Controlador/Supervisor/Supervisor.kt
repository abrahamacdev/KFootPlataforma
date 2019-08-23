package Controlador.Supervisor

import Modelo.Plugin.EstadosPlugin
import Modelo.Plugin.Plugin
import Utiles.esIgual
import lib.Plugin.IPlugin
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

    // Lista con los plugins que han terminado de ejecutarse
    private val pluginsCompletados: LinkedList<Plugin> = LinkedList()

    companion object {

        private val instancia = Supervisor()

        fun getInstance(): Supervisor{
            return instancia
        }
    }

    private constructor()

    /**
     * Añadimos el plugin pasado por parámetro a la
     * lista de plugins que se ejecutarán
     *
     * @param plugin: PluginView a añadir a la lista
     *
     * @return Boolean: Si ha podido añadirse el plugin a la lista
     */
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

    /**
     * Añadimos la lista de plugins pasados
     * por parámetros lista de plugins que se ejecutarán
     *
     * @param listaPlugins: LIsta de plugins que se añadirán a la lista
     *
     * @return List<PluginView>: Lista con los plugins que no pueden añadirse a la lista
     */
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


    /**
     * Eliminamos de la lista el plugin que se
     * encuentra en la posición pasada por parámetro
     *
     * @param pos: Posición del plugin a eliminar
     *
     * @return Boolean: Si se ha eliminado
     */
    override fun eliminarPlugin(pos: Int): Boolean{

        // TODO: Comprobar primero que el plugin no se este ejecutando

        // Comprobamos que el índice solicitado sea válido
        if (pos >= 0 && pos < plugins.size){
            plugins.removeAt(pos)
            return true
        }

        return false
    }

    /**
     * Eliminamos de la lista al plugin
     * que tenga el mismo identificador
     * que el pasado por parámetro
     *
     * @param plugin: PluginView que se buscara en el array
     */
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


    /**
     * Ejecutamos todos los plugins que se encuentren en la lista,
     * cada uno en una coroutina separada
     */
    override fun ejecutarPlugins() {

        // Activamos todos los plugins que estén inactivos
        plugins.filter { it.getEstadoActual() == EstadosPlugin.INACTIVO }.forEach{ it.activar(null,this) }
    }

    /**
     * Ejectutamos el plugin que tenga el [id] pasado
     * @param id: Id del plugin a ejecutar
     * @param onResultadoInicioListener: Listener por el que transmitiremos el correcto inicio del plugin
     */
    fun ejecutarPlugin(id: AtomicLong, onResultadoInicioListener: IPlugin.onResultadoAccionListener? = null){
        val plugin = plugins.firstOrNull{ it.ID.esIgual(id)}

        println(plugin)

        // Activamos el plugin
        if (plugin != null && plugin.getEstadoActual() == EstadosPlugin.INACTIVO){
            plugin.activar(onResultadoInicioListener, this)
        }
    }




    override fun onEjecutadoCorrectamente(plugin: Plugin) {
        pluginsCompletados.add(plugin)
    }

    // TODO: Completar el funcionamiento del método
    override fun onErrorEnEjecucion(plugin: Plugin) {
        pluginsCompletados.add(plugin)
    }
}
