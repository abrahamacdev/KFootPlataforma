package Datos.Modelo

class ParametrosEscenario {

    val pantallaMaximizada: Boolean
    val mostrarEscenarioInmediatamente: Boolean
    val minWidth: Double
    val minHeight: Double

    class ParametrosEscenarioFactory{

        private var pantallaMaximizada = false
        private var mostrarEscenarioInmediatamente = false
        private var minWidth = 400.0
        private var minHeight = 600.0

        constructor()

        fun conPantallaMaximizada(){
            pantallaMaximizada = true
        }

        /**
         * Establecemos los anchos y altos mínimos del escenario
         *
         * @param ancho: Ancho mínimo
         * @param alto: Alto mínimo
         */
        fun conAnchoYAltoMinimos(ancho: Double, alto: Double){

            if (ancho > 0.0){
                minWidth = ancho
            }

            if (alto > 0.0){
                minHeight = alto
            }
        }

        /**
         * Mostraremos el escenario justo después de asignarle
         * las propiedades
         */
        fun mostrarEscenarioInmediatamente(){
            mostrarEscenarioInmediatamente = true
        }

        fun build(): ParametrosEscenario{
            return ParametrosEscenario(pantallaMaximizada, mostrarEscenarioInmediatamente, minWidth, minHeight)
        }

    }

    private constructor(pantallaMaximizada: Boolean, mostrarEscenarioInmediatamente: Boolean, minWidth: Double, minHeight: Double) {
        this.pantallaMaximizada = pantallaMaximizada
        this.mostrarEscenarioInmediatamente = mostrarEscenarioInmediatamente
        this.minWidth = minWidth
        this.minHeight = minHeight
    }
}