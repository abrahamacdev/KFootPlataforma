package Modelo

import Utiles.plus
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.*
import nonapi.io.github.classgraph.json.JSONDeserializer
import java.io.File
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
class Plugin (val jar: File, val clasePrincipal: Class<*>): CoroutineScope{

    // Instancia de la clase principal del plugin
    private val obj = this.clasePrincipal.newInstance()

    // Tarea vinculada al Plugin
    val job = Job()

    // Coroutina en la que se ejecutara la tarea
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    // Id del plugin para la sesion actual
    val ID: AtomicLong = Companion.ID

    // Nombre del plugin
    var nombrePlugin: String = "Desconocido"

    // Version del plugin
    var version: Double = -1.0

    init {
        cargarMetadatos()
    }

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
        return "(Plugin) Id: $ID. Nombre: $nombrePlugin"
    }



    /**
     * Activamos el plugin para que comience a ejecutarse
     * en su propia coroutina
     */
    fun activar() {

        // Ejecutamos el plugin en la coroutina dedicada
        launch(coroutineContext){
            println("Ejecutando plugin en ${Thread.currentThread().name}")
        }
    }

    /**
     * Obtenemos los metadatos del plugin
     */
    private fun cargarMetadatos(){

        val jarFile = JarFile(jar)

        // Cargamos el archivo json del jar
        val configJson = jarFile.getEntry("config.json") ?: null

        // Comprobamos si hay un archivo de metadatos
        if (configJson != null){

            // Leemos el contenido del jar
            val texto = jarFile.getInputStream(configJson).bufferedReader()

            // Convertimos el texto a un archivo json
            val json = Klaxon().parseJsonObject(texto)

            println(json)

        }
    }
}