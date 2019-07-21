package Controlador.UI.Main

import Controlador.UI.IController
import Controlador.UI.Plugins.PluginsController
import Vista.Main
import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyphLoader
import com.sun.webkit.plugin.Plugin
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle


class MainController(private val main: Main, private val etapa: Stage): IMainController, EventHandler<MouseEvent>{

    // Fragmento del menú principal sobre el que se mostrará el contenido
    private lateinit var fragmentoPrincipal: Pane;

    // Layout del menú principal
    private lateinit var mainLayout: Parent

    // Escena principal
    private lateinit var escena: Scene

    // Botones del menú
    private val botonesMenu: ArrayList<Button> = ArrayList()

    // Controlador que esta ocupando actualmente el #fragmentoPrincipal
    private var controladorFragmento: IController? = null

    // Nos permitirá arrastrar la ventana en caso de no
    // querer los marcos que añade el SO a la ventana
    private var xOffset: Double = -1.0
    private var yOffset: Double = -1.0

    companion object {

        // Nos permitirá saber si hemos eliminado los bordes de la ventana
        private var bordesEliminados = false
    }



    override fun mostrarLayoutInicial() {
        // Guardamos el layout principal junto con el fragmento principal
        mainLayout = FXMLLoader.load<Parent>(javaClass.getResource("../../../layouts/main.fxml"))
        fragmentoPrincipal = mainLayout.lookup("#fragmentoPrincipal") as Pane

        // Creamos una escena a partir del layout principal
        escena = Scene(mainLayout)

        // Comenzamos el programa cargando los plugins válidos
        cambiarLayoutFragmento(PluginsController())
        val botonPlugins = escena.lookup("#botonPlugins") as Button
        botonPlugins.styleClass.remove("")
        botonPlugins.styleClass.remove("boton-menu-normal")
        botonPlugins.styleClass.add("boton-menu-pulsado")

        // Cargamos el botón de apagado
        cargarBotonApagado()

        // Establecemos un tamaño mínimo a la ventana
        establecerTamanioMin()

        // Establecemeos la escena a la etapa principal
        etapa.setScene(escena)

        // Atendemos los clicks de los botones del menú
        botonesMenu.addAll((mainLayout.lookup("#menu") as  VBox).children.filter { it is Button && !it.styleClass.contains("jfx-button")} as Collection<Button>)
        botonesMenu.forEach { (it as Button).onMouseClicked = this }

        // Mostramos el layout
        etapa.show()
    }



    /**
     * Cargamos la imagen svg que tendrá el botón de apagado
     * del menú principal
     */
    private fun cargarBotonApagado(){

        // Obtenemos el botón de apagar
        val botonApagar = escena.lookup("#botonApagar") as JFXButton

        // Obtenemos la imagen del botón de apagado
        SVGGlyphLoader.loadGlyphsFont(javaClass.getResource("../../../imagenes/vectoriales.svg"))
        val glyph = SVGGlyphLoader.getGlyph("vectoriales.svg.powerOff")

        // Le establecemos el color de fondo y algunas propiedades más
        glyph.fill = javafx.scene.paint.Color.valueOf("#ffffff")
        glyph.scaleX = 0.15
        glyph.scaleY = 0.25
        glyph.translateX = -20.0

        // Establecemos la imagen al boton
        botonApagar.graphic = glyph
    }

    /**
     * Le establecemos un tamaño mínimo a la ventana para
     * que esta no tome una forma extraña
     */
    private fun establecerTamanioMin(){
        etapa.minWidth = 900.0
        etapa.minHeight= 600.0
    }

    override fun handle(event: MouseEvent?) {
        val botonPulsado = (event!!.source as Button)
        val noEstaPulsado = botonPulsado.styleClass.contains("boton-menu-normal")

        when {

            // Si no estaba pulsado le cambiamos el color de fondo
            noEstaPulsado -> {

                // A los demás botones les ponemos el color de fondo normal
                botonesMenu.filter { it.styleClass.contains("boton-menu-pulsado")}
                        .forEach {demasBotones ->
                            demasBotones.styleClass.remove("boton-menu-pulsado")
                            demasBotones.styleClass.add("boton-menu-normal")
                        }

                // Al pulsado le cambiamos el color de fondo
                botonPulsado.styleClass.remove("boton-menu-normal")
                botonPulsado.styleClass.add("boton-menu-pulsado")



                // Cambiamos el layout del fragmento principal
                var tempControlFrag: IController? = null
                when {

                    // Cargamos el fragmento de plugins
                    botonPulsado.id.equals("botonPlugins") -> {

                        // Creamos una instancia del nuevo controlador del fragmento
                        tempControlFrag = PluginsController()
                    }

                    // Cargamos el fragmento de los ajustes de la aplicación
                    botonPulsado.id.equals("botonAjustes") -> {

                        // Creamos una instancia del nuevo controlador del fragmento
                        //tempControlFrag = AjustesController()
                    }

                    // Cargamos el fragmento de plugins
                    botonPulsado.id.equals("botonCuenta") -> {

                        // Creamos una instancia del nuevo controlador del fragmento
                        //tempControlFrag = CuentaController()
                    }
                }
                if (tempControlFrag != null){
                    cambiarLayoutFragmento(tempControlFrag)
                }
            }
        }
    }

    /**
     * Cambiamos el contenido del fragmento por uno nuevo
     *
     * @param nuevoControllFrag: Nuevo [IController] que modificará el layout del fragmento
     */
    private fun cambiarLayoutFragmento(nuevoControllFrag: IController){

        // Comprobamos si el controlador del fragmento que hay actualmente establecido
        // es diferente al nuevo que estableceremos
        if (esDiferenteControladorFrag(nuevoControllFrag)){

            // Comprobamos que haya algún controlador del fragmento
            if (controladorFragmento != null){
                controladorFragmento!!.cancelar()
            }

            println("Cambiamos el fragmento por el de los plugins")

            // Iniciamos el nuevo controlador del fragmento
            controladorFragmento = nuevoControllFrag
            controladorFragmento!!.iniciar(fragmentoPrincipal)
        }
    }

    /**
     * Comprobamos si el nuevo [controlador] es el mismo
     * que el actual controlador ([controladorFragmento])
     */
    private fun esDiferenteControladorFrag(controlador: IController): Boolean{
        return controlador != this.controladorFragmento
    }

}