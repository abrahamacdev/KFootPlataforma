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
 * @param clasePrincipal: Clase que contiene el metodo de cargado y ejecucion del plugin
 * @param nombrePlugin: Nombre del plugin
 */
class Plugin (val jarFile: File, val clasePrincipal: Class<*>, var nombrePlugin: String = "Desconocido"): CoroutineScope{

    // Instancia de la clase principal del plugin
    private val obj = this.clasePrincipal.newInstance()

    // Tarea vinculada al Plugin
    val job = Job()

    // Estado actual del plugin
    private var estado = EstadosPlugin.INACTIVO

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

    override fun toString(): String {
        return "(Plugin) Id: $ID. Nombre: $nombrePlugin. Estado: $estado"
    }



    /**
     * Activamos el plugin para que comience a ejecutarse
     * en su propia coroutina
     */
    fun activar() {

        // Ejecutamos el plugin en la coroutina dedicada
        launch(coroutineContext){
            
        }
    }
}