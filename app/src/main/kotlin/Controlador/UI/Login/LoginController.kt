package Controlador.UI.Login

import Vista.Login.LoginView
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class LoginController(private val loginView: LoginView): ILoginController, CoroutineScope {

    // Contexto en el que se ejecutarán las tareas
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    override fun getOnLoginClickListener(): EventHandler<MouseEvent> {
        
        return object : EventHandler<MouseEvent> {
            override fun handle(p0: MouseEvent?) {

                // Reseteamos los validadores
                loginView.usuarioField.resetValidation()
                loginView.contraseniaField.resetValidation()

                // Obtenemos el nombre de usuario y la contrasenia
                val nombreUsuario = loginView.usuarioField.text
                val contrasenia = loginView.contraseniaField.characters

                if (nombreUsuario.isEmpty()){
                    loginView.mostrarValidatorConMensaje(loginView.usuarioValidator,"Campo vacío")
                    return
                }

                if (contrasenia.isEmpty()){
                    loginView.mostrarValidatorConMensaje(loginView.contraseniaValidator,"Campo vacío")
                    return
                }

                else {


                }
            }
        }
    }
}