package com.dlapps.acahelp.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dlapps.acahelp.LoginState
import com.dlapps.acahelp.MainActivityViewModel
import com.dlapps.acahelp.api.ApiException
import com.dlapps.acahelp.data.local.TokenManager
import com.dlapps.acahelp.repositories.AuthRepository
import kotlinx.coroutines.launch

class LoginScreenViewModel (
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel()  {

    var uiState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    val errorMessage = MutableLiveData<String?>()
    fun login(email: String, password: String) {
        viewModelScope.launch {

            repository.login(email, password)
                .onSuccess {
                    uiState = LoginState.Success
                }
                .onFailure { error ->
                    val message =
                        when {
                            error is ApiException -> error.messageCode
                            else -> "UNKNOWN_ERROR"
                        }
                    uiState = LoginState.Error(message)
                }
        }
    }
}

class LoginScreenViewModelFactory(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginScreenViewModel(repository, tokenManager) as T
    }
}