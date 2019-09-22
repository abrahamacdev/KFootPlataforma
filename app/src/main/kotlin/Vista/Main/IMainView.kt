package Vista.Main

import javafx.scene.control.Button
import javafx.stage.Stage

interface IMainView {

    /**
     * Marcamos como pulsado el [boton] del menú recibido por parámetros
     */
    fun botonMenuPulsado(boton: Button)
}