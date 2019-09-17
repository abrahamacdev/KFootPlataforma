package Controlador.UI.Login

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import khttp.responses.Response

interface ILoginController {

    interface onLoginResult {

        fun onSucess()

        fun onError(respuesta: Response)
    }

    fun getOnLoginClickListener(): EventHandler<MouseEvent>
}