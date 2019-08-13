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
        val mainView = MainView()
        mainView.start(p0)
    }
}