package Controlador.Office

import Controlador.Supervisor.Supervisor
import Modelo.Plugin.Plugin
import Utiles.Utils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile
import Vista.Main.IMainView
import lib.Plugin.IPlugin
import java.util.concurrent.atomic.AtomicLong

/**
 * Esta clase se encarga de toddo lo relacionado con los plugins
 * de la plataforma KScrap. Desde aquí cargaremos en memoria los
 * diferentes plugins y
 * @version 1.0
 * @author Abraham
 */
class Office: IOffice{

    private val supervisor: Supervisor = Supervisor.getInstance()

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

                    // Comprobamos si el jar es un plugin valido
                    if (comprobarPluginValido(jar)){

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
        }

        return hayPlugins
    }

    /**
     * Cargamos todos los plugins válidos en memoria
     * para su posterior ejecución
     *
     * @param onPluginCargadoListener: Callback por el que pasaremos el plugin recien cargado
     */
    override fun cargarPlugins(onPluginCargadoListener: IOffice.onPluginCargadoListener?){

        // Obtenemos todos los jars del directorio de plugins
        val observableJars = Utils.obtenerJarsDirPlugins()

        // Comprobamos que halla jars
        if (observableJars != null){

            val observer = object : Observer<File> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(jar: File) {

                    // Comprobamos que el plguin  cumpla con los requisitos
                    if (comprobarPluginValido(jar)){

                        // Cargamos el archivo .jar
                        val urlClassLoader: URLClassLoader = URLClassLoader.newInstance(arrayOf(URL("jar:file:${jar.absolutePath}!/")))

                        // Cargamos la primera clase válida que haya en el jar
                        val clase = urlClassLoader.loadClass("Main")

                        // Creamos un plugin con la ruta del jar y la clase principal
                        val plugin = Plugin(jar, clase)

                        // Pasamos el plugin recién cargado
                        if (onPluginCargadoListener != null){
                            onPluginCargadoListener.onPluginCargado(plugin)
                        }

                        // Añadimos el plugin a la lista de los que se ejecutaran
                        supervisor.anadirPlugin(plugin)
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            }

            // Cargamos todos los plugins validos
            observableJars.subscribe(observer)
        }
    }

    /**
     * Llamamos al supervisor para que ejecute todos
     * los plugins que se hallan cargado hasta el momendo
     */
    /*fun ejecutarPlugins(): Supervisor {
        supervisor.ejecutarPlugins()
        return supervisor
    }*/

    override fun ejecutarPlugin(id: AtomicLong, onResultadoInicioListener: IPlugin.onResultadoAccionListener?) {
        supervisor.ejecutarPlugin(id,onResultadoInicioListener)
    }

    /**
     * Comprobamos que el jar contenga los componentes necesarios
     * para ser un plugin válido
     *
     * @param jar: Archivo que presupuestamente es un plugin
     *
     * @return Boolean: Si el jar pasado es un plugin valido
     */
    private fun comprobarPluginValido(jar: File): Boolean {

        // Componentes necesarios para ser considerado como plugin
        var tieneArchConf = false
        var tieneClasPrinc = false

        // Obtenemos el caracter de la barra inclinada
        var barraInclinada = 47.toChar()

        // Secuencia de elementos que estan en la raíz del jar
        val itemsEnRaiz = JarFile(jar).entries().asSequence().filter {
            it.name.indexOf(barraInclinada) == -1
        }

        // Recorremos los elementos que estan en la raiz paea
        // comprobar que el plugin tiene los requisitos necesarios
        itemsEnRaiz.forEach {

            val spliteado = it.name.split(".")
            val extension = spliteado[spliteado.size - 1]

            when {

                // Es una clase
                extension.equals("class") -> {

                    if (esUnaClaseValida(it, jar)){
                        tieneClasPrinc = true
                    }
                }

                // Es un json
                extension.equals("json") -> {

                    // Comprobamos que el arrhivo se llame "config"
                    if (spliteado[0].equals("config")){
                        tieneArchConf = true
                    }
                }
            }
        }

        return tieneClasPrinc && tieneArchConf
    }

    /**
     * Comprobamos que la clase recibida por parametro
     * cumple con las necesidades
     *
     * @param jarEntry: Elemento del jar analizado
     * @param jarFile: Archivo jar que se esstá anlizando
     *
     * @return Boolean: Si la clase contiene los elementos necesarios
     */
    private fun esUnaClaseValida(jarEntry: JarEntry, jar: File): Boolean{

        val nomClase = jarEntry.name.split(".")[0]

        // Comprobamos que la clase se llame "MainView"
        if (nomClase.equals("Main")){

            // Cargamos la clase "Main" del plugin
            val classLoader = URLClassLoader.newInstance(arrayOf(URL("jar:file:${jar.absolutePath}!/")))
            val clase = classLoader.loadClass(nomClase)

            // Comprobemos que implemente la interfaz diseñada para la
            // clase principal de los plugins
            if (IPlugin::class.java in clase.interfaces){

                // Método de sincronización
                val metSinc = IPlugin::class.java.declaredMethods[0]

                val existeMetSinc = clase.declaredMethods.firstOrNull {
                    KFoot.Utils.sonElMismoMetodo(it,metSinc)
                }

                // Comprobamos que el método de sincronización existe en la clase
                // principal del plugin
                if (existeMetSinc != null){
                    return true
                }
            }
        }
        return false
    }
}