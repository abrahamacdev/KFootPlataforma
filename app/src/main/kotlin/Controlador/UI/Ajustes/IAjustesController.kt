package Controlador.UI.Ajustes

import Controlador.IController
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

interface IAjustesController: IController {

    /**
     * Escuchamos los clicks que se produzcan en los botones
     * del layout
     */
    fun getBotonClickListener(): EventHandler<MouseEvent>
}