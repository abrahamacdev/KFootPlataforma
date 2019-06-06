package Modelo

import Utiles.plus
import com.kscrap.libreria.Controlador.Transmisor
import com.kscrap.libreria.Modelo.Dominio.Inmueble
import com.kscrap.libreria.Modelo.Repositorio.ConfiguracionRepositorioInmueble
import com.kscrap.libreria.Modelo.Repositorio.RepositorioInmueble
import com.kscrap.libreria.Utiles.Constantes
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.File
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Esta clase recoge todos los datos necesarios para poder lanzar un plugin.
 * Cada vez que se vaya a ejecutar un plugin, se creara un nuevo objeto de este tipo
 * que contenta entre otras cosas:
 * @param jarFile: Archivo jar con el codigo del plugin a ejecutar
 * @param metodoCargado: Este metodo de cargado se utilizara para obtener algun objeto que nos permita pasar informacion del plugin a la plataforma
 * @param metodoEjecucion: Este metodo se llamara para comenzar la ejecucion del plugin
 * @param clasePrincipal: Clase que contiene el metodo de cargado y ejecucion del plugin
 * @param nombrePlugin: Nombre del plugin
 */
class Plugin (val jarFile: File, val metodoCargado: Method?, val metodoEjecucion: Method, val clasePrincipal: Class<*>, val nombrePlugin: String = "Desconocido"){

    // Instancia de la clase principal del plugin
    private val obj = this.clasePrincipal.newInstance()

    // Transmisor que utiliza el plugin para transmitir la informacion
    private var transmisor: Transmisor<Inmueble>? = null

    // RepositorioInmueble en el que iremos guardando los datos que se scrapeen
    private val repositorioInmueble = crearRepositorioInmuebles()

    // Nos servirá para conocer si un plugin terminó de enviar datos
    private var transmisionCompletada = false

    init {
        Companion.ID += 1
    }

    // Id del plugin para la sesion actual
    val ID: AtomicLong = Companion.ID

    companion object {

        // Nos servirá para identificar a cada plugin unequívocamente
        private var ID: AtomicLong = AtomicLong(0)
    }

    override fun equals(o: Any?): Boolean {
        if (o == null) return false
        if (this.javaClass != o.javaClass) return false
        if (this === o) return true
        val plugin = o as Plugin
        return ID === plugin.ID
    }

    /**
     * Creamos el {[RepositorioInmueble]} en el que
     * se almacenará la información que se scrapee del
     * plugin
     *
     * @return Respositorio<Inmueble> con los datos que se scrapeen
     */
    private fun crearRepositorioInmuebles(): RepositorioInmueble<Inmueble>{
        // TODO Crear un {[repositorioInmueble ]} con una configuración más exhaustiva
        val configuracion = ConfiguracionRepositorioInmueble()
        with(configuracion){
            guardaCada(5, TimeUnit.SECONDS)
            guardaLosDatosEn("/home/abraham/Documentos/")
            archivoConNombre("Prueba")
            archivoConExtension(Constantes.EXTENSIONES_ARCHIVOS.CSV)
        }

        return RepositorioInmueble.create<Inmueble>(propiedades = configuracion)
    }

    /**
     * Activamos el plugin para que comience a ejecutarse
     * en una corutina
     */
    fun activar() {

        println(Thread.currentThread().name)

        val transmisor = this.metodoCargado!!.invoke(obj) as Transmisor<Inmueble>    // Obtenemos el transmisor del plugin

        transmisor!!.subscribirse(object : Subscriber<Inmueble> {

            var subscripcion: Subscription? = null

            override fun onComplete() {

                transmisionCompletada = true                    // El plugin ha terminado de ejecutarse
                val sujeto = repositorioInmueble.guardar()      // Guardamos los datos

                // Comprobamos que se esten guardando los datos
                if (sujeto != null){}
            }

            override fun onSubscribe(s: Subscription?) {
                subscripcion = s

                if (s != null){
                    s!!.request(1)
                }
            }

            override fun onNext(t: Inmueble?) {

                repositorioInmueble.anadirInmueble(t!!)

                if (subscripcion != null){
                    subscripcion!!.request(1)
                }
            }

            override fun onError(t: Throwable?) {}
        })

        // Guardamos el transmisor
        setTransmisor(transmisor)

        // Comenzamos a recibir inmuebles
        metodoEjecucion.invoke(obj)
    }

    /**
     * Comprobamos si el plugin actual a terminado
     * de ejecutarse
     */
    fun haTerminado():Boolean{
        return repositorioInmueble.todoGuardado() && transmisionCompletada
    }

    fun setTransmisor(transmisor: Transmisor<Inmueble>){
        if(this.transmisor == null){
            this.transmisor = transmisor
        }
    }

}