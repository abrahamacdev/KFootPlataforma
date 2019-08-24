package Controlador.UI.Plugins

import Controlador.UI.Controller
import Controlador.Office.IOffice
import Controlador.Office.Office
import Modelo.Plugin.Plugin
import Vista.Plugins.PluginView
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.controls.events.JFXDialogEvent
import io.reactivex.Observable
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.event.EventDispatcher
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.Circle
import javafx.scene.shape.Ellipse
import javafx.scene.shape.Rectangle
import javafx.scene.web.WebView
import kotlinx.coroutines.*
import lib.Plugin.IPlugin
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class PluginsController(private val pluginView: PluginView): IPluginsController, CoroutineScope, Controller() {

    private val office: Office = Office()

    // Contexto en el que se ejecutarán las tareas
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job


    init {
        preCargar()
    }



    override fun preCargar() {

        // Mostramos la carga indefinida, se ocultará una vez que
        // vayamos a mostrar los plugins disponibles
        pluginView.mostrarCargaIndefinida()

        launch {

            // Si existen plugins comenzaremos a cargarlos y mostrarlos
            var existenPlugins: Boolean? = null
            val tiempo = measureTimeMillis {
                existenPlugins = office.existenPlugins()
            }

            // Si existen plugins los mostramos
            if (existenPlugins!!){

                // Añadimos todos los plugins al layout
                pluginView.anadirPluginsUI(obtenerTodosPlugins())
            }

            // Esperamos un poco y mostramos la animación de la tienda
            else {

                val block = {
                    val anchorPane: AnchorPane = FXMLLoader.load(javaClass.getResource("../../layouts/irATienda.fxml"))

                    val webView = anchorPane.lookup("#webViewTienda") as WebView
                    webView.engine.load(javaClass.getResource("../../animaciones/irATienda/animacion.html").toString())

                    AnchorPane.setRightAnchor(anchorPane,0.0)
                    AnchorPane.setLeftAnchor(anchorPane,0.0)
                    AnchorPane.setTopAnchor(anchorPane,0.0)
                    AnchorPane.setBottomAnchor(anchorPane,0.0)

                    // Comprobamos que no se haya pulsado otra opción del menú principal
                    if (!ejecCancelado){
                        pluginView.esconderCargaIndefinida()
                        pluginView.renovarContenidoFragmento(anchorPane)
                    }
                }


                // Si ha estado más de 2 segundos buscando plugins mostramos directamente la animación
                if (tiempo > 2000){

                    // Ejecutamos en el hilo principal
                    Platform.runLater(block)
                }

                // Sino esperamos un poco para no saturar el hilo principal
                else {
                    // Esperamos 2 segundos
                    Observable.interval(2,TimeUnit.SECONDS).take(1).subscribe {

                        // Ejecutamos en el hilo principal
                        Platform.runLater(block)
                    }
                }
            }
        }
    }

    /**
     * Obtenemos una lista con todos los plugins
     * válidos que se encuentren en el directorio de plugins
     *
     * @return ArrayList<PluginView>: Lista con los plugins
     */
    private fun obtenerTodosPlugins(): ArrayList<Plugin>{

        val listaPlugins = ArrayList<Plugin>()

        office.cargarPlugins(object : IOffice.onPluginCargadoListener{
            override fun onPluginCargado(plugin: Plugin) {
                listaPlugins.add(plugin)
            }
            override fun onCompletado() {}
        })

        return listaPlugins
    }

    override fun getInicioPluginClickListener(): EventHandler<MouseEvent> {

        return object: EventHandler<MouseEvent> {
            override fun handle(evento: MouseEvent?) {
                cargarPlugin((evento!!.source as Node).parent)
            }
        }
    }

    /**
     * Cargamos el plugin clickeado por el usuario
     * @param nodo: Nodo que ha sido clickeado
     */
    private fun cargarPlugin(nodo: Node){

        // Obtenemos el layout que mostrará la animación
        val stackPane: StackPane = FXMLLoader.load(javaClass.getResource("../../../layouts/dialogInicioPlugin.fxml"))
        val dialogLayout: JFXDialogLayout = stackPane.children.get(0) as JFXDialogLayout

        // Cargamos la animación al webview y le damos una forma circular
        val webView = dialogLayout.lookup("#webViewInicioPlugin") as WebView
        val ellipse = Ellipse(50.0,50.0,50.0,50.0)
        ellipse.centerX = 70.0
        ellipse.centerY = 80.0
        webView.clip = ellipse
        webView.engine.load(javaClass.getResource("../../../animaciones/inicioPlugin/animacion.html").toString())

        // Añadimos los estilos para que el #stackPane ocupe toodo
        // el tamaño del fragmento principal
        AnchorPane.setRightAnchor(stackPane,0.0)
        AnchorPane.setLeftAnchor(stackPane,0.0)
        AnchorPane.setTopAnchor(stackPane,0.0)
        AnchorPane.setBottomAnchor(stackPane,0.0)
        stackPane.styleClass += "scroll-ajustes"
        stackPane.style = "-fx-background-color: transparent;\n"

        // Creamos el diálogo con el contendor principal (#stackPane) y el layout
        // que mostrará la animación
        val dialog = JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER, false)
        dialog.styleClass += "scroll-ajustes"
        dialog.style = "-fx-background-color: transparent;\n"

        // Añadimos el #stackPane al fragmento principal y lo mostramos
        pluginView.getFragmentoPrincipal().children.add(stackPane)
        dialog.show()

        // Ejecutamos el plugin
        office.ejecutarPlugin(AtomicLong(nodo.id.toLong()), object : IPlugin.onResultadoAccionListener {
            override fun onCompletado() {
                dialog.onDialogClosed = EventHandler {
                    Platform.runLater {
                        pluginView.getFragmentoPrincipal().children.remove(stackPane)
                    }
                }
                dialog.close()
            }

            override fun onError(e: Exception) {
                // TODO Tratar mejor el error
                e.printStackTrace()
            }
        })
    }
}