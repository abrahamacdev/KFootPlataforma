package Vista.Login

import com.jfoenix.validation.RequiredFieldValidator

interface ILoginView {

    interface onLoginNeeded {
        fun onNewLogin()
    }

    fun mostrarDialogCarga()

    fun esconderDialogCarga()

    fun mostrarValidatorConMensaje(validator: RequiredFieldValidator, msg: String)
}