package Datos.Modelo

import java.util.prefs.Preferences
import KFoot.Constantes as KFootConstantes
import KFoot.Utils as KFootUtils

object Preferencias{

    private val prefs = Preferences.userRoot().node(javaClass.canonicalName)

    /**
     * Retornamos la preferencia solicitada por
     * parámetro
     *
     * @param clave: Clave de la preferencia
     *
     * @return Any: Valor asociado a la clave si existe
     */
    fun obtener(clave: String): Any{
        return prefs.get(clave,"")
    }

    /**
     * Retornamos la preferencia solicitada por
     * parámetro
     *
     * @param clave: Clave de la preferencia
     *
     * @return Any?: Valor asociado a la clave si existe
     */
    fun obtenerOrNulo(clave: String): Any? {
        val valor = prefs.get(clave,"")
        if (valor.equals("")) return null else return valor
    }

    /**
     * Modificamos el valor de alguna preferencia
     *
     * @param clave: Nombre de la clave de la preferencia
     * @param valor: Nuevo valor asociado a esa clave
     */
    fun modificar(clave: String, valor: Any){

        prefs.remove(clave)
        prefs.put(clave,valor.toString())
    }

    /**
     * Añadimos la nueva clave a las preferencias en caso de
     * que aún no exista. En caso contrario se modificará el
     * valor antiguo
     */
    fun anadir(clave: String, valor: Any){

        if (obtenerOrNulo(clave) == null){
            prefs.put(clave,valor.toString())
        }

        else {
            modificar(clave,valor)
        }
    }

}