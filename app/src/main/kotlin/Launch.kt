import KFoot.DEBUG
import KFoot.Logger
import Vista.Main.MainView
import javafx.application.Application
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

        // Seteamos el nivel de debug
        Logger.getLogger().setDebugLevel(DEBUG.DEBUG_SIMPLE)

        // TODO Eliminar
        /*Observable.interval(1, TimeUnit.SECONDS).subscribe {
            println("Memoria consumida ${Utils.memoriaUsada()} MB (Max: ${Utils.memoriaTotal()})MB")
        }*/

        val mainView = MainView()
        mainView.start(p0)
    }
}