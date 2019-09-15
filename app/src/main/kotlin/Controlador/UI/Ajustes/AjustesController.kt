package Controlador.UI.Ajustes

import Controlador.UI.Controller
import KFoot.Logger
import Modelo.Preferencias
import Utiles.Constantes
import Utiles.Utils
import Vista.Ajustes.AjustesView
import Vista.Ajustes.AjustesView.Companion.inputDirPlugins
import kotlin.coroutines.CoroutineContext
import java.io.File
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.*


class AjustesController(private val ajustesView: AjustesView): CoroutineScope, IAjustesController, Controller() {


    // Contexto en el que se ejecutarán las tareas
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job



    companion object {

        private var instancia: AjustesController? = null

        fun getInstancia(ajustesView: AjustesView): AjustesController{

            if (instancia == null){
                instancia = AjustesController(ajustesView)
            }

            instancia!!.preCargar()
            return instancia!!
        }

        // Job asociado a la coroutina
        private val job: Job = SupervisorJob()

        // Listener de los botones
        private var botonesClickListener: EventHandler<MouseEvent>? = null

        private var yaEjecutado: Boolean = false
    }

    override fun preCargar() {

        async {

            // Establecemos los valores actuales a los inputs del layout
            ajustesView.establecerLabelsInputs()

            // Comprobamos que no se haya ejecutado antes
            if (!yaEjecutado) {

                // Añadimos los validadores a los inputs
                ajustesView.anadirValidadoresInputs()

            }

            // Guardamos la ejecución
            yaEjecutado = true
        }

        // Aplicar sombras
        //iconPane.setEffect(DropShadow(2.0, 0.0, +2.0, Color.BLACK))
    }

    override fun getBotonClickListener(): EventHandler<MouseEvent> {

        if (botonesClickListener == null){

            botonesClickListener = object : EventHandler<MouseEvent>{
                override fun handle(event: MouseEvent?) {
                    val boton = event!!.source as Button

                    if (boton.id != null){
                        when {

                            boton.id.equals("botonDirPlugin") -> {
                                val ruta = Utils.mostrarSelectorDirectorio()

                                // Comprobamos que haya una ruta válida
                                if (ruta != null){

                                    if (AjustesView.inputDirPlugins != null){
                                        // Establecemos la ruta en el texto del "inputDirPlugins"
                                        inputDirPlugins!!.text = ruta.absolutePath
                                    }
                                }
                            }

                            // Aplicamos los cambios que hayan ocurrido
                            boton.id.equals("aplicarCambios") -> {
                                aplicarCambiosAjustes()
                            }
                        }
                    }
                }
            }
        }
        return botonesClickListener!!
    }


    /**
     * Aplicamos los cambios que se hayan realizado
     * a los diferentes ajustes
     */
    private fun aplicarCambiosAjustes(){

        // Bloqueamos la modificación de valores
        ajustesView.bloquearInputs()

        // Obtenemos los valores de los inputs
        val valores = ajustesView.obtenerValoresInputs()

        val rutaPlugin = valores.get("inputDirPlugins")

        // Modificamos la ruta de directorios si es diferente respecto a la actual
        if (rutaPlugin != null && (rutaPlugin as String).trim().isNotEmpty()){
            inputDirPlugins!!.resetValidation()
            modificarDirPlugins(rutaPlugin as String)
        }else {
            inputDirPlugins!!.validate()
        }
    }

    /**
     * Modificamos el directorio de plugins si el nuevo
     * es válido y no coincide con el actual establecido
     */
    private fun modificarDirPlugins(nuevoValor: String){
        val tempFile = File(inputDirPlugins!!.text)
        if (!tempFile.exists() || !tempFile.isDirectory || !tempFile.canRead()){
            inputDirPlugins!!.validate()
        }
        else {

            inputDirPlugins!!.resetValidation()
            val antiguoVal = Preferencias.obtenerOrNulo(Constantes.RUTA_PLUGINS_KEY)

            when {
                // No teníamos ninguna ruta guardada, la añadimos
                antiguoVal == null -> {
                    Preferencias.anadir(Constantes.RUTA_PLUGINS_KEY, inputDirPlugins!!.text)
                }

                // Comprobamos si la nueva ruta coincide con la ya establecida
                !antiguoVal.equals(inputDirPlugins!!.text) -> {
                    Preferencias.modificar(Constantes.RUTA_PLUGINS_KEY, inputDirPlugins!!.text)
                }
            }
        }
    }

}