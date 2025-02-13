package Vista.Ajustes

import Controlador.UI.Ajustes.AjustesController
import KFoot.Logger
import Modelo.Preferencias
import Utiles.Constantes
import Utiles.Utils
import Vista.View
import afester.javafx.svg.SvgLoader
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.validation.RequiredFieldValidator
import javafx.fxml.FXMLLoader
import javafx.scene.control.ScrollPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AjustesView: IAjustesView, View(), CoroutineScope {

    private lateinit var ajustesController: AjustesController

    // Job asociado a la coroutina
    private val job: Job = SupervisorJob()

    // Contexto en el que se ejecutarán las tareas
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    // Deferred de la pre carga
    private lateinit var deferredPreCarga: Deferred<Unit>

    companion object {

        // Layout de "AjustesController" (ajustes.fxml)
        private val layoutAjustes: AnchorPane = FXMLLoader.load<AnchorPane>(javaClass.getResource("../../layouts/ajustes.fxml"))

        var inputDirPlugins: JFXTextField? = null
        var botonDirPlugin: JFXButton? = null

        var botonAplicarCambios: JFXButton? = null
    }


    override fun preCargar() {
        super.preCargar()

        // Creamos el controlador y seteamos los listeners
        deferredPreCarga = async {

            // Obtenemos el controlador
            ajustesController = AjustesController.getInstancia(this@AjustesView)

            // Seteamos los listeners a los botones
            val vbox = (layoutAjustes.children.get(0) as ScrollPane).content as VBox
            val jfxBotones = Utils.buscarNodosPorTipo(JFXButton::class.java, vbox) as ArrayList<JFXButton>
            jfxBotones.forEach {it.onMouseClicked = ajustesController.getBotonClickListener()}
        }
    }

    override fun iniciar() {
        super.iniciar()

        // Esperamos a que se complete la pre carga
        runBlocking {
            deferredPreCarga.await()
        }

        // Mostramos el layout de ajustes
        renovarFragmentoPrincipal(layoutAjustes)

    }

    override fun establecerLabelsInputs(){
        // VBox que contiene todos los controles del fragmento
        val vbox = (layoutAjustes.children.get(0) as ScrollPane).content as VBox

        if (inputDirPlugins == null){
            inputDirPlugins = Utils.buscarNodoPorId("inputDirPlugins", vbox) as JFXTextField
        }

        // Establecemos el valor del "directorioPlugins" actual
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