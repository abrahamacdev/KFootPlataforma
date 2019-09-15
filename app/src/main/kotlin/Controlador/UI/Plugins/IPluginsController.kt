package Controlador.UI.Plugins

import Modelo.Plugin.Plugin
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent

interface IPluginsController {

    /**
     * Retornamos el listener que se encargará de manejar
     * los botones de control de estado de los plugins
     *
     * @return EventHandler<MouseEvent>: Manejador del click
     */
    fun getControlPluginClickListener(): EventHandler<MouseEvent>


    /**
     * Iniciamos el plugin del nodo recibido por parámetros
     *
     * @param nodo: Nodo con el ID del plugin a iniciar
     */
    fun iniciarPlugin(node: Node)

    /**
     * Pausamos el plugin del nodo recibido por parámetros
     *
     * @param nodo: Nodo con el ID del plugin a pausar
     */
    fun pausarPlugin(node: Node)

    /**
     * Cancelamos el plugin del nodo recibido por parámetros
     *
     * @param nodo: Nodo con el ID del plugin a cancelar
     */
    fun cancelarPlugin(node: Node)
}