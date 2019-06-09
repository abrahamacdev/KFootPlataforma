package Modelo

import Utiles.plus
import com.kscrap.libreria.Controlador.Transmisor
import com.kscrap.libreria.Modelo.Dominio.Inmueble
import com.kscrap.libreria.Modelo.Repositorio.ConfiguracionRepositorioInmueble
import com.kscrap.libreria.Modelo.Repositorio.RepositorioInmueble
import com.kscrap.libreria.Utiles.Constantes
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.File
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext

/**
 * Esta clase se encarga de ejecutar cada uno de los plugins que se encuentren disponibles.
 * La ejecucion del plugin se realizara en una coroutina dedicada para las
 * tareas CPU-Intensivas
 *
 * @param jarFile: Archivo jar con el codigo del plugin a ejecutar
 * @param metodoCargado: Este metodo de cargado se utilizara para obtener algun objeto que nos permita pasar informacion del plugin a la plataforma
 * @param metodoEjecucion: Este metodo se llamara para comenzar la ejecucion del plugin
 * @param clasePrincipal: Clase que contiene el metodo de cargado y ejecucion del plugin
 * @param nombrePlugin: Nombre del plugin
 */
class Plugin (val jarFile: File, val metodoCargado: Method?, val metodoEjecucion: Method, val clasePrincipal: Class<*>, val nombrePlugin: String = "Desconocido"): CoroutineScope{

    // Instancia de la clase principal del plugin
    private val obj = this.clasePrincipal.newInstance()

    // RepositorioInmueble en el que iremos guardando los datos que se scrapeen
    private val repositorioInmueble: RepositorioInmueble<Inmueble> by lazy {
        RepositorioInmueble.create<Inmueble>(propiedades = configuracion)
    }

    // Configuracion que utilizara el repositorioInmueble
    private val configuracion: ConfiguracionRepositorioInmueble by lazy {
        cargarConfiguracionRepInmuble()
    }

    // Nos servirá para conocer si un plugin terminó de enviar datos
    private var transmisionCompletada = false

    // Tarea vinculada al Plugin
    val job = Job()

    // Estado actual del plugin
    private var estado = EstadosPlugin.INACTIVO

    // Transmisor que usaremos para recibir los datos del plugin
    lateinit var transmisor: Transmisor<Inmueble>

    // Coroutina en la que se ejecutara la tarea
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    // Id del plugin para la sesion actual
    val ID: AtomicLong = Companion.ID

    companion object {

        // Nos servirá para identificar a cada plugin unequívocamente
        private var ID: AtomicLong = AtomicLong(0)

        // Actualizamos el valor del ID
        get() {
            val temp = field
            field+=1
            return temp
        }

    }

    override fun equals(o: Any?): Boolean {
        if (o == null) return false
        if (this.javaClass != o.javaClass) return false
        if (this === o) return true
        val plugin = o as Plugin
        return ID === plugin.ID
    }



    /**
     * Cargamos la configuracion que se utilizara para el
     * repositorioInmueble
     *
     * @return ConfiguracionRepositorioInmueble: COnfiguracion que estableceremos para el [repositorioInmueble]
     */
    fun cargarConfiguracionRepInmuble(): ConfiguracionRepositorioInmueble{
        val configuracion = ConfiguracionRepositorioInmueble()
        with(configuracion){
            guardaLosDatosEn("/home/abraham/Documentos/")
            archivoConExtension(Constantes.EXTENSIONES_ARCHIVOS.csv)
        }
        return configuracion
    }

    /**
     * Retornamos la direccion absoluta del archivo en
     * el que se guardara la informacion que se vaya scrapean
     *
     * @return File: Archivo en el que se guardaran lo datos
     */
    fun obtenerRutaArchivoGuardado(): File{

        val archivo: File = with(configuracion){
            File(getRutaGuardadoArchivos() + "/" + getNombreArchivo() + "." + getExtensionArchivo().name)
        }

        return archivo
    }

    /**
     * Establecemos la nueva ruta del archivo en el que se
     * guardaran los datos
     *
     * @param nuevaRuta: Nueva ruta del archivo
     */
    fun establecerNuevaRutaArchivo(nuevaRuta: String){
        configuracion.establecerRutaArchivo(nuevaRuta)
    }


    /**
     * Activamos el plugin para que comience a ejecutarse
     * en su propia coroutina
     */
    fun activar() {

        // Ejecutamos el plugin en la coroutina dedicada
        launch(coroutineContext){

            // Obtenemos el transmisor del plugin
            this@Plugin.transmisor = this@Plugin.metodoCargado!!.invoke(obj) as Transmisor<Inmueble>

            // Nos conectamos al transmisor del plugin para comenzar a recibir los datos
            this@Plugin.transmisor!!.subscribirse(object : Subscriber<Inmueble> {

                var subscripcion: Subscription? = null

                override fun onComplete() {

                    // El plugin ha terminado de ejecutarse, no se recibiran mas datos
                    this@Plugin.transmisionCompletada = true

                    // Cuando hallan terminado de guardarse los datos,
                    // terminaremos la coroutina
                    val sujeto = PublishSubject.create<Nothing>()
                    sujeto.subscribe({},{},{
                        estado = EstadosPlugin.COMPLETADO   // Cambiamos el estado del plugin
                        job.cancel()                        // Cancelamos la coroutina en la que se esta ejecutando el plugin
                    })

                    // Ejecutamos el guardado dentro de la coroutina
                    this@launch.launch {
                        this@Plugin.repositorioInmueble.guardar(sujeto)
                    }
                }

                override fun onSubscribe(s: Subscription?) {

                    // COmenzamos a solicitar datos
                    if (s != null){

                        // Cambiamos el estado del plugin a activo
                        estado = EstadosPlugin.ACTIVO

                        // Guardamos la subscripcion
                        subscripcion = s
                        s!!.request(1)
                    }

                    else {
                        // Cambiamos el estado del plugin a inactivo
                        estado = EstadosPlugin.INACTIVO
                    }
                }

                override fun onNext(t: Inmueble?) {

                    // Guardamos el inmueble en el repositorio
                    repositorioInmueble.anadirInmueble(t!!)

                    // Seguimos solicitando mas viviendas
                    if (subscripcion != null){
                        subscripcion!!.request(1)
                    }
                }

                override fun onError(t: Throwable?) {}
            })

            // Comenzamos a recibir inmuebles
            metodoEjecucion.invoke(obj)
        }
    }

    /**
     * Comprobamos si el plugin actual a terminado
     * de ejecutarse
     */
    fun haTerminado():Boolean{

        // Todos los datos han sido obtenidos y guardados
        if (estado == EstadosPlugin.COMPLETADO){
            return true
        }

        else {

            // Los datos han sido obtenidos y guardados pero no se ha actualizado el
            // estado del plugin
            if (repositorioInmueble.todoGuardado() && transmisionCompletada){
                estado == EstadosPlugin.COMPLETADO

                return true
            }

            return false
        }
    }

    /**
     * Forzamos el fin de la ejecucion del
     * plugin actual
     */
    fun forzarAcabadoPlugin(){

        // Forzamos la llamada al metodo onComplete del transmisor
        // para que guarde los datos que se hallan scrapeado hasta el momento
        this.transmisor.terminarEnvio()
    }

}