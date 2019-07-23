package Controlador.UI.Plugins

import Controlador.Office.Office
import Controlador.UI.IController
import Modelo.Plugin.Plugin
import Utiles.POSICION
import Utiles.Utils
import Vista.IMain
import com.jfoenix.controls.JFXSpinner
import javafx.animation.*
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.util.Duration
import kotlinx.coroutines.*
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class PluginsController: IController, CoroutineScope {

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

    // Contexto en el que se ejecutarán las tareas
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job



    override fun iniciar(fragmentoPrincipal: Pane) {

        // Fragmento principal sobre el que colocaremos el layout
        this.fragmentoPrincipal = fragmentoPrincipal

        // Layout que colocaremos sobre el fragmento principal
        layoutPlugins = FXMLLoader.load<ScrollPane>(javaClass.getResource("../../../layouts/plugins.fxml"))

        // Aumentamos la velocidad del scroll
        cambiarSensibilidadScroll()

        // Obtenemos el VBox en el que introduciremos las cardviews con los detalles de los plugins
        vbox = (layoutPlugins.content as Pane).children.firstOrNull{ it.id.equals("vboxPlugin") } as VBox

        // Mientras buscamos plugins, mostramos el spinner
        mostrarCargadoIndefinido()

        launch {

            // Si existen plugins comenzaremos a cargarlos y mostrarlos
            if (office.existenPlugins()){

                // Añadimos todos los plugins al layout
                anadirPluginsUI(obtenerTodosPlugins())
            }
        }
    }

    /**
     * Dejamos el contenedor principal del fragmento
     * sin ningún layout
     */
    private fun limpiarFragmento(){
        Platform.runLater {
            this.fragmentoPrincipal.children.removeAll()
        }
    }

    private fun mostrarCargadoIndefinido() {

        // Cargamos el spinner en el #fragmentoPrincipal
        val buscandoPlugins = FXMLLoader.load<Node>(javaClass.getResource("../../../layouts/spinnerIndefinido.fxml"))

        Platform.runLater {
            fragmentoPrincipal.children.removeAll()
            fragmentoPrincipal.children.add(buscandoPlugins)
        }
    }

    private fun esconderCargadoIndefinido(){

        val contenedorSpinner = fragmentoPrincipal.children.firstOrNull() { it.id != null && it.id.equals("contenedorSpinner") }

        if (contenedorSpinner != null){
            Platform.runLater {
                fragmentoPrincipal.children.remove(contenedorSpinner)
            }
        }
    }

    override fun cancelar() {
        this.cancel()
    }

    /**
     * Obtenemos una lista con todos los plugins
     * válidos que se encuentren en el directorio de plugins
     *
     * @return ArrayList<Plugin>: Lista con los plugins
     */
    private fun obtenerTodosPlugins(): ArrayList<Plugin>{

        val listaPlugins = ArrayList<Plugin>()

        office.cargarPlugins(object : IMain.setOnPluginCargadoListener{
            override fun onPluginCargado(plugin: Plugin) {
                listaPlugins.add(plugin)
            }
            override fun onCompletado() {}
        })

        return listaPlugins
    }

    /**
     * Mostramos todos los plugins recibidos por parámetro
     *
     * @param plugins: Lista de plugins a añadir
     */
    private fun anadirPluginsUI(plugins: ArrayList<Plugin>){

        val cantidadPlugins = plugins.size

        if (cantidadPlugins > 0){

            val numFilas = if (cantidadPlugins == 1 ) 1 else Math.ceil(plugins.size.toDouble() / 2.0).toInt()

            val filas = ArrayList<HBox>(numFilas)

            for (i in 0 until numFilas){

                // Fila que añadiremos al layout
                val fila = HBox()

                // Posición del elemento que estará a la izquierda
                val pos = i * 2

                /**
                 * Parseamos los datos del plugin a un "cardView"
                 * @see resources/layouts/detallePlugin.fxml")
                 */
                val cardIzquierda = parsearCardPlugin(plugins.getOrNull(pos), POSICION.IZQUIERDA)
                val cardDerecha = parsearCardPlugin(plugins.getOrNull(pos+1), POSICION.DERECHA)

                // Comprobamos que el cardDerecha tenga la misma anchura que el de la izquierda
                if (cardDerecha is Pane){
                    cardDerecha.prefWidth = (cardIzquierda as AnchorPane).prefWidth
                    cardDerecha.prefHeight = cardIzquierda.prefHeight
                }

                // Establecemos la opacidad inicial a 0
                cardDerecha.opacity = 0.0
                cardIzquierda.opacity = 0.0

                // Hacemos que se expandan lo máximo posible horizontalmente
                HBox.setHgrow(cardIzquierda, Priority.ALWAYS)
                HBox.setHgrow(cardDerecha, Priority.ALWAYS)

                // Añadimos las animaciones de entrada
                anadirAnimacionEntrada(cardIzquierda,pos)
                anadirAnimacionEntrada(cardDerecha,pos)

                // Añadimos los cards a la fila
                fila.children.addAll(cardIzquierda,cardDerecha)

                // Añadimos la fila a la lista
                filas.add(fila)

            }

            Platform.runLater {

                // Añadimos todas las filas al layout
                vbox.children.addAll(*filas.toTypedArray())

                // Escondemos el spinner
                esconderCargadoIndefinido()

                // Mostramos el fragmento con todos los plugins
                fragmentoPrincipal.children.add(layoutPlugins)
            }
        }
    }

    // TODO Falta parsear los datos
    /**
     * Parseamos los datos del plugin a la vista
     *
     * @param plugin: Plugin con los datos a parsear
     * @param posicion: Posición que tendrá la cardview del plugin
     *
     * @return Node: Nodo que se añadirá a la vista
     */
    private fun parsearCardPlugin(plugin: Plugin?, posicion: POSICION): Node{

        var base: Node? = null

        if (plugin == null){
            base = Pane()
            base.style = "-fx-background-color: transparent;"
        }

        else {

            // Obtenemos el layout base
            base = FXMLLoader.load<AnchorPane>(javaClass.getResource("../../../layouts/detallePlugin.fxml"))

            // Establecemos el nombre y la version del plugin
            (base.lookup("#textoNombrePlugin") as Text).text = plugin.getMetaDatos()!!.nombrePlugin
            (base.lookup("#textoVersionPlugin") as Text).text += plugin.getMetaDatos()!!.version.toString()


            // Le añadimos los estilos correspondientes
            base.style = establecerColorGradienteAleatorio()
        }

        // Añadimos elevacion
        base!!.style += "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);\n"

        return base!!
    }

    /**
     * Obtenemos un estilo para establecer a un nodo
     * con un fondo con gradiente basado en la paleta de colores
     *
     * @return String: Estilo a aplicar al nodo
     */
    private fun establecerColorGradienteAleatorio(): String{

        val colores: Pair<Color,Color> = Utils.coloresRandomPaleta()

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
        return "-fx-background-color: linear-gradient(to bottom right, $f, $f2);\n"
    }

    /**
     * Cambiamos la velocidad a la que se hace scroll
     * en el [layoutPlugins]
     */
    private fun cambiarSensibilidadScroll(){
        layoutPlugins.content.setOnScroll {
            val deltaY = it.getDeltaY() * 5 // *6 to make the scrolling a bit faster
            val width = layoutPlugins.content.getBoundsInLocal().getWidth()
            val vvalue = layoutPlugins.vvalue
            layoutPlugins.setVvalue(vvalue + -deltaY / width) }
    }

    /**
     * Establecemos las animaciones de entrada que tendrán
     * los cardViews
     *
     * @param card: Tarjeta a la que se le aplicará la animación
     * @param delayBase: Número de la posición de la tarjeta
     */
    private fun anadirAnimacionEntrada(card: Node, delayBase: Int) {
        val fade = FadeTransition(Duration.millis(1000.0), card)
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.delay = Duration.millis((delayBase * 100 + 500).toDouble())
        fade.play()
    }
}