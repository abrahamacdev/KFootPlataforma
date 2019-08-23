package Controlador.UI.Plugins

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

interface IPluginsController {

    /**
     * Retornamos el listener que se encargará de manejar
     * el click del botón de inicio de plugins
     *
     * @return EventHandler<MouseEvent>: Manejador del click
     */
    fun getInicioPluginClickListener(): EventHandler<MouseEvent>
}