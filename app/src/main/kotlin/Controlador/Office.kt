package Controlador

import Modelo.Plugin
import Utiles.Constantes
import com.kscrap.libreria.Controlador.Transmisor
import com.kscrap.libreria.Modelo.Dominio.Inmueble
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope
import java.io.File
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.sql.Time
import java.util.concurrent.TimeUnit
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Esta clase se encarga de toddo lo relacionado con los plugins
 * de la plataforma KScrap. Desde aquí cargaremos en memoria los
 * diferentes plugins y
 * @version 1.0
 * @author Abraham
 */
class Office{

    // Lista de plugins que se ejecutarán posteriormente
    private var plugins: ArrayList<Plugin> = ArrayList()


    companion object {

        // Lista de métodos que podrá tener un plugin para ser ejecutado
        private val METODOS_EJECUCION: HashMap<String,String> = HashMap(mapOf("obtenerOfertas" to Void.TYPE.canonicalName))

        // Lista de métodos que tendrá que tener un plugin para ser cargado
        private val METODOS_CARGADO: HashMap<String,String> = HashMap(
                // obtenerTransmisor: Transmisor<Inmueble>
                mapOf("obtenerTransmisor" to Transmisor.crear<Inmueble>().javaClass.canonicalName)
        )

        // Instancia del [Office]
        val instancia: Office = Office();

        /**
         * Comprobamos que la plataforma se cierre correctamente,
         * ejecutando las acciones oportunas para asegurar la integridad
         */
        @JvmStatic
        @Synchronized
        suspend fun cerrarAplicacion(forzado: Boolean = false){

            // Comprobamos si todos los plugins han terminado de ejecutarse
            if (!Supervisor.instancia.comprobarTodosPluginsFinalizados()){

                // EL cierre sera forzado
                if (forzado){

                    // Forzamos que los plugins finalicen su ejecucion
                    Supervisor.instancia.forzarFinEjecucionPlugins()

                    // Esperamos 3 segundos antes de cerrar la aplicacion
                    Observable.timer(3, TimeUnit.SECONDS).subscribe({},{},{
                        System.exit(0)
                    })

                }

                // Esperamos y luego forzamos el cierre
                else {

                    Supervisor.instancia.esperarFinalizacionPlugins()

                    System.exit(0)
                }
            }
        }
    }

    private constructor()



    /**
     * Comprobamos si hay plugins válidos en el directorio de plugins
     * establecido.
     *
     * @return Boolean: Si se ha encontrado al menos 1 plugin válido
     */
    fun hayPluginsValidos(): Boolean{

        val jarsObservable: Observable<File>? = obtenerJarsDirPlugins()

        var hayPlugins = false

        // Hay archivos .jar
        if (jarsObservable != null){

            val observer = object : Observer<File> {

                private lateinit var subcripcion: Disposable;

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {
                    this.subcripcion = d
                }

                override fun onNext(jar: File) {

                    // Recorremos cada uno de los elementos del jar
                    val claseValida = JarFile(jar.absolutePath).stream()
                            .filter{
                                comprobarClaseValida(it)
                            }
                            .findFirst()

                    // Si hay una clase válida, comprobamos que tenga los métodos necesarios
                    if (claseValida.isPresent){

                        // Cargamos el archivo .jar
                        val urlClassLoader: URLClassLoader = URLClassLoader.newInstance(arrayOf(URL("jar:file:${jar.absolutePath}!/")))

                        // Cargamos la clase del .jar
                        val plugin = urlClassLoader.loadClass(claseValida.get().name.split(".")[0])

                        var cargadoValido = false
                        var ejecucionValida = false
                        // Comprobamos que tenga alguno de los métodos de ejecución
                        // y cargado neceasarios
                        val esValida = plugin.declaredMethods
                                .filter{
                                    if (comprobarMetodoCargadoValido(it,plugin)){
                                        cargadoValido = true
                                    }

                                    if (comprobarMetodoEjecucionValido(it)){
                                        ejecucionValida = true
                                    }

                                    cargadoValido && ejecucionValida
                                }
                                .firstOrNull()

                        // Comprobamos que la clase halla pasado los
                        // filtros necesarios
                        if (esValida != null){
                            hayPlugins = true

                            // Evitamos seguir comprobando jars
                            if (!this.subcripcion.isDisposed){
                                this.subcripcion.dispose()
                            }
                        }
                    }
                }

                override fun onError(e: Throwable) { throw e }
            }

            // Recorremos cada uno de los .jar
            jarsObservable.subscribe(observer)
            observer.onComplete()
        }

        return hayPlugins
    }

