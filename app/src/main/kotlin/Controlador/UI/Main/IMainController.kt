package Controlador.UI.Main

import javafx.event.Event
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
     * menú principal, a excepción del botón de apagar
     *
     * @return EventHandler<MouseEvent>: Listener asociado a los botones
     */
    fun getItemMenuClickListener(): EventHandler<MouseEvent>

    /**
     * Establecemos el listener del botón de apagado
     * del menú principal
     *
     * @return EventHandler<Event>: Listener asociado a los botones de cierre de la aplicación
     */
    fun getCerrarClickListener(): EventHandler<Event>
}