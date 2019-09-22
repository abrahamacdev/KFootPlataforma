package Vista.Login

import Controlador.UI.Login.LoginController
import Vista.Main.MainView
import Vista.View
import afester.javafx.svg.SvgLoader
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyphLoader
import com.jfoenix.validation.RequiredFieldValidator
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.geometry.Rectangle2D
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import sun.applet.Main

class LoginView: ILoginView, View {

    // Layout del login
    val loginLayout: StackPane by lazy {

        val stackPane: StackPane = FXMLLoader.load(javaClass.getResource("../../layouts/login.fxml"))

        // Establecemos el tamaño del fondo al tamaño total de la ventana
        AnchorPane.setBottomAnchor(stackPane,0.0)
        AnchorPane.setTopAnchor(stackPane,0.0)
        AnchorPane.setRightAnchor(stackPane,0.0)
        AnchorPane.setLeftAnchor(stackPane,0.0)

        stackPane
    }

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
            this.pausar() // Pausamos la vista
        }
        dialog
    }

    // Controller
    private val loginController = LoginController(this)

    // Fields
    val usuarioField by lazy {
        val field = loginLayout.lookup("#nombreUsuarioInput") as JFXTextField

        // Obtenemos la imagen del botón de apagado
        val imagen = Image(javaClass.getResource("../../imagenes/user.svg").toString(), 20.0, 20.0, false, true)

        //val backgroundSize = BackgroundSize(100.0, 100.0, true, true, true, false)
        val backgroundImage = BackgroundImage(imagen,BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition(Side.RIGHT, 0.0, false, Side.BOTTOM, 10.0, false), BackgroundSize.DEFAULT)
        field.background = Background(backgroundImage)

        field
    }
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

    override fun iniciar() {
        super.iniciar{

            Platform.runLater {
                // Añadimos el contendor del dialog al main layout
                View.getLayoutPrincipal()!!.children.add(loginLayout)
            }
        }
    }

    override fun pausar() {
        super.pausar{

        }
    }

    override fun reanudar() {
        super.reanudar{
            mostrarDialogCarga()
        }
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