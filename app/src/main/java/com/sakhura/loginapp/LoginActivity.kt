package com.sakhura.loginapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sakhura.loginapp.data.models.ResultadoAutenticacion
import com.sakhura.loginapp.data.repository.AutenticacionRepositoryImpl
import com.sakhura.loginapp.data.repository.LocalDataSource
import com.sakhura.loginapp.domain.usecases.LoginUseCase
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope


class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnMostrarCredenciales: Button
    private lateinit var tvError: TextView

    // Arquitectura completa con UseCase
    private lateinit var loginUseCase: LoginUseCase
    private var intentosFallidos = 0
    private val maxIntentos = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inicializarVistas()
        inicializarDependencias()
        configurarEventos()
    }

    private fun inicializarDependencias() {
        // Crear las dependencias manualmente (sin DI por simplicidad)
        val localDataSource = LocalDataSource()
        val repository = AutenticacionRepositoryImpl(localDataSource)
        loginUseCase = LoginUseCase(repository)
    }

    private fun inicializarVistas() {
        etUsuario = findViewById(R.id.et_usuario)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnMostrarCredenciales = findViewById(R.id.btn_mostrar_credenciales)
        tvError = findViewById(R.id.tv_error)
    }

    private fun configurarEventos() {
        btnLogin.setOnClickListener {
            realizarLogin()
        }

        btnMostrarCredenciales.setOnClickListener {
            mostrarCredencialesPrueba()
        }
    }

    private fun realizarLogin() {
        val usuario = etUsuario.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (intentosFallidos >= maxIntentos) {
            mostrarError("Máximo de intentos alcanzado. Intenta más tarde.")
            return
        }

        // Mostrar loading
        btnLogin.isEnabled = false
        btnLogin.text = "Verificando..."

        // Usar coroutines para el UseCase
        lifecycleScope.launch {
            val resultado = loginUseCase.ejecutar(usuario, password)

            // Restaurar botón
            btnLogin.isEnabled = true
            btnLogin.text = "🚀 Iniciar Sesión"

            when (resultado) {
                is ResultadoAutenticacion.Exitoso -> {
                    Toast.makeText(this@LoginActivity, "¡Login exitoso!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, MenuPrincipalActivity::class.java)
                    intent.putExtra("USUARIO_ID", resultado.usuario.id)
                    intent.putExtra("USUARIO_NOMBRE", resultado.usuario.nombreUsuario)
                    intent.putExtra("USUARIO_EMAIL", resultado.usuario.email)
                    startActivity(intent)
                    finish()
                }
                is ResultadoAutenticacion.Error -> {
                    intentosFallidos++
                    val intentosRestantes = maxIntentos - intentosFallidos
                    mostrarError("${resultado.mensaje} ($intentosRestantes intentos restantes)")
                    limpiarCampos()
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE

        tvError.postDelayed({
            tvError.visibility = View.GONE
        }, 3000)
    }

    private fun limpiarCampos() {
        etPassword.text.clear()
        etUsuario.requestFocus()
    }

    private fun mostrarCredencialesPrueba() {
        val credenciales = """
            Credenciales de prueba:
            
            👤 admin / 🔒 admin123
            👤 usuario1 / 🔒 password123
            👤 test / 🔒 test123
            👤 demo / 🔒 demo123
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("🔑 Credenciales de Prueba")
            .setMessage(credenciales)
            .setPositiveButton("OK", null)
            .show()
    }
}