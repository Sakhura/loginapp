package com.sakhura.loginapp.domain.usecases

import com.sakhura.loginapp.data.models.ResultadoAutenticacion
import com.sakhura.loginapp.data.repository.AutenticacionRepository

/**
 * Caso de uso para el login
 * Contiene la lógica de negocio específica para la autenticación
 */
class LoginUseCase(
    private val repository: AutenticacionRepository
) {

    suspend fun ejecutar(nombreUsuario: String, password: String): ResultadoAutenticacion {

        // Validaciones de negocio
        if (nombreUsuario.length < 3) {
            return ResultadoAutenticacion.Error("El usuario debe tener al menos 3 caracteres")
        }

        if (password.length < 6) {
            return ResultadoAutenticacion.Error("La contraseña debe tener al menos 6 caracteres")
        }

        // Validaciones adicionales de seguridad
        if (esUsuarioBloqueado(nombreUsuario)) {
            return ResultadoAutenticacion.Error("Usuario temporalmente bloqueado")
        }

        // Delegar al repository
        return repository.autenticarUsuario(nombreUsuario, password)
    }

    private fun esUsuarioBloqueado(nombreUsuario: String): Boolean {
        // TODO: Implementar lógica de bloqueo temporal
        // Por ejemplo, después de 5 intentos fallidos
        return false
    }
}