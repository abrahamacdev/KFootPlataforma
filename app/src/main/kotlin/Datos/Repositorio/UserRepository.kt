package Datos.Repositorio

import KFoot.DEBUG
import KFoot.Logger
import org.jetbrains.annotations.TestOnly

class UserRepository: IUserRepository {

    @TestOnly
    // TODO Modificar la obtención del token del usuario
    override fun getTokenUsuario(correo: String, contrasenia: CharSequence): String? {

        if (Logger.getLogger().getDebugLevel() == DEBUG.DEBUG_TEST){
            ""
        }
        return null
    }

    @TestOnly
    // TODO Modificar la obtención del token en local
    override fun getLocalToken(): String? {
        if (Logger.getLogger().getDebugLevel() == DEBUG.DEBUG_TEST){
            return ""
        }
        return null
    }

    @TestOnly
    // TODO Modificar la validación del token
    override fun validarTokenUsuario(token: String): Boolean {
        if (Logger.getLogger().getDebugLevel() == DEBUG.DEBUG_TEST){
            return true
        }
        return false
    }
}