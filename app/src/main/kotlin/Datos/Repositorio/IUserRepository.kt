package Datos.Repositorio

interface IUserRepository {

    fun getTokenUsuario(correo: String, contrasenia: CharSequence): String?

    fun getLocalToken(): String?

    fun validarTokenUsuario(token: String): Boolean
}