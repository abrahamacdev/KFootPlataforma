package Vista.Main

import Vista.IView
import javafx.scene.control.Button

interface IMainView: IView {

    /**
     * Marcamos como pulsado el [boton] recibido por parámetros
     */
    fun botonPulsado(boton: Button)
}