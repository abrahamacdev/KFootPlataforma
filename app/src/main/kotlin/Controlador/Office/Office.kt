package Controlador.Office

import Controlador.Supervisor
import Modelo.Plugin
import Utiles.Constantes
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.TimeUnit
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.collections.ArrayList

/**
 * Esta clase se encarga de toddo lo relacionado con los plugins
 * de la plataforma KScrap. Desde aquí cargaremos en memoria los
 * diferentes plugins y
 * @version 1.0
 * @author Abraham
 */
class Office: IOffice{

    // Lista de plugins que se ejecutarán posteriormente
    private var plugins: ArrayList<Plugin> = ArrayList()

    companion object {

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
     * Comprobamos que halla plugins validos en el directorio
     * de plugins
     *
     * @return Boolean: Si se ha encontrado al menos 1 plugin válido
     */
    override fun existenPlugins(): Boolean {
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
                        //val urlClassLoader: URLClassLoader = URLClassLoader.newInstance(arrayOf(URL("jar:file:${jar.absolutePath}!/")))

                        // Cargamos la clase del .jar
                        //val plugin = urlClassLoader.loadClass(claseValida.get().name.split(".")[0])

                        // Establecemos la existencia de plugins
                        hayPlugins = true

                        // Evitamos seguir comprobando jars
                        if (!this.subcripcion.isDisposed){
                            this.subcripcion.dispose()
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
    override fun cargarPlugins(){

        // Obtenemos todos los jars del directorio de plugins
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

                        // Cargamos la primera clase válida que haya en el jar
                        val clase = urlClassLoader.loadClass(claseValida.get().name.split(".")[0])

                        // Creamos un plugin con la ruta del jar
                        val plugin = Plugin(jar, clase)
                        plugins.add(plugin)
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            }

            // Cargamos todos los plugins validos
            observableJars.subscribe(observer)

            // Terminamos de transmitir jars
            observer.onComplete()
        }
    }

    /**
     * Ejecutamos cada uno de los plugins que se
     * hayan cargado en memoria
     */
    override fun ejecutarPlugins(){



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

        // Comprobamos que el componente sea una clase con nombre "[Mm]ain"
        val reg = Regex("^[Mm]ain.class$")

        // Comprobamos que la clase se encuentre en la raíz del jar
        val enRaiz = componente.name.indexOf(47.toChar()) == -1

        return componente.name.matches(reg) && enRaiz
    }
}