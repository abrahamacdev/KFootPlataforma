package Controlador.UI.Main

import Controlador.Supervisor.Supervisor
import Controlador.UI.Controller
import Vista.Ajustes.AjustesView
import Vista.View
import Vista.Main.MainView
import Vista.Plugins.PluginView
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.controls.events.JFXDialogEvent
import io.reactivex.Observable
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import lib.Plugin.IPlugin
import java.lang.Exception
import java.util.concurrent.TimeUnit


class MainController(private val mainView: MainView): IMainController, Controller() {

    // Ruta de las imagenes disponibles para la sección "Cuenta" del menú
    private val rutasImagenesMenuCuenta: Array<String> = arrayOf(javaClass.getResource("../../../imagenes/account1.png").toString(),javaClass.getResource("../../../imagenes/account2.png").toString())

    // Dialogo de cierre de la aplicación
    private val dialogCerrar: JFXDialog by lazy {
        // Obtenemos el layout que mostrará la animación
        val stackPane: StackPane = FXMLLoader.load(javaClass.getResource("../../../layouts/dialogCerrar.fxml"))
        val dialogLayout: JFXDialogLayout = stackPane.children.get(0) as JFXDialogLayout

        // Añadimos los estilos para que el #stackPane ocupe toodo
        // el tamaño del fragmento principal
        AnchorPane.setRightAnchor(stackPane,0.0)
        AnchorPane.setLeftAnchor(stackPane,0.0)
        AnchorPane.setTopAnchor(stackPane,0.0)
        AnchorPane.setBottomAnchor(stackPane,0.0)

        // Seteamos los listeners de los botones de la alerta
        (dialogLayout.children.get(3) as Pane).children
                .filter{ it is JFXButton }
                .forEach { it.onMouseClicked = getAlertaSalirClickListener() }

        // Creamos el diálogo con el contendor principal (#stackPane) y el layout
        // que mostrará la animación
        val dialog = JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER)
        dialog.styleClass += "bordes-fragmento"

        dialog
    }

    // Vista que esta ocupando actualmente el #fragmentoPrincipal
    private var vistaFragmento: View? = null

    init {
        preCargar()
    }

    override fun preCargar() {
        super.preCargar{}
    }

    override fun obtenerRutaImagenCuenta(): String {
        val posIcono = Math.round(Math.random()).toInt()
        return rutasImagenesMenuCuenta.get(posIcono)
    }

    /**
     * Cambiamos el contenido del fragmento por uno nuevo
     *
     * @param nuevaVistaFrag: Nuevo [IController] que modificará el layout del fragmento
     */
    fun cambiarLayoutFragmento(nuevaVistaFrag: View){

        // Comprobamos si el controlador del fragmento que hay actualmente establecido
        // es diferente al nuevo que estableceremos
        if (esDiferenteVistaFrag(nuevaVistaFrag)){

            // Comprobamos si hay alguna vista ocupando el fragmento para cancelarla
            if (vistaFragmento != null){
                vistaFragmento!!.cancelar()
            }

            // TODO: Hacer que cada vista se elimine del fragmento cuando llamemos a su método "cancelar"
            // Si hay hijos en la vista los eliminamos
            if (mainView.getFragmento().children.size > 0){
                mainView.limpiarFragmento()
            }

            // Iniciamos el nuevo controlador del fragmento
            vistaFragmento = nuevaVistaFrag
            vistaFragmento!!.iniciar()
        }
    }

    /**
     * Comprobamos si la nueva [vista] es la misma
     * que la [vistaFragmento]
     */
    private fun esDiferenteVistaFrag(vista: View): Boolean{
        return vista != this.vistaFragmento
    }



    override fun getItemMenuClickListener(): EventHandler<MouseEvent> {
        return object : javafx.event.EventHandler<MouseEvent> {
            override fun handle(event: MouseEvent?) {
                val botonPulsado = (event!!.source as Button)

                val noEstaPulsado = !botonPulsado.styleClass.contains("boton-menu-pulsado")

                when {

                    // Si no estaba pulsado le cambiamos el color de fondo
                    noEstaPulsado -> {

                        // A los demás botones les ponemos el color de fondo normal
                        mainView.botonMenuPulsado(botonPulsado)

                        // Cambiamos el layout del fragmento principal
                        var tempViewFrag: View? = null
                        when {

                            // Cargamos el fragmento de plugins
                            botonPulsado.id.equals("botonPlugins") -> {

                                // Creamos una instancia del nuevo controlador del fragmento
                                tempViewFrag = PluginView()
                            }

                            // Cargamos el fragmento de los ajustes de la aplicación
                            botonPulsado.id.equals("botonAjustes") -> {

                                // Creamos una instancia del nuevo controlador del fragmento
                                tempViewFrag = AjustesView()
                            }

                            // Cargamos el fragmento de plugins
                            botonPulsado.id.equals("botonCuenta") -> {

                                // Creamos una instancia del nuevo controlador del fragmento
                                //tempControlFrag = CuentaController()
                            }
                        }

                        if (tempViewFrag != null){

                            // Pre cargamos la vista
                            tempViewFrag!!.preCargar()
                            cambiarLayoutFragmento(tempViewFrag)
                        }
                    }
                }
            }
        }
    }

    override fun getCerrarClickListener(): EventHandler<Event> {
        return object : EventHandler<Event> {
            override fun handle(p0: Event) {

                // Añadimos el #stackPane al fragmento alertas y lo mostramos
                mainView.mostrarDialogPrimario(dialogCerrar!!)

                // Consumimos el evento para evitar que se cierre la aplicación
                p0.consume()
            }
        }
    }

    /**
     * Establecemos el listener de los botones del diálogo
     * que se mostrará cuando intentemos salir de la aplicación
     *
     * @return EventHandler<MouseEvent>: Listener asociado a los botones del diálogo de cierre de la aplicación
     */
    private fun getAlertaSalirClickListener(): EventHandler<MouseEvent>{
        return object : EventHandler<MouseEvent>{
            override fun handle(p0: MouseEvent?) {

                val nodo = p0!!.source as JFXButton

                when {

                    // TODO Cancelar los plugins que estén ejecutandose
                    nodo.id.equals("botonAdelante") -> {
                        cerrarAplicacion()
                    }

                    nodo.id.equals("botonCancelar") -> {
                        dialogCerrar!!.close()
                    }
                }
            }
        }
    }

    /**
     * Cancelamos los plugins que se estén ejecutando actualmente, si no lo hacen
     * antes de x tiempo, forzaremos el cierre
     */
    fun cerrarAplicacion(){

        // Cancelamos la ejecución de todos los plugins
        Supervisor.getInstance().cancelarPlugins(object : IPlugin.onResultadoAccionListener {
            override fun onCompletado() {
                Platform.exit()
            }

            override fun onError(e: Exception) {
                Platform.exit()
            }
        })

        // Si no, se cerrará la aplicación forzosamente
        Observable.interval(5,TimeUnit.SECONDS).take(1).subscribe { Platform.exit() }
    }
}