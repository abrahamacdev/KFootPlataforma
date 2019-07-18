package Vista

import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyphLoader
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.*
import javafx.scene.layout.Pane
import javafx.stage.StageStyle

/**
 * Desde aquí lanzaremos el "Office" desde donde se cargaŕan
 * los diferente plugins para la obtención de los datos
 *
 * @author Abraham Álvarez
 * @since 1.0
 */
class Main(): Application() {

    var xOffset: Double = 0.0
    var yOffset: Double = 0.0
    private lateinit var escena: Scene
    private lateinit var etapa: Stage
    private lateinit var mainLayout: Parent
    private lateinit var fragmentoPrincipal: Pane

    companion object {

        @JvmStatic
        fun main(args: Array<String>) = runBlocking{

            val c = Main().lanzar(args)

            // Realizamos las comprobaciones iniciales del programa
            /*Setup.realizarComprobaciones(args)

            // Obtenemos el office que se encargara de comprobar los plugins
            // existentes y de lanzarlos
            val office = Office()

            // Si hay plugins validos, los cargaremos y ejecutaremos
            if(office.existenPlugins()){

                // Cargamos en memoria los plugins
                office.cargarPlugins()

                // Ejecutamos los plugins cargados
                val supervisor = office.ejecutarPlugins()
            }*/
        }
    }

    private fun lanzar(argumentos: Array<String>){
        Application.launch(*argumentos)
    }

    override fun start(primaryStage: Stage?) {

        mainLayout = FXMLLoader.load<Parent>(javaClass.getResource("../layouts/main.fxml"))

        // Cargamos el spinner en el #fragmentoPrincipal
        val buscandoPlugins = FXMLLoader.load<Node>(javaClass.getResource("../layouts/buscandoPlugins.fxml"))
        fragmentoPrincipal = mainLayout.lookup("#fragmentoPrincipal") as Pane
        fragmentoPrincipal.children.add(buscandoPlugins)

        // Creamos una escena a partir del layout principal
        escena = Scene(mainLayout)
        etapa = primaryStage!!

        // Establecemos un tamaño mínimo a la ventana
        etapa.minWidth = 900.0
        etapa.minHeight= 600.0

        // Establecemeos la escena a la etapa principal
        etapa.setScene(escena)

        // Cargamos el botón de apagado (SVG)
        cargarBotonApagado()

        // Quitamos el marco que Windows añade
        //sinMarcos()

        // Evitamos que el boton aparezca pulsado
        mainLayout.requestFocus()

        // Mostramos el layout
        etapa.show()
    }

    /**
     * Cargamos la imagen svg que tendrá el botón de apagado
     * del menú principal
     */
    fun cargarBotonApagado(){

        // Obtenemos el botón de apagar
        val botonApagar = escena.lookup("#botonApagar") as JFXButton

        // Obtenemos la imagen del botón de apagado
        SVGGlyphLoader.loadGlyphsFont(javaClass.getResource("../../resources/imagenes/vectoriales.svg"))
        val glyph = SVGGlyphLoader.getGlyph("vectoriales.svg.powerOff")

        // Le establecemos el color de fondo y algunas propiedades más
        glyph.fill = javafx.scene.paint.Color.valueOf("#ffffff")
        glyph.scaleX = 0.15
        glyph.scaleY = 0.25
        glyph.translateX = -20.0

        //
        botonApagar.graphic = glyph
    }

    /**
     * Eliminamos los marcos que el sistema operativo
     * añade por defecto a todas las ventanas
     *
     * @param movible: Si queremos que la ventana se pueda mover por la pantalla
     */
    fun sinMarcos(movible: Boolean = false){

        // Quitamos el marco
        etapa.initStyle(StageStyle.UNDECORATED);

        // Permitimos que mueva la ventana por la pantalla
        if (movible){
            mainLayout.setOnMousePressed({ event ->
                xOffset = event.sceneX
                yOffset = event.sceneY
            })

            //move around here
            mainLayout.setOnMouseDragged({ event ->
                etapa.setX(event.screenX - xOffset)
                etapa.setY(event.screenY - yOffset)
            })
        }
    }
}
