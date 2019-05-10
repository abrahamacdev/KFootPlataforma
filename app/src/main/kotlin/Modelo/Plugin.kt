package Modelo

import Utiles.plus
import com.kscrap.libreria.Controlador.Transmisor
import com.kscrap.libreria.Modelo.Dominio.Inmueble
import com.kscrap.libreria.Modelo.Repositorio.ConfiguracionRepositorioInmueble
import com.kscrap.libreria.Modelo.Repositorio.RepositorioInmueble
import com.kscrap.libreria.Utiles.Constantes
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.File
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class Plugin (val jarFile: File, val metodoCargado: Method, val metodoEjecucion: Method, val clasePrincipal: Class<*>, val nombrePlugin: String = "Desconocido"){

    // Instancia de la clase principal del plugin
    private val obj = this.clasePrincipal.newInstance()

    // Transmisor que utiliza el plugin
    private var transmisor: Transmisor<Inmueble>? = null

    // DataFrame que contiene la información que se va scrapeando
    private val dataframe = crearDataframe()

    // Nos servirá para conocer si un plugin terminó de enviar datos
    private var transmisionCompletada = false

    init {
        Companion.ID += 1
    }

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
    private fun crearDataframe(): RepositorioInmueble<Inmueble>{
        // TODO Crear un dataframe con una configuración más exhaustiva
        val configuracion = ConfiguracionRepositorioInmueble()
        with(configuracion){
            guardaCada(1, TimeUnit.MINUTES)
            guardaLosDatosEn("/home/admin/Documentos/")
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

        val transmisor = this.metodoCargado.invoke(obj) as Transmisor<Inmueble>    // Obtenemos el transmisor del plugin

        transmisor!!.subscribirse(object : Subscriber<Inmueble> {

            var subscripcion: Subscription? = null

            override fun onComplete() {
                transmisionCompletada = true
            }

            override fun onSubscribe(s: Subscription?) {
                subscripcion = s

                if (s != null){
                    s!!.request(1)
                }
            }

            override fun onNext(t: Inmueble?) {
                dataframe.anadirInmueble(t!!)
                dataframe.guardar()

                if (subscripcion != null){
                    subscripcion!!.request(1)
                }
            }

            override fun onError(t: Throwable?) {}
        })

        metodoEjecucion.invoke(obj)

        setTransmisor(transmisor)
    }

    /**
     * Comprobamos si el plugin actual a terminado
     * de ejecutarse
     */
    fun haTerminado():Boolean{
        return dataframe.todoGuardado() && transmisionCompletada
    }

    fun setTransmisor(transmisor: Transmisor<Inmueble>){
        if(this.transmisor == null){
            this.transmisor = transmisor
        }
    }

}