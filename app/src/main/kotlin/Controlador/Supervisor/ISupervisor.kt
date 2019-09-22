package Controlador.Supervisor

import Datos.Modelo.Plugin.Plugin
import lib.Plugin.IPlugin
import java.util.concurrent.atomic.AtomicLong

interface ISupervisor {

    /**
     * Añadimos el plugin pasado por parámetro a la
     * lista de plugins que se ejecutarán
     *
     * @param plugin: PluginView que añadiremos a la lista
     *
     * @return Boolean: Si ha podido añadirse el plugin a la lista
     */
    fun anadirPlugin(plugin: Plugin): Boolean

    /**
     * Añadimos la lista de plugins pasados
     * por parámetros lista de plugins que se ejecutarán
     *
     * @param listaPlugins: LIsta de plugins que se añadirán a la lista
     *
     * @return ArrayList<PluginView>: Lista con los plugins que no pueden añadirse a la lista
     */
    fun anadirListaPlugins(listaPlugins: List<Plugin>): ArrayList<Plugin>

    /**
     * Eliminamos de la lista al plugin
     * que tenga el mismo identificador
     * que el pasado por parámetro
     *
     * @param plugin: PluginView que se buscara en el array
     *
     * @return Boolean: Si se ha podido eliminar el plugin de la lista
     */
    fun eliminarPlugin(plugin: Plugin): Boolean

    /**
     * Eliminamos de la lista el plugin que se
     * encuentra en la posición pasada por parámetro
     *
     * @param pos: Posición del plugin a eliminar
     *
     * @return Boolean: Si se ha eliminado
     */
    fun eliminarPlugin(pos: Int): Boolean

    /**
     * Obtenemos todos los plugins que están cargados
     * en el supervisor
     */
    fun obtenerPluginsCargados(): ArrayList<Plugin>


    /**
     *
     * Se ejecuta los plugins que se encuentren en la lista
     * en una coroutina separada
     */
    //fun ejecutarPlugins()

    /**
     * Ejecutamos el plugin que contenga el [id] recibido por parámetros
     *
     * @param id: Id del plugin a ejecutar
     * @param onResultadoInicioListener: Listener por el que transimitiremos el resultado de la ejecución del plugin
     */
    fun ejecutarPlugin(id: AtomicLong, onResultadoInicioListener: IPlugin.onResultadoAccionListener? = null)



    /**
     * Pausamos la ejecución de todos los plugins
     *
     * @param listener: Listener por el que transimitiremos el resultado del pausado de los plugins
     */
    fun pausarPlugins(listener: IPlugin.onResultadoAccionListener? = null)



    /**
     * Cancelamos la ejecución de todos los plugins
     *
     * @param listener: Listener por el que transimitiremos el resultado del cancelado de los plugins
     */
    fun cancelarPlugins(listener: IPlugin.onResultadoAccionListener? = null)



    /**
     * Interfaz que usaremos para saber que plugins
     * se han ejecutado correctamente y cuáles han sufrido
     * algún error
     */
    interface onPluginEjecutado {

        /**
         * Se llamará una vez que el plugin se haya
         * ejecutado correctamente
         *
         * @param plugin: PluginView que se ha ejecutado correctamente
         */
        fun onEjecutadoCorrectamente(plugin: Plugin)

        /**
         * Se llamará si el plugin no ha podido ejecutarse
         * correctamente por algún error
         */
        fun onErrorEnEjecucion(plugin: Plugin)
    }
}