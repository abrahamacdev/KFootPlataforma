package Utiles

import java.security.SecureRandom
import kotlin.random.Random

//private val PALETA_COLORES: Array<Color> = arrayOf(Color.AZUL_1,Color.AMARILLO_1,Color.NARANJA_1,Color.ROSA_1,Color.PURPURA_1, Color.CORAL_1)

enum class Color(val value: String, val grupo: Int) {
    AZUL_1("#47B39D",1),
    AZUL_2("#28878E",1),
    AZUL_3("#319D98",1),
    //AZUL_4("#2A7180",1),
    AZUL_5("#2E5C6D",1),
    //PURPURA_1("#462446",2),
    PURPURA_2("#976F95",2),
    //PURPURA_3("#6D486C",2),
    PURPURA_4("#C299C0",2),
    //PURPURA_5("#EFC4ED", 2),
    //ROSA_1("#B05F6D",3),
    ROSA_2("#782D3E",3),
    ROSA_3("#944655",3),
    //ROSA_4("#5D1428",3),
    //ROSA_5("#430013"),
    CORAL_1("#D06560",4),
    CORAL_2("#8E2B2D",4),
    CORAL_3("#AF4846",4),
    CORAL_4("#6E0916",4),
    //CORAL_5("#500000", 2),
    //NARANJA_1("#EB6B56",3),
    //NARANJA_2("#A0291E",3),
    //NARANJA_3("#C54B39",5),
    //NARANJA_4("#7C0002",5),
    //NARANJA_5("#5A0000"),
    AMARILLO_1("#FFC153",5),
    //AMARILLO_2("#9C6C00",3),
    AMARILLO_3("#CD9525",5),
    AMARILLO_4("#6E4500",5),
    AMARILLO_5("#472000",5),
    VERDE_1("#237B2F",6),
    VERDE_2("#004300",6),
    VERDE_3("#66B568",6),
    VERDE_4("#0C661F",6)
}

data class Colar(val r: Int,
                 val g: Int,
                 val b: Int) {
    constructor(color: javafx.scene.paint.Color): this(color.red.toInt(),color.green.toInt(),color.blue.toInt())
}

object Colores {

    // Cantidad de grupos de colores
    private val numGruposColores = Color.values().distinctBy { it.grupo }.max()!!.grupo

    // Gradientes no repetidos
    private var noRepetidos = Color.values().size / 2 - 2

    // Ultimos (#noRepetidos * 2) colores utilizados para crear los ultimos #noRepetidos gradientes
    private val coloresProhibidos = ArrayList<Int>((0 until noRepetidos * 2).map { -1 })
    private val gruposProhibidos = ArrayList<Int>()
    private val posiblesColores = ArrayList<Int>((0 until Color.values().size).toMutableList())
    private val posiblesGrupos = ArrayList<Int>((1..numGruposColores).toMutableList())

    /**
     * Retornamos dos colores aleatorios de la paleta de colores
     * @see [Constantes.PALETA_COLORES]
     *
     * @return Pair<Color,Color>: Dos pares de colores aleatorios
     */
    fun coloresRandom(): Pair<Colar, Colar> {

        // Generador seguro
        val secureRandom = SecureRandom()

        // Obtenemos de manera aleatoria el grupo de colores que se usará para el gradiente
        val grupoColor = if (posiblesGrupos.size == 1) posiblesGrupos.get(0) else posiblesGrupos.get(secureRandom.nextInt(posiblesGrupos.size - 1) + 1)

        // Obtenemos los colores posibles del grupo
        val colores = posiblesColores.filter { Color.values().get(it).grupo == grupoColor }

        // Obtenemos de manera aleatoria el primer color según su grupo
        val posPrimero = secureRandom.nextInt(colores.size - 1)
        val indxPrimerColor = colores.get(posPrimero)

        // Creamos el primer color del gradiente
        var temp = java.awt.Color.decode(Color.values().get(indxPrimerColor).value)
        val primerColor = Colar(temp.red, temp.green, temp.blue)

        //Creamos el segundo @Colar. Siempre buscaremos obtener el siguiente color del grupo,
        // si no hay siguiente, obtendremos el anterior
        var posSegundo = posPrimero + 1
        if (indxPrimerColor == colores.size - 1) {
            posSegundo = posPrimero - 1
        }
        val indxSegundoColor = colores.get(posSegundo)
        temp = java.awt.Color.decode(Color.values().get(indxSegundoColor).value)
        val segundoColor = Colar(temp.red, temp.green, temp.blue)

        // Actualizamos la lista de colores que podremos usar en la siguiente
        // llamada al método
        actualizarListaColoresPosibles(Color.values().get(indxPrimerColor), indxPrimerColor, Color.values().get(indxSegundoColor), indxSegundoColor)

        // Retornamos el par de colores
        return Pair(primerColor, segundoColor)
    }

