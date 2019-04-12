package Utiles

import java.io.File

object Utils {

    /**
     * Devolvemos la ruta por defecto en la que se buscará la existencia
     * de plugins.
     *
     * @return String ruta
     */
    fun getDirectorioDefecto(): String{

        val direcPersonal: File = File(System.getProperty("user.home"));

        // Recorreemos las carpetas existentes en el directorio personal
        for (carpeta: File in direcPersonal.listFiles().filter { it.isDirectory }){
            // Comprobamos que exista una carpeta "Documents||Documentos" bajo
            // el directorio personal
            if (carpeta.name.equals("Documents") || carpeta.name.equals("Documentos")){
                return direcPersonal.name + carpeta.name
            }
        }

        // Por defecto retornaremos el directorio personal
        return direcPersonal.name
    }

}