    /**
     * Cargamos todos los plugins válidos en memoria
     * para su posterior ejecución
     */
    fun cargarPlugins(){

        val observableJars = obtenerJarsDirPlugins()

        // Comprobamos que halla jars
        if (observableJars != null){


            val observer = object : Observer<File> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(jar: File) {

                    // Recorremos cada uno de los elementos del jar
                    val claseValida = JarFile(jar.absolutePath).stream()
                            .filter{
                                comprobarClaseValida(it)
                            }
                            .findFirst()

                    // Si hay una clase válida, comprobamos que tenga los métodos necesarios
                    if (claseValida.isPresent){

                        // Cargamos el archivo .jar
                        val urlClassLoader: URLClassLoader = URLClassLoader.newInstance(arrayOf(URL("jar:file:${jar.absolutePath}!/")))

                        // Cargamos la primera clase válida que halla en el jar
                        val clase = urlClassLoader.loadClass(claseValida.get().name.split(".")[0])

                        val metodoCargado = buscarPrimerMetodoCargado(clase)
                        val metodoEjecucion = buscarPrimerMetodoEjecucion(clase)

                        // Hay métodos válidos en la clase
                        if (metodoCargado != null && metodoEjecucion != null){

                            val plugin = Plugin(jar, metodoCargado, metodoEjecucion, clase)
                            plugins.add(plugin)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            }

            observableJars.subscribe(observer)
            observer.onComplete()

        }
    }

    /**
     * Pasamos la lista de plugins al supervisor
     * y le pedimos que los ejecute
     */
    suspend fun ejecutarPlugins(){

        with(Supervisor.instancia!!){
            anadirListaPlugin(plugins)
            lanzarPlugins()
        }
    }



    /**
     * Obtenemos todos los .jar del directorio de plugins establecido
     *
     * @return Observable<File>?: Observable con los archivos que son .jar
     */
    private fun obtenerJarsDirPlugins(): Observable<File>?{

        // Observable con todoo el contenido de un directorio
        val archivos: Observable<File> = File(Constantes.DIRECTORIO_PLUGINS).listFiles().toObservable()

        // Lista con todos los jars del directorio
        val jars: ArrayList<File> = ArrayList()

        // Filtramos por su extensión
        archivos.filter { it.isFile && it.extension.equals("jar")}
                .subscribe{
                    jars.add(it)
                }

        // Si hay '.jar's devolveremos el observable
        if (jars.size >= 0){
            return jars.toObservable()
        }

        return null
    }

    /**
     * Comprobamos que el componente del jar sea una clase válida
     *
     * @param componente: ELemento del jar a revisar
     *
     * @return Boolean: SI la clase es válida
     */
    private fun comprobarClaseValida(componente: JarEntry): Boolean{

        val reg = Regex("^[Mm]ain.class$")
        val enRaiz = componente.name.indexOf(47.toChar()) == -1

        return componente.name.matches(reg) && enRaiz
    }

    /**
     * Comprobamos el método de cargado existente
     * en el plugin
     *
     * @param clazz: Clase en la que buscaremos los métodos de cargado
     *
     * @return Method?: Método de cargado existente en la clase
     */
    private fun buscarPrimerMetodoCargado(clazz: Class<*>): Method? {

        for(metodo in clazz.declaredMethods){
            if (comprobarMetodoCargadoValido(metodo, clazz)){
                return metodo
            }
        }
        return null
    }

    /**
     * Comprobamos si el método pasado por parámetro es válido para la etapa
     * de cargado del plugin
     *
     * @param metodo: Método a revisar
     * @param clasePrincipal: Clase principal que contiene el método de cargado
     *
     * @return Si el método es válido
     */
    private fun comprobarMetodoCargadoValido(metodo: Method, clasePrincipal: Class<*>): Boolean{

        // Comprobamos que el nombre del método sea válido
        if (METODOS_CARGADO.containsKey(metodo.name)){

            val value = METODOS_CARGADO.get(metodo.name) ?: ""

            var clase = clasePrincipal.newInstance()
            val retornoMetodo = metodo.returnType

            when {

                // El método de cargado del plugin es mediante
                // un {[Transmisor]}
                metodo.name.equals("obtenerTransmisor") && retornoMetodo.name.equals(value)-> {

                    // Obtenemos el transmisor
                    var transmisor = clasePrincipal.getDeclaredMethod("obtenerTransmisor").invoke(clase)
                    var classTransmisor = transmisor.javaClass

                    // Obtenemos el tipo de dato que almacena el transmisor
                    var tipoRetorno = classTransmisor.getDeclaredMethod("getTipoTransmisor").invoke(transmisor) as Class<*>

                    // Si el tipo de dato extiende de {[Inmueble]}, será válido
                    if (tipoRetorno.superclass.canonicalName.equals(Inmueble().javaClass.canonicalName) || tipoRetorno.canonicalName.equals(Inmueble().javaClass.canonicalName)){
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * Comprobamos el método de ejecucion existente
     * en el plugin
     *
     * @param clazz: Clase en la que buscaremos los métodos de ejecución
     *
     * @return Method?: Método de cargado existente en la clase
     */
    private fun buscarPrimerMetodoEjecucion(clazz: Class<*>): Method? {

        for(metodo in clazz.declaredMethods){
            if (comprobarMetodoEjecucionValido(metodo)){
                return metodo
            }
        }
        return null
    }

    /**
     * Comprobamos si el método pasado por parámetro es válido para la etapa
     * de ejecución del plugin
     *
     * @param metodo: Método a revisar
     *
     * @return Si el método es válido
     */
    private fun comprobarMetodoEjecucionValido(metodo: Method): Boolean{

        if (METODOS_EJECUCION.containsKey(metodo.name)){

            val value = METODOS_EJECUCION.get(metodo.name)

            if (value != null && value.equals(metodo.genericReturnType.typeName)){
                return true
            }
        }
        return false
    }
}