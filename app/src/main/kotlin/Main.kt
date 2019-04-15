import Controlador.Excepciones.ComandoException
import Controlador.Office
import Controlador.Setup
import Modelo.Preferencias.Propiedades
import Utiles.Constantes
import com.natpryce.konfig.Key
import com.natpryce.konfig.booleanType
import com.natpryce.konfig.stringType
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import java.io.File
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

    // Realizamos las comprobaciones iniciales del programa
    Setup.realizarComprobaciones(args)

}