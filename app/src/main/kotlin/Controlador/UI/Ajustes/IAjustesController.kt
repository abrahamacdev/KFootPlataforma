package Controlador.UI.Ajustes

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

interface IAjustesController{

    /**
     * Escuchamos los clicks que se produzcan en los botones
     * del layout
     */
    fun getBotonClickListener(): EventHandler<MouseEvent>
}