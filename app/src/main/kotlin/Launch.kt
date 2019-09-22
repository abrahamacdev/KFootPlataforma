import Controlador.Setup
import Datos.Modelo.ParametrosEscenario
import KFoot.DEBUG
import KFoot.Logger
import Utiles.Utils
import Vista.View
import Vista.Main.MainView
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage


class Launch: Application() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            Launch().lanzar(args)
        }
    }

    fun lanzar(args: Array<String>){
        Application.launch(*args)
    }

    override fun start(p0: Stage?) {

        // Permitimos el parseo de svg's como imágenes no vectoriales
        SvgImageLoaderFactory.install();

        // Seteamos el nivel de debug
        Logger.getLogger().setDebugLevel(DEBUG.DEBUG_TEST)

        // Realizamos las comprobaciones iniciales
        Setup.realizarComprobaciones()

        // Establecemos los layouts principales que se usarán a lo largo de la aplicación
        val base: Parent = FXMLLoader.load(javaClass.getResource("./layouts/base.fxml"))
        View.setearLayoutBase(base)

        // Establecemos los parámetros del escenario
        val parametrosEscenarioFactory = ParametrosEscenario.ParametrosEscenarioFactory()
        val anchoAltoMin = Utils.obtenerTamanioMin()
        parametrosEscenarioFactory.conAnchoYAltoMinimos(anchoAltoMin.first,anchoAltoMin.second)
        parametrosEscenarioFactory.conPantallaMaximizada()
        parametrosEscenarioFactory.mostrarEscenarioInmediatamente()

        // Creamos los parámetros del escenario a partir del factory anterior
        val parametrosEscenario = parametrosEscenarioFactory.build()

        // Guardamos el escenario principal
        View.crearEscenaConEscenario(p0!!, parametrosEscenario)

        // Creamos la vista principal y la iniciamos
        val mainView = MainView()
        mainView.preCargar()
        mainView.iniciar()
    }


}