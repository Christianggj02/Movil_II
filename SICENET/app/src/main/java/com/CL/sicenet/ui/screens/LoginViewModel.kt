package com.CL.sicenet.ui.screens

import android.text.Spannable.Factory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.CL.sicenet.SicenetApplication
import com.CL.sicenet.data.SiceRepository
import com.CL.sicenet.model.Student
import com.CL.sicenet.model.User

class LoginViewModel(
    private val siceRepository: SiceRepository
) : ViewModel(){

    var loginState by mutableStateOf(LoginState())
        private set

    fun updateLoginState(newState: LoginState){
        loginState = newState
    }

    suspend fun login(credentials: User){
        Log.d("SOAP", credentials.toString())
        updateLoginState(loginState.copy(user = siceRepository.getAccess(credentials.matricula,credentials.contrasenia,"ALUMNO")))
        if (loginState.user.acceso){
            updateLoginState(loginState.copy(student = siceRepository.getInfo()))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SicenetApplication)
                val siceRepository = application.container.siceRepository
                LoginViewModel(siceRepository)
            }
        }
    }
}

data class LoginState(
    val user: User = User(false,"",0,"",""),
    val student: Student = Student(
        fechaReins = "",
        modEducativo = 0,
        adeudo = false,
        urlFoto = "",
        adeudoDescripcion = "",
        inscrito = false,
        estatus = "",
        semActual = 0,
        cdtosAcumulados = 0,
        cdtosActuales = 0,
        especialidad = "",
        carrera = "",
        lineamiento = 0,
        nombre = "",
        matricula = ""
    )
)
