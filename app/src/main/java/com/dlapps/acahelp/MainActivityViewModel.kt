package com.dlapps.acahelp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dlapps.acahelp.api.ApiException
import com.dlapps.acahelp.api.ApiResponse
import com.dlapps.acahelp.data.local.TokenManager
import com.dlapps.acahelp.repositories.AuthRepository
import com.dlapps.acahelp.utils.mapErrorCodeToMessage
import com.google.gson.Gson
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class MainActivityViewModel (
    private val tokenManager: TokenManager
) : ViewModel() {

    // Empezamos en true para que la app sepa que debe esperar
    var isLoading = mutableStateOf(true)
        private set

    var startDestination = mutableStateOf("auth_graph")
        private set

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val isValid = tokenManager.isSessionValid()
            if (isValid) {
                startDestination.value = "main_graph"
            } else {
                startDestination.value = "auth_graph"
            }
            // Una vez que tenemos el veredicto, dejamos de cargar
            isLoading.value = false
        }
    }
}

class MainActivityViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(tokenManager) as T
    }
}