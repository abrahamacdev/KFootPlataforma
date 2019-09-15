package Modelo.Plugin

import Controlador.Supervisor.ISupervisor
import Controlador.Supervisor.Supervisor
import KFoot.Logger
import Utiles.plus
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import kotlinx.coroutines.*
import lib.Plugin.IPlugin
import java.io.File
import java.lang.Exception
import java.util.concurrent.atomic.AtomicLong
import java.util.jar.JarFile
import kotlin.coroutines.CoroutineContext

/**
 * Esta clase se encarga de ejecutar cada uno de los plugins que se encuentren disponibles.
 * La ejecucion del plugin se realizara en una coroutina dedicada para las
 * tareas CPU-Intensivas
 *
 * @param jar: Archivo jar con el codigo del plugin a ejecutar
 * @param clasePrincipal: Clase que contiene el metodo de cargado y ejecucion del plugin
 */
class Plugin (val jar: File, val clasePrincipal: Class<*>): CoroutineScope, IPlugin.onAvisosPluginListener{

    // Interfaz que usaremos para controlar la ejecución del plugin
    private var controlPluginListener: IPlugin.onControlPluginListener? = null

    // Interfaz por la que informaremos al supervisor
    // de la correcta/erronea ejecucion del plugin
    private var resultadoEjecucionListener: ISupervisor.onPluginEjecutado? = null

    // Tarea vinculada al PluginView y contexto de la coroutina
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job
    private var completableDeferred: CompletableDeferred<Unit>? = null


    // Id del plugin para la sesion actual
    val ID: AtomicLong = Companion.ID

    // Objeto que almacena los metadatos del plugin
    private var metadatosPlugin: MetaPlugin? = null

    // Estado actual del plugin
    private var estadoActual: EstadosPlugin = EstadosPlugin.INACTIVO

    init {
        cargarMetadatos()
    }

