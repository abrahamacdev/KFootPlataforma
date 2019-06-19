package Controlador.Office

import Controlador.Supervisor
import Modelo.Plugin
import Utiles.Constantes
import Utiles.Utils
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
            // Esperamos 3 segundos antes de cerrar la aplicacion
            Observable.timer(3, TimeUnit.SECONDS).subscribe({},{},{
                System.exit(0)
            })
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
        val jarsObservable: Observable<File>? = Utils.obtenerJarsDirPlugins()

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
        val observableJars = Utils.obtenerJarsDirPlugins()

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

                        // Creamos un plugin con la ruta del jar y la clase principal
                        val plugin = Plugin(jar, clase)
                        Supervisor.instancia.anadirPlugin(plugin)
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