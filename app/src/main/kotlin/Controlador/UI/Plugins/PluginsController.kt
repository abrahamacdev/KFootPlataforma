package Controlador.UI.Plugins

import Controlador.Office.Office
import Controlador.UI.IController
import Modelo.Plugin.Plugin
import Utiles.Utils
import Utiles.esDiferenteDe
import Vista.IMain
import com.jfoenix.controls.JFXSpinner
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList

class PluginsController: IController {

    // Layout que pondremos sobre el fragmento principal
    private lateinit var layoutPlugins: ScrollPane

    // Fragmento principal sobre el que pondremos el layout
    private lateinit var fragmentoPrincipal: Pane

    // Spinner de carga
    private lateinit var spinnerIndefinido: JFXSpinner

    // Caja a la que iremos añadiendo las nuevas filas de "cards views"
    private lateinit var vbox: VBox
    private var ultFila: HBox? = null

    private val office: Office = Office()


    override fun iniciar(fragmentoPrincipal: Pane) {

        // Fragmento principal sobre el que colocaremos el layout
        this.fragmentoPrincipal = fragmentoPrincipal

        // Layout que colocaremos sobre el fragmento principal
        layoutPlugins = FXMLLoader.load<ScrollPane>(javaClass.getResource("../../../layouts/plugins.fxml"))

        // Mientras buscamos plugins, mostramos el spinner
        mostrarCargadoIndefinido()

        // Si existen plugins comenzaremos a cargarlos y mostrarlos
        if (office.existenPlugins()){

            // Recorremos cada uno de los plugins cargados
            obtenerTodosPlugins().subscribe {


            }
        }

        // Guardamos la caja a la que iremos añadiendo las nuevas filas
        val anchorPane = layoutPlugins.content as AnchorPane
        vbox = anchorPane.children.get(0) as VBox

        // Eliminamos del fragmento principal los layouts
        // que pueda haber
        //fragmentoPrincipal.children.removeAll()
        //fragmentoPrincipal.children.add(layoutFragmento)
    }

    private fun mostrarCargadoIndefinido() {

        // Cargamos el spinner en el #fragmentoPrincipal
        val buscandoPlugins = FXMLLoader.load<Node>(javaClass.getResource("../../../layouts/spinnerIndefinido.fxml"))
        fragmentoPrincipal.children.removeAll()
        fragmentoPrincipal.children.add(buscandoPlugins)

    }

    override fun cancelar() {

    }

    /**
     * Obtenemos un observable con todos los plugins
     * válidos que se encuentren en el directorio de plugins
     *
     * @return Observable<Plugin>: Observable con los plugins
     */
    private fun obtenerTodosPlugins(): Observable<Plugin>{

        val listaPlugins = ArrayList<Plugin>()

        office.cargarPlugins(object : IMain.setOnPluginCargadoListener{
            override fun onPluginCargado(plugin: Plugin) {
                listaPlugins.add(plugin)
            }
            override fun onCompletado() {}
        })

        return listaPlugins.toObservable()
    }

    /**
     * Añadimos el plugin recibido por parámetros al [vbox]
     *
     * @param plugin: Plugin a añadir
     */
    private fun anadirPluginUI(plugin: Plugin){

        if (ultFila != null){}

        else {


        }
    }

    // TODO Eliminar
    private fun establecerColorGradienteAleatorio(){

        val scrollPane = fragmentoPrincipal.lookup("#scrollFragmento") as ScrollPane
        val anchor = scrollPane.content as AnchorPane
        val vbox = anchor.children.get(0) as VBox
        var ultimosColores: Pair<Color,Color> = Utils.coloresRandomPaleta()
        vbox.children.forEach { hbox -> (hbox as HBox).children.forEach {

            var colores: Pair<Color,Color>
            do {
                colores = Utils.coloresRandomPaleta()
            }while (!ultimosColores.first.esDiferenteDe(colores.first) && !ultimosColores.second.esDiferenteDe(colores.second))


            // Cambia el fondo a un gradiente
            val f = Formatter(StringBuffer("#"))
            f.format("%02X", colores.first.red)
            f.format("%02X", colores.first.green)
            f.format("%02X", colores.first.blue)
            f.toString()
            val f2 = Formatter(StringBuffer("#"))
            f2.format("%02X", colores.second.red)
            f2.format("%02X", colores.second.green)
            f2.format("%02X", colores.second.blue)
            f2.toString()
            it.style = "-fx-background-color: linear-gradient(to bottom right, $f, $f2)"

        } }
    }
}