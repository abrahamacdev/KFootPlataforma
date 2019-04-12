import Controlador.Office
import Utiles.Constantes
import com.sun.org.apache.xpath.internal.operations.Bool
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.yield
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * Desde aquí lanzaremos el "Office" u "Oficina" desde la que se cargaŕan
 * los diferente plugins para la obtención de los inmuebles de sus respectivas
 * páginas web.
 *
 * @author Abraham Álvarez
 * @since 1.0
 */
fun main(args: Array<String>){

    // Creamos la carpeta que contendrá los plugins
    

    // Comprobamos que halla argumentos
    if (args.size > 0){
        var argss = parsearComandos(args)

        // Creamos el observable con los datos pasados como argumento
        val observable: Observable<String> = Observable.create {
            for (s: String in argss){
                it.onNext(s)
            }
            it.onComplete()
        }


        // Observer de los parámetros pasados por comando
        val observer = getObserverPrincipal()


        /* Ejemplos de comandos válidos:
           --comando-1 valor
           -comando-2
           -comando_3 valor
        */
        // Comprobamos que sea un comando válido o un valor asociado
        observable.subscribe(observer)

        // Paramos de emitir elementos
        observer.onComplete()
    }
}

/**
 * Convertimos un array de supuestos comandos en una lista
 * de comandos con sus respectivos argumentos
 *
 * @param Array<String> args: Array con los datos pasados por terminal
 *
 * @return ArrayList<String> con los comandos y sus respectivos agurmentos
 */
fun parsearComandos(args: Array<String>): ArrayList<String>{

    val completos: ArrayList<String> = ArrayList()

    for (i in 0 until args.size){

        // Comprobamos si el elemento actual es un comando
        if (args[i].matches(Regex(Constantes.REG_COMANDO))){

            // Comprobamos que el siguiente elemento sea un argumento
            if (i + 1 < args.size){
                if (args[i+1].matches(Regex(Constantes.REG_VAL_COMANDO))){
                    completos.add((args[i] + " " + args[i+1]).trim()) //Concatenamos el comando con su argumento
                    continue
                }
            }

            // Comando sin argumento
            completos.add(args[i].trim())
        }
    }

    return completos
}

/**
 * Este {[Observer]} se encargará de aplicar los cambios
 * especificados por parámetros.
 *
 * @return Observer<String>. Observador que consumirá los comandos
 */
fun getObserverPrincipal(): Observer<String> {

    return object : Observer<String> {

        // Último comando escrito
        var ultComando: String? = null

        // Office que se encargará de cargar los plugins
        lateinit var office: Office

        override fun onComplete() {
            office = Office() // Cargamos los plugins
        }

        override fun onSubscribe(d: Disposable) {}

        override fun onNext(t: String) {

            // Es un comando sin argumentos
            aplicarCambios(t,this)
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }
    }
}

/**
 * Aplicamos los cambios a la ejecución del programa
 *
 * @param String args: Comando con su respectivo valor (si tiene)
 */
fun aplicarCambios(args: String, observer: Observer<String>){

    var comando: String
    var valor: String = ""

    // Es un comando con valor
    if (args.split(" ").size > 1){
        comando = args.split(Regex("\\s"))[0]
        valor = args.split(Regex("\\s"))[1]
    }

    // Solo es un comando
    else {
        comando = args
    }

    // Comprobamos que comando es el que se ha
    // pasado
    when {

        // Establecemos el directorio en el que se encuentra la
        // carpeta con los plugins
        comando.equals("-d") -> {

            val f: File = File(valor)

            // Comprobamos que sea un directorio y exista
            if (f.isDirectory && f.exists()){
                Constantes.DIRECTORY = valor
            }

            // Ocurrio un error al comprobar el directorio
            else{
                observer.onError(Exception("El comando -d necesita como parámetro un directorio válido"))
            }
        }

    }
}