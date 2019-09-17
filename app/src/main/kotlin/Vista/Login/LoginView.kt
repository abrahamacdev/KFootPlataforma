package Vista.Login

import Controlador.UI.Login.LoginController
import Vista.View
import afester.javafx.svg.SvgLoader
import com.jfoenix.controls.*
import com.jfoenix.validation.RequiredFieldValidator
import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane

class LoginView: ILoginView, View {

    // Layout del login
    val loginLayout: AnchorPane = FXMLLoader.load(javaClass.getResource("../../layouts/login.fxml"))

    // Dialog de carga
    val dialogCarga: JFXDialog by lazy {
        val stackPane: StackPane =  loginLayout.lookup("#stack") as StackPane
        stackPane.isVisible = false
        stackPane.isFocusTraversable = false
        val dialogLayout: JFXDialogLayout = stackPane.children.get(0) as JFXDialogLayout

        // Añadimos los estilos para que el #stackPane ocupe toodo
        // el tamaño del fragmento principal
        AnchorPane.setRightAnchor(stackPane,0.0)
        AnchorPane.setLeftAnchor(stackPane,0.0)
        AnchorPane.setTopAnchor(stackPane,0.0)
        AnchorPane.setBottomAnchor(stackPane,0.0)
        stackPane.style = "-fx-background-color: transparent;\n"

        // Creamos el diálogo con el contendor principal (#stackPane) y el layout
        // que mostrará la animación
        val dialog = JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER, false)
        dialog!!.style = "-fx-background-color: transparent;\n"

        dialog.setOnDialogClosed {
            dialog.dialogContainer.isVisible = false
            dialog.dialogContainer.isFocusTraversable = false
        }
        dialog
    }

    // Controller
    private val loginController = LoginController(this)

    // Fields
    val usuarioField = loginLayout.lookup("#nombreUsuarioInput") as JFXTextField
    val contraseniaField = loginLayout.lookup("#contraseniaField") as JFXPasswordField

    // Validadores
    val usuarioValidator: RequiredFieldValidator = RequiredFieldValidator()
    val contraseniaValidator: RequiredFieldValidator = RequiredFieldValidator()


    constructor(){

        // Seteamos el click listener al botón de loguear
        (loginLayout.lookup("#botonLoguearse") as JFXButton).onMouseClicked = loginController.getOnLoginClickListener()

        // Establecemos los validadores de los labels
        establecerValidadores()
    }

    private fun establecerValidadores(){

        val svgImage = SvgLoader().loadSvg(javaClass.getResourceAsStream("../../imagenes/error.svg"))
        svgImage.scaleX = 0.3
        svgImage.scaleY = 0.3

        usuarioValidator!!.icon = svgImage
        contraseniaValidator!!.icon = svgImage

        usuarioField.validators.add(usuarioValidator)
        contraseniaField.validators.add(contraseniaValidator)
    }



    override fun mostrarValidatorConMensaje(validator: RequiredFieldValidator, msg: String){
        validator.message = msg

        if (usuarioField.validators.find { it == validator } != null) usuarioField.validate()
        else contraseniaField.validate()
    }

    override fun mostrarDialogCarga(){
        dialogCarga.dialogContainer.isVisible = true
        dialogCarga.dialogContainer.isFocusTraversable = true
        dialogCarga.show()
    }

    override fun esconderDialogCarga(){
        dialogCarga.close()
    }
}