    companion object  {

        // Nos servirá para identificar a cada plugin unequívocamente
        private var ID: AtomicLong = AtomicLong(0)

        // Actualizamos el valor del ID
        get() {
            val temp = field
            field+=1
            return temp
        }

    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this.javaClass != other.javaClass) return false
        if (this === other) return true
        val plugin = other as Plugin
        return ID === plugin.ID
    }

    override fun toString(): String {
        var msg: String = "Id: $ID "

        if (metadatosPlugin != null){
           msg += "| Nombre: ${metadatosPlugin!!.nombrePlugin}"
        }
        return msg
    }

    fun getMetaDatos(): MetaPlugin?{
        return metadatosPlugin
    }

    fun getEstadoActual(): EstadosPlugin{
        return estadoActual
    }



    /**
     * Activamos el plugin para que comience a ejecutarse
     * en su propia coroutina
     *
     * @param onResultadoInicioListener: Listener por el que transimitiremos el correcto inicio de la ejecución del plugin
     * @param onPluginEjecutadoListener: Listener por el que transmitiremos el resultado de la ejecucion del plugin
     */
    fun activar(onResultadoInicioListener: IPlugin.onResultadoAccionListener? = null, onPluginEjecutadoListener: ISupervisor.onPluginEjecutado? = null) {

        // Para activar el plugin este no debe de estar ejecutándose
        // ni haber completado su ejecucion
        if (estadoActual == EstadosPlugin.INACTIVO || estadoActual == EstadosPlugin.COMPLETADO){

            // Establecemos el listener
            resultadoEjecucionListener = onPluginEjecutadoListener

            // Cambiamos el estado actual del plugin
            estadoActual = EstadosPlugin.ACTIVO

            // Ejecutamos el plugin en la coroutina dedicada
            launch(coroutineContext){

                // Creamos una instancia de la clase principal del plugin,
                // lo sincronizamos con la plataforma y comenzamos su ejecución
                val obj = clasePrincipal.newInstance()
                val metodoSync = clasePrincipal.declaredMethods.find { it.name.equals(IPlugin::class.java.methods[0].name) }
                controlPluginListener = metodoSync!!.invoke(obj, this@Plugin) as IPlugin.onControlPluginListener

                // Iniciamos el plugin
                controlPluginListener!!.onIniciar(object : IPlugin.onResultadoAccionListener {

                    override fun onCompletado() {
                        // Guardamos un deferred que usaremos para avisar
                        // de la ejecucion del plugin
                        completableDeferred = CompletableDeferred(job)

                        // Indicaremos que se ha iniciado correctamente el plugin
                        if (onResultadoInicioListener != null){
                            onResultadoInicioListener.onCompletado()
                        }
                    }

                    override fun onError(e: Exception) {

                        // Indicamos que no se ha podido iniciar el plugin
                        if (onResultadoInicioListener != null){
                            onResultadoInicioListener.onError(e)
                        }

                        // Transmitimos la finalización de la ejecución del plugin
                        // y acabamos con la coroutina ligada al plugin
                        onPluginTerminado(e)
                    }
                })
            }
        }
    }

    /**
     * Pausamos la ejecución del plugin si se está
     * ejecutanto
     *
     * @param resultadoAccionListener: Listener por el que transmitiremos el resultado del pausado
     */
    fun pausar(resultadoAccionListener: IPlugin.onResultadoAccionListener? = null){

        // Comprobamos que el plugin se esté ejecutando actualmente
        if (controlPluginListener != null && estadoActual == EstadosPlugin.ACTIVO){

            controlPluginListener!!.onPausar(object : IPlugin.onResultadoAccionListener {
                override fun onCompletado() {

                    // Transmitimos el completado por el listener
                    if (resultadoAccionListener != null){
                        resultadoAccionListener.onCompletado()
                    }
                }

                override fun onError(e: Exception) {

                    // Transmitimos el error por el listener
                    if (resultadoAccionListener != null){
                        resultadoAccionListener.onError(e)
                    }
                }
            })
        }
    }

    /**
     * Cancelamos por completo la ejecución del plugin si se está
     * ejecutando
     *
     * @param resultadoAccionListener: Listener por el que transmitiremos el resultado del pausado
     */
    fun cancelar(resultadoAccionListener: IPlugin.onResultadoAccionListener? = null){

        // Si el plugin esta activo, mandamos la orden de cancelar su ejecución
        if (estadoActual == EstadosPlugin.ACTIVO){

            controlPluginListener!!.onCancelar(object : IPlugin.onResultadoAccionListener {
                override fun onCompletado() {

                    // Transmitimos el completado por el listener
                    if (resultadoAccionListener != null){
                        resultadoAccionListener.onCompletado()
                    }
                }

                override fun onError(e: Exception) {

                    // Transmitimos el error por el listener
                    if (resultadoAccionListener != null){
                        resultadoAccionListener.onError(e)
                    }
                }
            })
        }

        // Si el plugin no está activo, transmitimos el completado
        if (resultadoAccionListener != EstadosPlugin.ACTIVO){
            resultadoAccionListener!!.onCompletado()
        }
    }




    /**
     * Obtenemos los metadatos del plugin a traves
     * de su archivo "config.json"
     */
    private fun cargarMetadatos(){

        // Comprobamos que aún no hayamos cargado los metadatos
        if (metadatosPlugin == null){

            val jarFile = JarFile(jar)

            // Cargamos el archivo json del jar
            val configJson = jarFile.getEntry("config.json") ?: null

            // Comprobamos si hay un archivo de metadatos
            if (configJson != null){

                // Leemos el contenido del jar
                val texto = jarFile.getInputStream(configJson).bufferedReader()

                // Convertimos el json a un objeto [MetaPlugin]
                try {
                    metadatosPlugin = Klaxon().parse<MetaPlugin>(texto)
                }catch (exception: KlaxonException){
                    metadatosPlugin = MetaPlugin()
                }
            }
        }
    }


    override fun onPluginTerminado(error: Exception?) {

        // Marcamos el deferred como completado
        if (completableDeferred != null){
            completableDeferred!!.complete(Unit)
        }

        // Cancelamos la coroutina
        job.cancelChildren()

        /**
         * Cambiamos el estado del plugin y propagamos la finalización
         * de la ejecucion del plugin a través del [resultadoEjecucionListener]
        */
        when {
            // Ejecutado correctamente
            error == null -> {

                // Cambiamos el estado del plugin
                estadoActual = EstadosPlugin.COMPLETADO

                // Propagamos la correcta ejecución del plugin
                if (resultadoEjecucionListener != null){
                    resultadoEjecucionListener!!.onEjecutadoCorrectamente(this)
                }
            }

            // Ocurrió un error
            else -> {

                // Cambiamos el estado del plugin
                estadoActual = EstadosPlugin.ERROR

                // Propagamos la correcta ejecución del plugin
                if (resultadoEjecucionListener != null){
                    resultadoEjecucionListener!!.onErrorEnEjecucion(this)
                }
            }
        }

        // Evitamos realizar más llamadas al plugin
        controlPluginListener = null
        resultadoEjecucionListener = null
        completableDeferred = null
    }
}