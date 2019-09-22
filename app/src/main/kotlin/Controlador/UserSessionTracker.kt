package Controlador

import KFoot.DEBUG
import KFoot.Logger
import Vista.Login.LoginView
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class UserSessionTracker: CoroutineScope {

    // Alcance de nuestra coroutina
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + supervisorJob

    // Vista que ejecutaremos cuando necesitemos un login
    private val loginView = LoginView()

    companion object {

        private var userSessionTracker: UserSessionTracker? = null

        fun track(){

            // Comprobamos que no se esté trackeando ya
            if (userSessionTracker == null){
                userSessionTracker = UserSessionTracker()
            }
        }
    }

    private constructor(){

        // Realizamos las comprobaciones en segundo plano
        launch {

            // Comprobamos si NO hay token guardado
            if (!comprobarValidezToken()){

                // Realizamos el login
                loginView.iniciar()
            }

            // Mantenemos un seguimiento de la sesión del usuario
            rastrear()
        }
    }

    private fun rastrear(){

        Observable.interval(5, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.computation())
                .subscribe {

                    // TODO Comprobar que el token que posee el usuario actualmente sigue estando activo
                    if (!comprobarValidezToken()){
                        loginView.reanudar()
                    }
                }
    }

    @TestOnly
    private fun comprobarValidezToken(): Boolean{

        if (Logger.getLogger().getDebugLevel() == DEBUG.DEBUG_TEST) return false else return true
    }
}