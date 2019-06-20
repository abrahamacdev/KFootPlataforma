
interface IMain {

    interface setOnPluginCargadoListener {
        /**
         * Este metodo se llamará cada vez que un plugin
         * se haya cargado en memoria
         *
         * @param plugin: Plugin recién cargado
         */
        fun onPluginCargado(plugin: Modelo.Plugin.Plugin)
    }
}