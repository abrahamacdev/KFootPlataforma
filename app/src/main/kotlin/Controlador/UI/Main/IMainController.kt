package Controlador.UI.Main

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

interface IMainController{

    /**
     * Retornamos la ruta absoluta de la imagen
     * que se usará como icono para la opción "cuenta"
     * del menú principal
     *
     * @return String: Ruta a utilizar
     */
    fun obtenerRutaImagenCuenta(): String

    /**
     * Establecemos los listeners de los botones del
     * menú principal
     *
     * @return EventHandler<MouseEvent>: Listener asociado al botón
     */
    fun getMenuClickListener(): EventHandler<MouseEvent>
}