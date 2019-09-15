package Vista.Plugins

import Controlador.UI.Plugins.PluginsController
import Modelo.Plugin.EstadosPlugin
import Modelo.Plugin.Plugin
import Utiles.Colar
import Utiles.Colores
import Utiles.Utils
import Vista.View
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.animation.FadeTransition
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.shape.Ellipse
import javafx.scene.text.Text
import javafx.scene.web.WebView
import javafx.util.Duration
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PluginView: IPluginView, View() {

    companion object {
        // Layout que pondremos sobre el fragmento principal
        private val layoutPlugins: ScrollPane = FXMLLoader.load<ScrollPane>(javaClass.getResource("../../layouts/plugins.fxml"))

        // Layout de carga indefinido
        private val buscandoPlugins = FXMLLoader.load<Node>(javaClass.getResource("../../layouts/spinnerIndefinido.fxml"))

        // Layout con la animación de ir a tienda
        private val layoutIrATienda = FXMLLoader.load<AnchorPane>(javaClass.getResource("../../layouts/irATienda.fxml"))
        private var webViewAnimacionTienda: WebView? = null
        private var botonIrTienda: JFXButton? = null
        private var textoIrTienda: Text? = null

        // Dialog que muetsra el inicio de un plugin
        private var dialogInicioPlugin: JFXDialog? = null
    }

    // Caja a la que iremos añadiendo las nuevas filas de "cards views"
    private val vbox: VBox = (layoutPlugins.content as Pane).children.firstOrNull{ it.id.equals("vboxPlugin") } as VBox

    // Cada una de las filas en las que tenemos plugins
    private val filas: ArrayList<HBox> = ArrayList()

    // Controlador al que está ligado
    private lateinit var pluginsController: PluginsController

    // Factor que usaremos para aumentar la velocidad del scroll
    private val FACTOR_SCROLL = 3



    override fun iniciar() {
        super.iniciar()

        pluginsController = PluginsController(this)

        // Aumentamos la velocidad del scroll
        Utils.cambiarSensibilidadScroll(layoutPlugins, FACTOR_SCROLL)
    }

    override fun cancelar() {
        super.cancelar()
        pluginsController.cancelar()

        if (textoIrTienda != null && botonIrTienda != null){
            textoIrTienda!!.opacity = 0.0
            botonIrTienda!!.opacity = 0.0
            botonIrTienda!!.isFocusTraversable = false
        }
    }


    override fun mostrarCargaIndefinida() {
        super.renovarFragmentoPrincipal(buscandoPlugins)
    }

    override fun cargaIndefinidaMostrandose(): Boolean {
        return super.getFragmentoPrincipal().children.firstOrNull { it.id != null && it.id.equals("contenedorSpinner") } != null
    }

    override fun esconderCargaIndefinida() {
        val contenedorSpinner = super.getFragmentoPrincipal().children.firstOrNull { it.id != null && it.id.equals("contenedorSpinner") }

        Platform.runLater {
            getFragmentoPrincipal().children.remove(contenedorSpinner)
        }
    }


    override fun mostrarDialogoInicioPlugin() {

        // Cargamos el layout y lo guardamos para un futuro
        if (dialogInicioPlugin == null){

            // Obtenemos el layout que mostrará la animación
            val stackPane: StackPane = FXMLLoader.load(javaClass.getResource("../../layouts/dialogInicioPlugin.fxml"))
            val dialogLayout: JFXDialogLayout = stackPane.children.get(0) as JFXDialogLayout

            // Cargamos la animación al webview y le damos una forma circular
            val webView = dialogLayout.lookup("#webViewInicioPlugin") as WebView
            val ellipse = Ellipse(50.0,50.0,50.0,50.0)
            ellipse.centerX = 70.0
            ellipse.centerY = 80.0
            webView.clip = ellipse
            webView.engine.load(javaClass.getResource("../../animaciones/inicioPlugin/animacion.html").toString())

            // Añadimos los estilos para que el #stackPane ocupe toodo
            // el tamaño del fragmento principal
            AnchorPane.setRightAnchor(stackPane,0.0)
            AnchorPane.setLeftAnchor(stackPane,0.0)
            AnchorPane.setTopAnchor(stackPane,0.0)
            AnchorPane.setBottomAnchor(stackPane,0.0)
            stackPane.style = "-fx-background-color: transparent;\n"

            // Creamos el diálogo con el contendor principal (#stackPane) y el layout
            // que mostrará la animación
            dialogInicioPlugin = JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER, false)
            dialogInicioPlugin!!.styleClass += "scroll-ajustes"
            dialogInicioPlugin!!.style = "-fx-background-color: transparent;\n"
        }

        // Añadimos el contendor del dialogo al fragmento principal y lo mostramos
        getFragmentoPrincipal().children.add(dialogInicioPlugin!!.dialogContainer)
        dialogInicioPlugin!!.show()
    }

    override fun esconderDialogoInicioPlugin() {

        // Comprobamos que el diálogo esté iniciado
        if (dialogInicioPlugin != null){

            // Cuando cerremos el diálogo eliminaremos el contenedor de este
            // del fragmento principal
            dialogInicioPlugin!!.onDialogClosed = EventHandler {
                Platform.runLater {
                    getFragmentoPrincipal().children.remove(dialogInicioPlugin!!.dialogContainer)
                }
            }

            // Cerramos el dialogo
            dialogInicioPlugin!!.close()
        }
    }


    override fun cambiarLayoutEstadoPlugin(node: Node, estaInactivo: Boolean ) {

        // Buscamos el botón de inico de plugins
        val botonInicioPlugin = (node.lookup("#botonIniciarPlugin") as JFXButton)

        // Buscamos el botón de cancelar el plugin y lo escondemos
        val botonCancelarPlugin = (node.lookup("#botonCancelarPlugin") as JFXButton)

        // Buscamos el botón de inicio de plugin y lo escondemos
        val botonPausarPlugin = (node.lookup("#botonPausarPlugin") as JFXButton)

        // Mostraremos el botón "Iniciar"
        if (estaInactivo){
            botonInicioPlugin.isFocusTraversable = true
            botonInicioPlugin.isVisible = true

            // Escondemos los botones de pausar y cancelar
            botonCancelarPlugin.isFocusTraversable = false
            botonCancelarPlugin.isVisible = false
            botonPausarPlugin.isFocusTraversable = false
            botonPausarPlugin.isVisible = false
        }

        // Mostraremos los botones "Pausar" | "Cancelar"
        else {
            // Escondemos el de iniciar
            botonInicioPlugin.isFocusTraversable = false
            botonInicioPlugin.isVisible = false

            // Mostramos los botones de pausar y cancelar
            botonCancelarPlugin.isFocusTraversable = true
            botonCancelarPlugin.isVisible = true
            botonPausarPlugin.isFocusTraversable = false
            botonPausarPlugin.isVisible = false
        }

        // Seteamos los clicks listeners
        botonCancelarPlugin.onMouseClicked = pluginsController.getControlPluginClickListener()
        botonPausarPlugin.onMouseClicked = pluginsController.getControlPluginClickListener()
        botonInicioPlugin.onMouseClicked = pluginsController.getControlPluginClickListener()

    }



    override fun anadirPluginsUI(plugins: ArrayList<Plugin>) {

        // Eliminamos todas la filas existentes
        vbox.children.clear()

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

            // Escondemos la carga indefinida si estaba mostrandose
            if (cargaIndefinidaMostrandose()){
                esconderCargaIndefinida()
            }

            // Mostramos el fragmento con todos los plugins
            super.renovarFragmentoPrincipal(layoutPlugins)
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

            // Le añadimos el id que tiene el plugin
            base.id = plugin.ID.toString()

            // Le establecemos el gradiente de fondo
            base.style = establecerColorGradienteAleatorio()


            // El plugin está ejecutandose
            if (plugin.getEstadoActual() == EstadosPlugin.ACTIVO){

                // Cambiamos la vista
                cambiarLayoutEstadoPlugin(base,false)
            }


            // El plugin está inactivo
            else {

                // Cambiamos la vista
                cambiarLayoutEstadoPlugin(base,true)
            }
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
        val fade = FadeTransition(Duration.millis(750.0), card)
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.delay = Duration.millis((delayBase * 100).toDouble())
        fade.play()
    }

    /**
     * Mostramos la animación de la tienda y un mensaje
     * que indique al usuario la inexistencia de plugins
     * en el directorio de plugins
     */
    fun mostrarAnimacionTienda(){

        // Cargamos el webview si aún no lo hicimos
        if (webViewAnimacionTienda == null){
            webViewAnimacionTienda = layoutIrATienda.lookup("#webViewTienda") as WebView
            webViewAnimacionTienda!!.engine.load(javaClass.getResource("../../animaciones/irATienda/animacion.html").toString())
        }

        // Cacheamos el boton y el texto de ir a tienda
        if (textoIrTienda == null || botonIrTienda == null){
            val pane = (layoutIrATienda.lookup("#stackPaneIrATienda") as Pane)
            textoIrTienda = pane.lookup("#textoIrATienda") as Text
            botonIrTienda = pane.lookup("#botonIrATienda") as JFXButton
        }

        // Establecemos los márgenes del layout
        AnchorPane.setRightAnchor(layoutIrATienda,0.0)
        AnchorPane.setLeftAnchor(layoutIrATienda,0.0)
        AnchorPane.setTopAnchor(layoutIrATienda,0.0)
        AnchorPane.setBottomAnchor(layoutIrATienda,0.0)

        // Añadimos las animaciones al texto de ir a tienda
        val animacionTexto = FadeTransition(Duration.millis(500.0), textoIrTienda)
        animacionTexto.setFromValue(0.0);
        animacionTexto.setToValue(1.0);
        animacionTexto.delay = Duration.millis(1000.0)
        animacionTexto.play()

        // Añadimos las animaciones al botón de ir a tienda
        val animacionBoton = FadeTransition(Duration.millis(500.0), botonIrTienda)
        animacionBoton.setFromValue(0.0);
        animacionBoton.setToValue(1.0);
        animacionBoton.delay = Duration.millis(2000.0)
        animacionBoton.setOnFinished { botonIrTienda!!.isFocusTraversable = true }
        animacionBoton.play()


        // Comprobamos que no se haya pulsado otra opción del menú principal
        if (!ejecCancelado){
            esconderCargaIndefinida()
            renovarFragmentoPrincipal(layoutIrATienda)
        }
    }
}