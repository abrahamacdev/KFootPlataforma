package Vista.Main

import javafx.scene.control.Button
import javafx.stage.Stage

interface IMainView {

    /**
     *  Comienza la ejecución del programa
     *  @param etapa: Etapa que se utilizará para mostrar el contenido
     */
    fun start(etapa: Stage?)

    /**
     * Marcamos como pulsado el [boton] recibido por parámetros
     */
    fun botonPulsado(boton: Button)
}