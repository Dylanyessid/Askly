package com.dlapps.acahelp.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlapps.acahelp.api.ApiResponse
import com.dlapps.acahelp.api.RegisterRequest
import com.dlapps.acahelp.repositories.AuthRepository
import com.dlapps.acahelp.utils.validateEmail
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okio.IOException

sealed class RegisterState {
    object Idle : RegisterState()           // Estado inicial (formulario vacío)
    object Loading : RegisterState()        // El backend de .NET está procesando
    object Success : RegisterState()        // Recibimos el 201 Created
    data class Error(val code: String) : RegisterState() // Algo falló (400, 500, etc.)
}

data class FieldState(
    val value: String = "",
    val error: String? = null, // Si es null, el campo es correct
    val isTouched: Boolean = false
)
class RegisterScreenViewModel ( private val authRepository: AuthRepository): ViewModel() {

    var nameState by mutableStateOf(FieldState())
        private set
    var lastNameState by mutableStateOf(FieldState())
        private set

    var emailState by mutableStateOf(FieldState())
        private set
    var passwordState by mutableStateOf(FieldState())
        private set

    fun onNameChange(value: String) {
        nameState = nameState.copy(value = value)
    }

    fun onLastNameChange(value: String) {
        lastNameState = lastNameState.copy(value = value)
    }

    fun onEmailChange(value: String) {

        emailState = emailState.copy(value = value)
    }

    fun onEmailBlur() {
        val validationResult = validateEmail(emailState.value)
        emailState = emailState.copy(error = validationResult.errorMessage, isTouched = true)
    }

    fun onPasswordChange(value: String) {
        passwordState = passwordState.copy(value = value)
    }


    val errorMessage = MutableLiveData<String?>()
    val registerState = MutableLiveData<RegisterState>(RegisterState.Idle)
    fun register(){
        viewModelScope.launch {

            val currentName = nameState.value
            val currentLastName = lastNameState.value
            val currentEmail = emailState.value
            val currentPassword = passwordState.value


            registerState.value = RegisterState.Loading
            try {
                val payload = RegisterRequest(currentName, currentLastName, currentEmail, currentPassword);
                val result = authRepository.register(payload)
                Log.d("RegisterScreen", "Success: ${result.isSuccessful})}")

                if(result.isSuccessful) {
                    val body = result.body()
                    Log.d("RegisterScreen", body.toString())
                    Log.d("RegisterScreen", "Botón presionado")
                    registerState.value = RegisterState.Success
                }else{

                    val errorJson = result.errorBody()?.string()

                    val errCode = try {
                        val errObject = Gson().fromJson(errorJson, ApiResponse::class.java)
                        errObject.messageCode
                    }catch (e: Exception){
                        "UNKNOWN_ERROR"
                    }
                    Log.e("API", "HTTP error: ${errCode}")
                    registerState.value = RegisterState.Error(errCode)
                }
            }catch (e: IOException){
                //val errorJson = e.response()?.errorBody()?.string()
                registerState.value = RegisterState.Error("UNKNOWN_ERROR")
                Log.e("API", "API call error: ${e.message}")
                errorMessage.value ="Algo salió mal"
            }
        }
    }
}