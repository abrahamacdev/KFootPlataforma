package Controlador.Office

import Controlador.Supervisor.Supervisor
import IMain

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
    fun cargarPlugins(onPluginCargadoListener: IMain.setOnPluginCargadoListener)

    /**
     * Llamamos al supervisor para que ejecute todos
     * los plugins que se hallan cargado hasta el momendo
     */
    fun ejecutarPlugins(): Supervisor

    /**
     * Cargamos los plugins validos en memoria
     * para ejecutarlos
     *
     * @param pluginCargadoSub: Sujeto por el que transmitiremos el plugin recien cargado
     */
    /*fun cargarPlugins(pluginCargadoSub: PublishSubject<Plugin>)*/
}