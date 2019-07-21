package Controlador.UI

import javafx.scene.layout.Pane

interface IController {

    /**
     * Será el método que llamemos para iniciar el
     * controlador de cada opción del menú
     *
     * @param fragmentoPrincipal: Fragmento sobre el que pondremos los layouts de cada opción del menú
     */
    fun iniciar(fragmentoPrincipal: Pane)

    /**
     * Servirá para cancelar en cualquier momento cualquier operación
     * que esté realizando
     */
    fun cancelar()

}