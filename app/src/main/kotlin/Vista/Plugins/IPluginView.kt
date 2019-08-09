package Vista.Plugins

import Modelo.Plugin.Plugin
import Vista.IView
import java.util.ArrayList

interface IPluginView: IView {

    /**
     * Mostramos un spinner de carga indefinido
     */
    fun mostrarCargaIndefinida()

    /**
     * Escondemos el spinner de carga
     */
    fun esconderCargaIndefinida()

    /**
     * Comprobamos si ahora se está
     * mostrando la carga indefinida
     */
    fun cargaIndefinidaMostrandose(): Boolean

    /**
     * Mostramos todos los plugins recibidos por parámetro
     *
     * @param plugins: Lista de plugins a añadir
     */
    fun anadirPluginsUI(plugins: ArrayList<Plugin>)
}