    /**
     * Actualizamos la lista de colores que podrá obtenerse en la próxima
     * llamada a [coloresRandom]
     * @param primerColor
     * @param posPrimer
     * @param segundoColor
     * @param posSegundo
     */
    private fun actualizarListaColoresPosibles(primerColor: Color, posPrimer: Int, segundoColor: Color, posSegundo: Int) {

        // Obtenemos los dos colores prohibidos con más antigüedad
        val antiguosColores = Pair(coloresProhibidos.get(0), coloresProhibidos.get(1))

        // Eliminamos los antiguos colores de la lista de prohibidos
        coloresProhibidos.removeAt(0)
        coloresProhibidos.removeAt(0)

        // Si el valor al que apuntan es positivo, lo eliminaremos de la lista
        // y pasará a ser un color permitido
        if (antiguosColores.first != -1 && antiguosColores.second != -1) {
            posiblesColores.addAll(posiblesColores.size, mutableListOf(antiguosColores.first, antiguosColores.second))

            // Grupo al que pertenecían los antiguos colores
            val grupoAntiguos = Color.values().get(antiguosColores.first).grupo

            // Comprobamos si el grupo de los colores antiguos estaba prohibido
            // y lo cambiamos a la lista de posibles
            if (gruposProhibidos.indexOf(grupoAntiguos) != -1) {
                gruposProhibidos.remove(grupoAntiguos)
                posiblesGrupos.add(posiblesGrupos.size,grupoAntiguos)
            }
        }

        // Prohibimos los colores utilizados actualmente
        coloresProhibidos.addAll(coloresProhibidos.size, mutableListOf(posPrimer, posSegundo))
        posiblesColores.removeAll(arrayOf(posPrimer, posSegundo))

        // Comprobamos si quedan colores disponibles del mismo grupo que los utilizados actualmente
        if (posiblesColores.filter { Color.values().get(it).grupo == primerColor.grupo }.size == 0) {

            // Añadimos el grupo a la lista de prohibidos
            gruposProhibidos.add(gruposProhibidos.size,primerColor.grupo)
            posiblesGrupos.remove(primerColor.grupo)
        }

        /*println("Colores prohibidos")
        if (coloresProhibidos.size > 0){
            coloresProhibidos.forEach { posColor ->

            if (posColor > -1){
                println("${Color.values().get(posColor)}") }
            }
        }*/
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
    fun colorRandomEntre(color1: Colar, color2: Colar): javafx.scene.paint.Color {

        val max = 100
        val min = 1

        var valido = false
        var randomStep = -1
        var randomSub = -1

        // Generamos dos valores aleatorios para #randomStep y #randomSub.
        // Siempre #randomSub tiene que ser menor o igual que #randomStep y en ningún momento
        // #randomStep puede valer 0
        while (!valido) {
            randomStep = Random.nextInt((max - min) + min)
            randomSub = Random.nextInt((randomStep - min) + min)
            while (randomSub > randomStep) {
                randomSub = Random.nextInt((randomStep - min) + 1)
            }

            valido = true

        }

        return javafx.scene.paint.Color.rgb(
                (color1.r * (randomStep - randomSub) + color2.r * randomSub) / randomStep,
                (color1.g * (randomStep - randomSub) + color2.g * randomSub) / randomStep,
                (color1.b * (randomStep - randomSub) + color2.b * randomSub) / randomStep
        )
    }
}