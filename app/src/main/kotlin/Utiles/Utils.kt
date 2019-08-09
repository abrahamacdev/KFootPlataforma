package Utiles

import Controlador.UI.Main.MainController
import KFoot.Utils
import Modelo.Preferencias
import Vista.Main.MainView
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.DirectoryChooser
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

object Utils {

    /**
     * Obtenemos todos los .jar del directorio de plugins establecido
     *
     * @return Observable<File>?: Observable con los archivos que son .jar
     */
    fun obtenerJarsDirPlugins(): Observable<File>?{

        // Observable con todoo el contenido de un directorio
        val archivos: Observable<File> = File(Preferencias.obtener(Constantes.RUTA_PLUGINS_KEY).toString()).listFiles().toObservable()

        println("Obteniendo de ${Preferencias.obtener(Constantes.RUTA_PLUGINS_KEY).toString()}")

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
     * Obtenemos un color aleatorio entre los dos especificados
     * por parámetros
     *
     * @param color1: Primer color del gradiente
     * @param color2: Segundo color del gradiente
     *
     * @return Color: Color que retornaremos
     */
    fun colorRandomEntre(color1: Colar, color2: Colar): Color{

        val max = 100
        val min = 0

        var valido = false
        var randomStep = -1
        var randomSub = -1

        // Generamos dos valores aleatorios para #randomStep y #randomSub.
        // Siempre #randomSub tiene que ser menor o igual que #randomStep y en ningún momento
        // #randomStep puede valer 0
        while (!valido){
            randomStep = Random.nextInt((max - min) + 1)
            randomSub = Random.nextInt((randomStep - min) + 1)
            while (randomSub > randomStep){
                randomSub = Random.nextInt((randomStep - min) + 1)
            }

            if (randomStep > 0){
                valido = true
            }
        }

        return Color.rgb(
                (color1.r * (randomStep - randomSub) + color2.r * randomSub) / randomStep,
                (color1.g * (randomStep - randomSub) + color2.g * randomSub) / randomStep,
                (color1.b * (randomStep - randomSub) + color2.b * randomSub) / randomStep
        )
    }

    /**
     * Retornamos dos colores aleatorios de la paleta de colores
     * @see [Constantes.PALETA_COLORES]
     *
     * @return Pair<Color,Color>: Dos pares de colores aleatorios
     */
    fun coloresRandomPaleta(): Pair<java.awt.Color,java.awt.Color>{

        // Creamos una lista de valores enteros que van desde 0 hasta el número de colores de nuestra paleta
        val numeros: ArrayList<Int> = ArrayList(IntRange(0, Paleta_Colores.values().size-1).toMutableList())

        // Obtenemos dos valores aleatorios de nuestra lista anterior
        val primero = numeros.get(Random.nextInt(numeros.size))
        numeros.remove(primero)
        val segundo = numeros.get(Random.nextInt(numeros.size))

        // Creamos un par que contiene dos colores recogidos de forma aleatoria
        // de nuestra paleta
        return Pair(java.awt.Color.decode(Paleta_Colores.values().get(primero).value),java.awt.Color.decode(Paleta_Colores.values().get(segundo).value))
    }

    /**
     * Ajustamos la sensibilidad a la hora de hacer scroll
     * vertical sobre [scrollPane]
     *
     * @param scrollPane: ScrollPane a modificar
     * @param factor: Entero que ayudará a elevar o minimizar la sensibilidad
     */
    fun cambiarSensibilidadScroll(scrollPane: ScrollPane, factor: Int){
        scrollPane.content.setOnScroll {
            val deltaY = it.getDeltaY() * factor
            val width = scrollPane.content.getBoundsInLocal().getWidth()
            val vvalue = scrollPane.vvalue
            scrollPane.setVvalue(vvalue + -deltaY / width) }
    }

    /**
     * Buscamos a un nodo por su id, este debe de coincidir
     * con el [id] recibido por parámetros
     *
     * @param id: Id del nodo a buscar
     * @param padre: Pane en el que buscaremos
     */
    fun buscarNodoPorId(id: String, padre: Pane): Node?{

        padre.children.forEach {

            if (it.id != null){

                if (it.id.equals(id)){
                    return it
                }
            }

            when {
                it is Pane -> return buscarNodoPorId(id,it)
            }

        }
        return null
    }

    /**
     * Buscamos a un nodo por su id, este debe de encontrarse
     * en la [listaIds] proporcionada. La busqueda se hará a partir
     * del nodo [padre].
     *
     * @param id: Id del nodo a buscar
     * @param padre: Pane en el que buscaremos
     */
    fun buscarNodosPorId(listaIds: List<String>, padre: Pane): ArrayList<Node>{

        val nodos = ArrayList<Node>()

        fun buscar(padre: Pane, nodos: ArrayList<Node>){

            padre.children.forEach {

                if (it.id != null){

                    if (it.id in listaIds){
                        nodos.add(it)
                    }
                }

                when {
                    it is Pane -> return buscar(it,nodos)
                }
            }
        }

        buscar(padre, nodos)
        return nodos
    }

    /**
     * Buscamos todos los elementos presentes en el [padre] que
     * sean del mismo tipo que [tipo]
     *
     * @param tipo: Tipo que tiene que tener el control
     * @param padre: Pane sobre el que se buscarán los controles
     *
     * @return ArrayList<Node>: Lista con todos los nodos del tipo solicitado
     */
    fun buscarNodosPorTipo(tipo: Class<*>, padre: Pane): ArrayList<Node>{

        val nodos = ArrayList<Node>()

        fun buscar(tipo: Class<*>, padre: Pane, nodos: ArrayList<Node>): ArrayList<Node>{

            // Recorremos los elementos del padre
            padre.children.forEach {

                // Comprobamos si la clase del elemento es del tipo solicitado y
                // la añadimos a la lista
                //if (it.javaClass == tipo || Utils.esSubclase(it.javaClass, tipo)){
                if (it.javaClass == tipo){
                    nodos.add(it)
                }

                // Si el elemento es un pane, recorremos sus elementos hijos
                when {
                    it is Pane -> buscar(tipo,it,nodos)
                }
            }

            return nodos
        }

        return buscar(tipo,padre,nodos)
    }

    /**
     * Mostramos un selector de directorios
     */
    fun mostrarSelectorDirectorio(): File?{
        return DirectoryChooser().showDialog(MainView.getEtapa())
    }

}

// Permite la suma de dos números atómicos
operator fun AtomicLong.plus (otro: AtomicLong): AtomicLong = AtomicLong(this.get() + otro.get())
operator fun AtomicLong.plus (otro: Long): AtomicLong = AtomicLong(this.get() + otro)

// Compara si dos colores son diferente
fun java.awt.Color.esDiferenteDe(color: java.awt.Color?): Boolean {
    if (color == null){
        return true
    }
    return this.red != color.red && this.blue != color.blue && this.green != color.green
}


data class Colar(val r: Int,
                 val g: Int,
                 val b: Int) {
    constructor(color: Color): this(color.red.toInt(),color.green.toInt(),color.blue.toInt())
}