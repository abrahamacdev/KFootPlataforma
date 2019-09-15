package Controlador.UI.Plugins

import Controlador.UI.Controller
import Controlador.Office.Office
import KFoot.IMPORTANCIA
import Vista.Plugins.PluginView
import com.jfoenix.controls.JFXButton
import io.reactivex.Observable
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.*
import lib.Plugin.IPlugin
import java.lang.Exception
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext
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
                pluginView.anadirPluginsUI(office.cargarPlugins())
            }

            // Esperamos un poco y mostramos la animación de la tienda
            else {

                // Si ha estado más de 1 segundo buscando plugins mostramos directamente la animación
                if (tiempo > 1000){

                    // Ejecutamos en el hilo principal
                    Platform.runLater{pluginView.mostrarAnimacionTienda()}
                }

                // Sino esperamos un poco para no saturar el hilo principal
                else {
                    // Esperamos 1 segundo
                    Observable.interval(1,TimeUnit.SECONDS).take(1).subscribe {

                        // Ejecutamos en el hilo principal
                        Platform.runLater{pluginView.mostrarAnimacionTienda()}
                    }
                }
            }
        }
    }

    override fun getControlPluginClickListener(): EventHandler<MouseEvent> {

        return object: EventHandler<MouseEvent> {
            override fun handle(evento: MouseEvent?) {

                val source = evento!!.source as JFXButton
                val parent = source.parent

                when {

                    // Iniciaremos el plugin clickeado
                    source.id.equals("botonIniciarPlugin") -> {
                        iniciarPlugin(parent)
                    }

                    // Pausaremos el plugin clickado
                    source.id.equals("botonPausarPlugin") -> {
                        pausarPlugin(parent)
                    }

                    // Cancelaremos el plugin clickado
                    source.id.equals("botonCancelarPlugin") -> {
                        cancelarPlugin(parent)
                    }
                }
            }
        }
    }



    override fun iniciarPlugin(node: Node){

        pluginView.mostrarDialogoInicioPlugin()

        // Ejecutamos el plugin
        office.ejecutarPlugin(AtomicLong(node.id.toLong()), object : IPlugin.onResultadoAccionListener {
            override fun onCompletado() {
                pluginView.esconderDialogoInicioPlugin()
            }

            override fun onError(e: Exception) {
                KFoot.Logger.getLogger().debug(KFoot.DEBUG.DEBUG_SIMPLE,e.localizedMessage, IMPORTANCIA.ALTA)
            }
        })
    }

    override fun pausarPlugin(node: Node) {

    }

    override fun cancelarPlugin(node: Node) {

    }
}