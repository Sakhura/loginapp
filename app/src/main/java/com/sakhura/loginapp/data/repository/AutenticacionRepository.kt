package com.sakhura.loginapp.data.repository

import com.sakhura.loginapp.data.models.Usuario
import com.sakhura.loginapp.data.models.ResultadoAutenticacion

/**
 * Interface del Repository (contrato)
 * Define qué operaciones puede hacer el repository
 */
interface AutenticacionRepository {
    suspend fun autenticarUsuario(nombreUsuario: String, password: String): ResultadoAutenticacion
    suspend fun obtenerUsuarios(): List<Usuario>
    suspend fun guardarUsuario(usuario: Usuario): Boolean
    suspend fun actualizarUsuario(usuario: Usuario): Boolean
    suspend fun eliminarUsuario(id: Int): Boolean
}

/**
 * Implementación del Repository
 * Coordina entre diferentes fuentes de datos
 */
class AutenticacionRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource? = null // Opcional para APIs futuras
) : AutenticacionRepository {

    override suspend fun autenticarUsuario(nombreUsuario: String, password: String): ResultadoAutenticacion {
        return try {
            // Validaciones básicas
            if (nombreUsuario.isBlank() || password.isBlank()) {
                return ResultadoAutenticacion.Error("Usuario y contraseña no pueden estar vacíos")
            }

            // Buscar en datos locales primero
            val usuario = localDataSource.buscarUsuarioPorCredenciales(nombreUsuario, password)

            if (usuario != null && usuario.estaActivo) {
                ResultadoAutenticacion.Exitoso(usuario)
            } else {
                // Si no existe localmente, intentar con servidor remoto (futuro)
                remoteDataSource?.let { remote ->
                    val usuarioRemoto = remote.autenticarUsuario(nombreUsuario, password)
                    if (usuarioRemoto != null) {
                        // Guardar en cache local
                        localDataSource.guardarUsuario(usuarioRemoto)
                        ResultadoAutenticacion.Exitoso(usuarioRemoto)
                    } else {
                        ResultadoAutenticacion.Error("Credenciales incorrectas")
                    }
                } ?: ResultadoAutenticacion.Error("Credenciales incorrectas")
            }
        } catch (e: Exception) {
            ResultadoAutenticacion.Error("Error de conexión: ${e.message}")
        }
    }

    override suspend fun obtenerUsuarios(): List<Usuario> {
        return try {
            localDataSource.obtenerTodosLosUsuarios()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun guardarUsuario(usuario: Usuario): Boolean {
        return try {
            localDataSource.guardarUsuario(usuario)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Boolean {
        return try {
            localDataSource.actualizarUsuario(usuario)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun eliminarUsuario(id: Int): Boolean {
        return try {
            localDataSource.eliminarUsuario(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Data Source Local (datos en memoria o base de datos local)
 */
class LocalDataSource {

    private val usuarios = mutableListOf<Usuario>()

    init {
        // Cargar usuarios de prueba
        cargarUsuariosPrueba()
    }

    private fun cargarUsuariosPrueba() {
        usuarios.addAll(listOf(
            Usuario(1, "admin", "admin123", "admin@empresa.com", true),
            Usuario(2, "usuario1", "password123", "user1@empresa.com", true),
            Usuario(3, "test", "test123", "test@empresa.com", true),
            Usuario(4, "demo", "demo123", "demo@empresa.com", true)
        ))
    }

    fun buscarUsuarioPorCredenciales(nombreUsuario: String, password: String): Usuario? {
        return usuarios.find {
            it.nombreUsuario.equals(nombreUsuario, ignoreCase = true) &&
                    it.password == password
        }
    }

    fun obtenerTodosLosUsuarios(): List<Usuario> {
        return usuarios.toList()
    }

    fun guardarUsuario(usuario: Usuario): Boolean {
        // Verificar que no existe ya
        if (usuarios.any { it.id == usuario.id || it.nombreUsuario == usuario.nombreUsuario }) {
            return false
        }
        usuarios.add(usuario)
        return true
    }

    fun actualizarUsuario(usuario: Usuario): Boolean {
        val index = usuarios.indexOfFirst { it.id == usuario.id }
        return if (index != -1) {
            usuarios[index] = usuario
            true
        } else {
            false
        }
    }

    fun eliminarUsuario(id: Int): Boolean {
        return usuarios.removeIf { it.id == id }
    }

    fun buscarUsuarioPorId(id: Int): Usuario? {
        return usuarios.find { it.id == id }
    }

    fun buscarUsuarioPorNombre(nombreUsuario: String): Usuario? {
        return usuarios.find { it.nombreUsuario.equals(nombreUsuario, ignoreCase = true) }
    }
}

/**
 * Data Source Remoto (APIs futuras)
 */
class RemoteDataSource {

    // Simulación de llamada a API
    suspend fun autenticarUsuario(nombreUsuario: String, password: String): Usuario? {
        // TODO: Implementar llamada real a API REST
        // Por ahora, simulamos una respuesta
        return null
    }

    suspend fun obtenerUsuarios(): List<Usuario> {
        // TODO: Implementar llamada real a API REST
        return emptyList()
    }

    suspend fun crearUsuario(usuario: Usuario): Usuario? {
        // TODO: Implementar llamada real a API REST
        return null
    }
}