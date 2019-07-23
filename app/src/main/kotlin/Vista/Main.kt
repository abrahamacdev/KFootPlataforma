package Vista

import Controlador.Setup
import Controlador.UI.Main.MainController
import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.*

/**
 * Desde aquí lanzaremos el "Office" desde donde se cargaŕan
 * los diferente plugins para la obtención de los datos
 *
 * @author Abraham Álvarez
 * @since 1.0
 */
class Main(): Application() {

    // Controlador principal
    private lateinit var mainController: MainController


    companion object {

        @JvmStatic
        fun main(args: Array<String>) = runBlocking{

            val c = Main().lanzar(args)
        }
    }



    private fun lanzar(argumentos: Array<String>){
        Application.launch(*argumentos)
    }

    override fun start(primaryStage: Stage?) {

        // Realizamos las comprobaciones iniciales del programa
        Setup.realizarComprobaciones()

        // Creamos el controlador de la clase principal
        mainController = MainController(this, primaryStage!!)

        // Mostramos la pantalla inicial de la plataforma
        mainController.mostrarLayoutInicial()

        // Obtenemos el office que se encargara de comprobar los plugins
        // existentes y de lanzarlos
        /*val office = Office()

        // Si hay plugins validos, los cargaremos y ejecutaremos
        if(office.existenPlugins()){

            // Cargamos en memoria los plugins
            office.cargarPlugins()

            // Ejecutamos los plugins cargados
            val supervisor = office.ejecutarPlugins()
        }*/
    }
}
