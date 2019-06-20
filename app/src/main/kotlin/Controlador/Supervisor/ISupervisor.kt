package Controlador.Supervisor

import Modelo.Plugin.Plugin
interface ISupervisor {

    /**
     * Añadimos el plugin pasado por parámetro a la
     * lista de plugins que se ejecutarán
     *
     * @param plugin: Plugin que añadiremos a la lista
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
     * @return ArrayList<Plugin>: Lista con los plugins que no pueden añadirse a la lista
     */
    fun anadirListaPlugins(listaPlugins: List<Plugin>): ArrayList<Plugin>

    /**
     * Eliminamos de la lista al plugin
     * que tenga el mismo identificador
     * que el pasado por parámetro
     *
     * @param plugin: Plugin que se buscara en el array
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
     * Se ejecuta los plugins que se encuentren en la lista
     * en una coroutina separada
     */
    fun ejecutarPlugins()

}