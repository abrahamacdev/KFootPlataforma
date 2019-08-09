package Controlador.Office

import Controlador.Supervisor.Supervisor
import Modelo.Plugin.Plugin
import Vista.Main.IMainView

interface IOffice {

    /**
     * Comprueba si existen plugins en el directorio de plugins
     *
     * @return Boolean: Si existen plugins validos en la ruta de plugin
     */
    fun existenPlugins(): Boolean

    /**
     * Cargamos los plugins validos en memoria
     * para ejecutarlos
     *
     * @param onPluginCargadoListener: Callback por el que transmitiremos el plugin recien cargado
     */
    fun cargarPlugins(onPluginCargadoListener: onPluginCargadoListener? = null)

    /**
     * Cargamos los plugins validos en memoria
     * para ejecutarlos
     *
     * @param pluginCargadoSub: Sujeto por el que transmitiremos el plugin recien cargado
     */
    /*fun cargarPlugins(pluginCargadoSub: PublishSubject<PluginView>)*/

    /**
     * Llamamos al supervisor para que ejecute todos
     * los plugins que se hallan cargado hasta el momendo
     */
    fun ejecutarPlugins(): Supervisor

    interface onPluginCargadoListener {

        /**
         * Este metodo se llamará cada vez que un plugin
         * se haya cargado en memoria
         *
         * @param plugin: PluginView recién cargado
         */
        fun onPluginCargado(plugin: Plugin)

        /**
         * Este método se llamará una vez que se hayan cargado
         * todos los plugins válidos
         */
        fun onCompletado()

    }
}