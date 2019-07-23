package Utiles

import Modelo.Preferencias
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import javafx.scene.paint.Color
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

object Utils {

    /**
     * Comprobamos cual es el separador que hay que
     * utilizar para las rutas del SO del cliente
     *
     * @return String: Separador que hay que utilizar en el SO
     */
    /*fun determinarSeparador(): String {

        var separador = ""

        // Recuperamos el SO que se esta ejecuutando en el cliente
        val SO = Utils.determinarSistemaOperativo()

        when {
            // Windows
            SO == com.kscrap.libreria.Utiles.Constantes.SO.WINDOWS -> {separador = "\\"}

            // Ubuntu
            SO == com.kscrap.libreria.Utiles.Constantes.SO.UBUNTU -> {separador = "/"}
        }

        return separador
    }*/

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

enum class POSICION{
    DERECHA,
    IZQUIERDA
}