package Controlador.UI.Plugins

import Controlador.Office.IOffice
import Controlador.Office.Office
import Modelo.Plugin.Plugin
import Utiles.Utils
import Vista.Main.IMainView
import Vista.Plugins.PluginView
import javafx.animation.*
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.util.Duration
import kotlinx.coroutines.*
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class PluginsController(private val pluginView: PluginView): IPluginsController, CoroutineScope {

    private val office: Office = Office()

    // Contexto en el que se ejecutarán las tareas
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job



    init {
        preCargar()
    }



    override fun preCargar() {

        pluginView.mostrarCargaIndefinida()

        launch {

            // Si existen plugins comenzaremos a cargarlos y mostrarlos
            if (office.existenPlugins()){

                // Añadimos todos los plugins al layout
                pluginView.anadirPluginsUI(obtenerTodosPlugins())
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
}