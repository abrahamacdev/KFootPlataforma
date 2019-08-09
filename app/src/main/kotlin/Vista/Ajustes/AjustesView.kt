package Vista.Ajustes

import Controlador.UI.Ajustes.AjustesController
import Modelo.Preferencias
import Utiles.Constantes
import Utiles.Utils
import afester.javafx.svg.SvgLoader
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.validation.RequiredFieldValidator
import javafx.fxml.FXMLLoader
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox

class AjustesView: IAjustesView{

    private lateinit var ajustesController: AjustesController

    companion object {

        // Layout de "AjustesController" (ajustes.fxml)
        private val layoutAjustes: ScrollPane = FXMLLoader.load<ScrollPane>(javaClass.getResource("../../layouts/ajustes.fxml"))

        var inputDirPlugins: JFXTextField? = null
        var botonDirPlugin: JFXButton? = null

        var botonAplicarCambios: JFXButton? = null
    }



    override fun iniciar(fragmento: Pane) {
        super.iniciar(fragmento)

        // Obtenemos el controlador
        ajustesController = AjustesController.getInstancia(this)

        // Seteamos los listeners
        val vbox = layoutAjustes.content as VBox
        val jfxBotones = Utils.buscarNodosPorTipo(JFXButton::class.java, vbox) as ArrayList<JFXButton>
        jfxBotones.forEach {it.onMouseClicked = ajustesController.getBotonClickListener()}

        // Mostramos el layout de ajustes
        renovarContenidoFragmento(layoutAjustes)
    }

    override fun cancelar() {}


    override fun establecerLabelsInputs(){
        // VBox que contiene todos los controles del fragmento
        val vbox = layoutAjustes.content as VBox

        // Establecemos el valor del "directorioPlugins" actual
        inputDirPlugins = Utils.buscarNodoPorId("inputDirPlugins", vbox) as JFXTextField
        inputDirPlugins!!.text = Preferencias.obtenerOrNulo(Constantes.RUTA_PLUGINS_KEY).toString() ?: ""
    }

    override fun anadirValidadoresInputs() {
        val validadorInputDir = RequiredFieldValidator()
        val svgImage = SvgLoader().loadSvg(javaClass.getResourceAsStream("../../imagenes/warning.svg"))
        svgImage.scaleX = 1.0
        svgImage.scaleY = 1.0
        validadorInputDir.icon = svgImage
        validadorInputDir.message = "Directorio no válido"
        inputDirPlugins!!.validators.add(validadorInputDir)
    }

    override fun bloquearInputs() {
        inputDirPlugins!!.isEditable = false
    }

    override fun obtenerValoresInputs(): HashMap<String, Any> {

        val valores = HashMap<String,Any>()

        valores.put("inputDirPlugins", inputDirPlugins!!.text)

        return valores
    }
}