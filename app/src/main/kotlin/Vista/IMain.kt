package Vista
interface IMain {

    interface setOnPluginCargadoListener {
        /**
         * Este metodo se llamará cada vez que un plugin
         * se haya cargado en memoria
         *
         * @param plugin: Plugin recién cargado
         */
        fun onPluginCargado(plugin: Modelo.Plugin.Plugin)

        /**
         * Este método se llamará una vez que se hayan cargado
         * todos los plugins válidos
         */
        fun onCompletado()
    }
}