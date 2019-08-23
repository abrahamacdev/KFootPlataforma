package Vista.Plugins

import Controlador.UI.Plugins.PluginsController
import Modelo.Plugin.Plugin
import Utiles.Colar
import Utiles.Colores
import Utiles.Utils
import Vista.View
import com.jfoenix.controls.JFXButton
import javafx.animation.FadeTransition
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.*

class PluginView: IPluginView, View() {

    // Layout que pondremos sobre el fragmento principal
    private val layoutPlugins: ScrollPane = FXMLLoader.load<ScrollPane>(javaClass.getResource("../../layouts/plugins.fxml"))

    // Layout de carga indefinido
    private val buscandoPlugins = FXMLLoader.load<Node>(javaClass.getResource("../../layouts/spinnerIndefinido.fxml"))

    // Caja a la que iremos añadiendo las nuevas filas de "cards views"
    private val vbox: VBox = (layoutPlugins.content as Pane).children.firstOrNull{ it.id.equals("vboxPlugin") } as VBox

    // Controlador al que está ligado
    private lateinit var pluginsController: PluginsController

    // Factor que usaremos para aumentar la velocidad del scroll
    private val FACTOR_SCROLL = 3



    override fun iniciar(fragmento: Pane) {
        super.iniciar(fragmento)

        pluginsController = PluginsController(this)

        // Aumentamos la velocidad del scroll
        Utils.cambiarSensibilidadScroll(layoutPlugins, FACTOR_SCROLL)
    }

    override fun mostrarCargaIndefinida() {
        super.renovarContenidoFragmento(buscandoPlugins)
    }

    override fun cargaIndefinidaMostrandose(): Boolean {
        return super.getFragmentoPrincipal().children.firstOrNull { it.id != null && it.id.equals("contenedorSpinner") } != null
    }

    override fun esconderCargaIndefinida() {
        val contenedorSpinner = super.getFragmentoPrincipal().children.firstOrNull { it.id != null && it.id.equals("contenedorSpinner") }

        if (contenedorSpinner != null){
            super.limpiarFragmento()
            /*Platform.runLater {
                super.getFragmentoPrincipal().children.remove(contenedorSpinner)
            }*/
        }
    }

    override fun anadirPluginsUI(plugins: ArrayList<Plugin>) {

        val cantidadPlugins = plugins.size

        if (cantidadPlugins > 0) {

            val numFilas = if (cantidadPlugins == 1) 1 else Math.ceil(plugins.size.toDouble() / 2.0).toInt()

            val filas = ArrayList<HBox>(numFilas)

            for (i in 0 until numFilas) {

                // Fila que añadiremos al layout
                val fila = HBox()

                // Posición del elemento que estará a la izquierda
                val pos = i * 2

                /**
                 * Parseamos los datos del plugin a un "cardView"
                 * @see resources/layouts/detallePlugin.fxml")
                 */
                val cardIzquierda = parsearCardPlugin(plugins.getOrNull(pos))
                val cardDerecha = parsearCardPlugin(plugins.getOrNull(pos + 1))

                // Comprobamos que el cardDerecha tenga la misma anchura que el de la izquierda
                if (cardDerecha is Pane) {
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
                anadirAnimacionEntrada(cardIzquierda, pos)
                anadirAnimacionEntrada(cardDerecha, pos)

                // Añadimos los cards a la fila
                fila.children.addAll(cardIzquierda, cardDerecha)

                // Añadimos la fila a la lista
                filas.add(fila)

            }

            // Añadimos todas las filas al layout
            vbox.children.addAll(*filas.toTypedArray())

            if (cargaIndefinidaMostrandose()){
                esconderCargaIndefinida()
            }

            // Mostramos el fragmento con todos los plugins
            super.renovarContenidoFragmento(layoutPlugins)
        }
    }


    /**
     * Parseamos los datos del plugin a la vista
     *
     * @param plugin: PluginView con los datos a parsear
     *
     * @return Node: Nodo que se añadirá a la vista
     */
    private fun parsearCardPlugin(plugin: Plugin?): Node{

        var base: Node? = null

        if (plugin == null){
            base = Pane()
            base.style = "-fx-background-color: transparent;"
        }

        else {

            // Obtenemos el layout base
            base = FXMLLoader.load<AnchorPane>(javaClass.getResource("../../layouts/detallePlugin.fxml"))

            // Establecemos el nombre y la version del plugin
            (base.lookup("#textoNombrePlugin") as Text).text = plugin.getMetaDatos()!!.nombrePlugin
            (base.lookup("#textoVersionPlugin") as Text).text += plugin.getMetaDatos()!!.version.toString()

            // Establecemos el manejador del click para el inicio del plugin
            (base.lookup("#botonIniciarPlugin") as JFXButton).onMouseClicked = this.pluginsController.getInicioPluginClickListener()

            // Le añadimos el id que tiene el plugin
            base.id = plugin.ID.toString()

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

        val colores: Pair<Colar, Colar> = Colores.coloresRandom()

        // Cambia el fondo a un gradiente
        val f = Formatter(StringBuffer("#"))
        f.format("%02X", colores.first.r)
        f.format("%02X", colores.first.g)
        f.format("%02X", colores.first.b)
        f.toString()

        val f2 = Formatter(StringBuffer("#"))
        f2.format("%02X", colores.second.r)
        f2.format("%02X", colores.second.g)
        f2.format("%02X", colores.second.b)
        f2.toString()

        return "-fx-background-color: linear-gradient(to bottom right, $f, $f2);\n"
    }

    /**
     * Establecemos las animaciones de entrada que tendrán
     * los cardViews
     *
     * @param card: Tarjeta a la que se le aplicará la animación
     * @param delayBase: Número de la posición de la tarjeta
     */
    fun anadirAnimacionEntrada(card: Node, delayBase: Int) {
        val fade = FadeTransition(Duration.millis(1000.0), card)
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.delay = Duration.millis((delayBase * 100).toDouble())
        fade.play()
    }
}