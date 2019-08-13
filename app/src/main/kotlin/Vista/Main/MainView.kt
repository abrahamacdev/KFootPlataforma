package Vista.Main

import Controlador.UI.Main.MainController
import Utiles.Constantes
import Vista.Plugins.PluginView
import Vista.View
import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyphLoader
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Screen
import javafx.stage.Stage
import kotlinx.coroutines.*

/**
 * Desde aquí lanzaremos el "Office" desde donde se cargaŕan
 * los diferente plugins para la obtención de los datos
 *
 * @author Abraham Álvarez
 * @since 1.0
 */
class MainView(): View(), IMainView {

    // Layout principal de la aplicación
    private val mainLayout: Parent = FXMLLoader.load<Parent>(javaClass.getResource("../../layouts/main.fxml"))

    // Escena principal
    private lateinit var escenaPrincipal: Scene

    // Botones del menú
    private val botonesMenu: List<Button> = (mainLayout.lookup("#menu") as VBox).children.filter {
        it is Button && !it.styleClass.contains("jfx-button")}.map { it as Button }.toList()

    // Controlador principal
    private lateinit var mainController: MainController

    companion object {

        // Etapa principal
        private lateinit var etapaPrincipal: Stage

        fun getEtapa(): Stage{
            return etapaPrincipal
        }

    }

    override fun start(etapa: Stage?) {

        // Precargamos la clase
        this.preCargar()

        // Guardamos la etapa principal
        etapaPrincipal = etapa!!

        // Iniciamos esta vista
        iniciar(mainLayout.lookup("#fragmentoPrincipal") as Pane)
    }

    override fun preCargar() {
        super.preCargar()

        // Inicializamos el controlador de la vista
        mainController = MainController(this)
    }

    override fun iniciar(fragmento: Pane) {
        super.iniciar(fragmento)

        // Guardamos la escena principal
        escenaPrincipal = Scene(mainLayout)

        // Cargamos las imágenes del "botonApagado" y del apartado "Cuenta"
        cargarImagenBotonApagado()
        cargarImagenMenuCuenta()

        // Atendemos los clicks de los botones del menú
        botonesMenu.forEach { (it as Button).onMouseClicked = mainController.getMenuClickListener() }

        // Marcamos como pulsado el botón de los plugins
        val botonPlugins = escenaPrincipal.lookup("#botonPlugins") as Button
        botonPulsado(botonPlugins)

        // Establecemos un tamaño mínimo a la ventana
        establecerTamanioMin()

        // Establecemos la escena a la etapa, maximizamos la ventana y la mostramos
        etapaPrincipal.scene = escenaPrincipal
        etapaPrincipal.isMaximized = true
        etapaPrincipal.show()

        // TODO: Optimizar para cargar plugins de forma asíncrona mientras se carga el layout principal de la aplicación
        // Cargamos plugins válidos
        mainController.cambiarLayoutFragmento(PluginView())
    }

    /**
     * Cargamos la imagen svg que tendrá el botón de apagado
     * del menú principal
     */
    private fun cargarImagenBotonApagado(){

        // Obtenemos el botón de apagar
        val botonApagar = escenaPrincipal.lookup("#botonApagar") as JFXButton

        // Obtenemos la imagen del botón de apagado
        SVGGlyphLoader.loadGlyphsFont(javaClass.getResource("../../imagenes/vectoriales.svg"))
        val glyph = SVGGlyphLoader.getGlyph("vectoriales.svg.powerOff")

        // Le establecemos el color de fondo y algunas propiedades más
        glyph.fill = javafx.scene.paint.Color.valueOf("#ffffff")
        glyph.scaleX = 0.125
        glyph.scaleY = 0.25
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
     * Le establecemos un tamaño mínimo a la ventana para
     * que esta no tome una forma extraña
     */
    private fun establecerTamanioMin(){

        val tamanioPantalla = Screen.getPrimary().visualBounds

        val tempWidth = tamanioPantalla.width / 5 * 4
        val tempHeight = tamanioPantalla.getWidth() / 5 * 2

        etapaPrincipal.minWidth = if (tempWidth < Constantes.ANCHO_MINIMO) Constantes.ANCHO_MINIMO else tempWidth
        etapaPrincipal.minHeight = if (tempHeight < Constantes.ALTO_MINIMO) Constantes.ALTO_MINIMO else tempHeight
    }

    override fun botonPulsado(boton: Button)    {
        botonesMenu.forEach {
            it.styleClass.remove("boton-menu-pulsado")

            when {
                // Marcamos como pulsado el recibido por parámetros
                it.id != null && it.id.equals(boton.id) -> boton.styleClass.add("boton-menu-pulsado")

                // Los demás los dejamos sin pulsar
                else -> it.styleClass.add("boton-menu-normal")
            }
        }

    }
}
