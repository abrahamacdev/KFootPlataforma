interface Inicializable {

    /**
     * Se llamará para preparar a la clase que implemente la interfaz
     * antes de iniciarla
     *
     * @param block: Bloque de código a ejecutar en la precarga
     */
    fun preCargar(block: () -> Unit)

    /**
     * Se llamará para preparar a la clase que implemente la interfaz
     * antes de iniciarla
     */
    fun preCargar()

    /**
     * Cada clase que implemente esta interfaz comenzará a ejecutarse una vez que se llame
     * a este método
     *
     * @param block: Bloque de código a ejecutar en el inicio
     */
    fun iniciar(block: () -> Unit)

    /**
     * Cada clase que implemente esta interfaz comenzará a ejecutarse una vez que se llame
     * a este método
     *
     */
    fun iniciar()

    /**
     * Cuando este métdo sea llamado, la clase que implementa la interfaz pausará su funcionamiento
     *
     * @param block: Bloque de código a ejecutar en el pausado
     */
    fun pausar(block: () -> Unit)

    /**
     * Cuando este métdo sea llamado, la clase que implementa la interfaz pausará su funcionamiento
     *
     */
    fun pausar()

    /**
     * Se cancelará por completo la ejecución de la clase
     *
     * @param block: Bloque de código a ejecutar en la cancelación
     */
    fun cancelar(block: () -> Unit)

    /**
     * Se cancelará por completo la ejecución de la clase
     *
     */
    fun cancelar()

    /**
     * Se reanudará la ejecución de la clase después de haber sido pausada
     *
     * @param block: Bloque de código a ejecutar en la reanudación
     */
    fun reanudar(block: () -> Unit)

    /**
     * Se reanudará la ejecución de la clase después de haber sido pausada
     *
     */
    fun reanudar()
}