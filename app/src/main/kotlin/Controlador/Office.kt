package Controlador

import Utiles.Constantes
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

class Office{

    /**
     * Comprobamos si hay plugins válidos en el directorio de plugins
     * establecido
     *
     * @return Boolean: Si se ha encontrado al menos 1 plugin válido
     */
    fun hayPluginsValidos(): Boolean{

        val observable: Observable<File>? = obtenerJarsDirPlugins()

        // Hay archivos .jar
        if (observable != null){

            var hayPlugins = false

            val observer = object : Observer<File> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(jar: File) {

                    JarFile(jar.absolutePath).stream()

                            // Comprobamos que el plugin/jar cumpla los patrones necesarios
                            // En caso afirmativo, se llamará a #onComplete()
                            .forEach {componente ->

                                // Comprobamos si hay una clase "Main" en la raíz del .jar
                                if (componente.name.matches(Regex("^[Mm]ain\\w*\\.class$"))){

                                    // Cargamos el archivo .jar
                                    val urlClassLoader: URLClassLoader = URLClassLoader.newInstance(arrayOf(URL("jar:file:${jar.absolutePath}!/")))

                                    // Cargamos la clase "Main" del .jar
                                    val plugin = urlClassLoader.loadClass(componente.name.split(".")[0])

                                    // TODO: Hay que comprobar que el tipo de dato de vuelta sea un "ConjuntoInmuebles"
                                    // Recorremos los métodos de la clase
                                    val cuenta = plugin.methods.forEach{metodo ->

                                        // TODO Revisar el nombre del método
                                        // Comprobamos que el nombre del método sea el necesario
                                        if (metodo.name.equals("obtenerOfertas")){
                                            hayPlugins = true
                                            onComplete()
                                        }
                                    }

                                }
                            }

                }

                override fun onError(e: Throwable) { throw e }
            }

            // Recorremos cada uno de los .jar
            observable.subscribe(observer)
            observer.onComplete()

            return hayPlugins

        }

        return false
    }

    /**
     * Obtenemos todos los .jar del directorio de plugins establecido
     *
     * @return Observable<File>?: Observable con los archivos que son .jar
     */
    fun obtenerJarsDirPlugins(): Observable<File>?{

        // Observable con todoo el contenido de un directorio
        val archivos: Observable<File> = File(Constantes.DIRECTORIO_PLUGINS).listFiles().toObservable()

        // Lista con todos los jars del directorio
        val jars: ArrayList<File> = ArrayList()

        // Filtramos por su extensión
        archivos.filter { it.isFile && it.extension.equals("jar")}
                .subscribe{
                    jars.add(it)
                }

        // Si '.jar's devolveremos el observable
        if (jars.size >= 0){
            return jars.toObservable()
        }

        return null
    }



}