package Vista.Main

import Controlador.UserSessionTracker
import Controlador.UI.Main.MainController
import Utiles.Constantes
import Vista.Plugins.PluginView
import Vista.View
import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyphLoader
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.WindowEvent

/**
 * Desde aquí lanzaremos el "Office" desde donde se cargaŕan
 * los diferente plugins para la obtención de los datos
 *
 * @author Abraham Álvarez
 * @since 1.0
 */
class MainView(): View(), IMainView {

    // Layout principal de la aplicación
    private val mainLayout: AnchorPane = FXMLLoader.load(javaClass.getResource("../../layouts/main.fxml"))
    private val fragmento: AnchorPane = mainLayout.lookup("#fragmentoPrincipal") as AnchorPane

    // Botones del menú
    private val botonesMenu: List<Button> = (mainLayout.lookup("#menu") as VBox).children.filter {
        it is Button || it is JFXButton}.map { it as Button }.toList()

    // Controlador principal
    private lateinit var mainController: MainController

    override fun preCargar() {
        super.preCargar {

            // Establecemos la salida implicita
            Platform.setImplicitExit(false)

            // Inicializamos el controlador de la vista
            mainController = MainController(this)

            // Establecemos los fragmentos que se usarán a lo largo
            // de la aplicación
            Platform.runLater {
                View.getLayoutPrincipal()!!.children.add(mainLayout)
            }
        }
    }

    override fun iniciar() {
        super.iniciar{

            // Cargamos las imágenes del "botonApagado" y del apartado "Cuenta"
            cargarImagenBotonApagado()
            cargarImagenMenuCuenta()

            // Atendemos los clicks de de los distintos items del menú
            botonesMenu.forEach {

                when {
                    // Es una opción del menú
                    !it.id.equals("botonApagar") -> { it.onMouseClicked = mainController.getItemMenuClickListener() }

                    // Es el botón de apagado
                    else -> { it.onMouseClicked = mainController.getCerrarClickListener()}
                }
            }

            // Seteamos el listener del boton "Cerrar"
            //etapaPrincipal.setOnCloseRequest =
            View.getEscenarioPrincipal()!!.onCloseRequest = mainController.getCerrarClickListener() as EventHandler<WindowEvent>

            // Trackeamos la sesión del usuario
            UserSessionTracker.track()

            // Marcamos como pulsado el botón de los plugins
            val botonPlugins = mainLayout.lookup("#botonPlugins") as Button
            botonMenuPulsado(botonPlugins)

            // TODO: Optimizar para cargar plugins de forma asíncrona mientras se carga el layout principal de la aplicación
            // Cargamos plugins válidos
            mainController.cambiarLayoutFragmento(PluginView())
        }
    }


    /**
     * Cargamos la imagen svg que tendrá el botón de apagado
     * del menú principal
     */
    private fun cargarImagenBotonApagado(){

        // Obtenemos el botón de apagar
        val botonApagar = mainLayout.lookup("#botonApagar") as JFXButton

        // Obtenemos la imagen del botón de apagado
        SVGGlyphLoader.loadGlyphsFont(javaClass.getResource("../../imagenes/vectoriales.svg"))
        val glyph = SVGGlyphLoader.getGlyph("vectoriales.svg.powerOff")

        // Le establecemos el color de fondo y algunas propiedades más
        glyph.fill = javafx.scene.paint.Color.valueOf("#ffffff")
        glyph.scaleX = 0.15
        glyph.scaleY = 0.30
        glyph.translateX = -10.0

        // Establecemos la imagen al boton
        botonApagar.graphic = glyph
    }

    /**
     * Cargamos la imagen del apartado "Cuenta" del menú
     * lateral
     */
    private fun cargarImagenMenuCuenta(){

        val botonAjustes = botonesMenu.first { it.id.equals("botonAjustes") }
        val botonCuenta = botonesMenu.first { it.id.equals("botonCuenta") }
        val imagen = ImageView(mainController.obtenerRutaImagenCuenta())
        imagen.isPreserveRatio = true
        imagen.fitHeight = botonAjustes.graphic.prefHeight(-1.0)
        imagen.fitWidth = botonAjustes.graphic.prefWidth(-1.0)
        botonCuenta.graphic = imagen
    }

    /**
     * Eliminamos todos los layoouts que puedan haber en el fragmento
     */
    fun limpiarFragmento(){
        Platform.runLater {
            fragmento.children.clear()
        }
    }

    override fun botonMenuPulsado(boton: Button) {

        // Recorremos los botones del menú
        botonesMenu.forEach {

            // Eliminamos la clase "boton-menu-pulsado" (si la tiene)
            it.styleClass.remove("boton-menu-pulsado")

            when {
                // Marcamos como pulsado el recibido por parámetros
                it.id != null && it.id.equals(boton.id) -> boton.styleClass.add("boton-menu-pulsado")
            }
        }
    }


    fun getFragmento(): AnchorPane {
        return fragmento
    }
}
