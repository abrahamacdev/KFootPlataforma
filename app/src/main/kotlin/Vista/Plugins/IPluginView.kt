package Vista.Plugins

import Modelo.Plugin.Plugin
import Vista.View
import javafx.scene.Node
import java.util.ArrayList

interface IPluginView {

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
     * Mostramos un diálogo que advierte del inicio
     * de la ejecución de un plugin
     */
    fun mostrarDialogoInicioPlugin()

    /**
     * Escondemos el diálogo de inicio de ejecución
     * de un plugin
     */
    fun esconderDialogoInicioPlugin()



    /**
     * Mostramos todos los plugins recibidos por parámetro
     *
     * @param plugins: Lista de plugins a añadir
     */
    fun anadirPluginsUI(plugins: ArrayList<Plugin>)



    /**
     * Cambiamos la vista del plugin según los botones de control que queramos
     * mostrar (iniciar ó pausar | cancelar)
     *
     * @param node: Nodo que modificar
     * @param estaInactivo: Nos indica si el estado actual del plugin es inactivo
     */
    fun cambiarLayoutEstadoPlugin(node: Node, estaInactivo: Boolean)
}