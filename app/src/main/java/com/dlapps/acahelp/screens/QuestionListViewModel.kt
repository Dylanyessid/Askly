package com.dlapps.acahelp.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


sealed interface UIState {
    object Loading : UIState
    object Empty : UIState
    //data class Success(thePreguntas: List<String>) : UIState
}
class QuestionListViewModel: ViewModel() {
    // El estado interno (mutable)
    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)

    // El estado público (de solo lectura para la vista)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {

        viewModelScope.launch {
            // Simulamos que la API tarda 2 segundos en responder
            delay(2000)

            // CASO A: Simular que la API devolvió una lista vacía
            _uiState.value = UIState.Empty

            // CASO B: Para probar cuando SÍ hay datos, descomenta la línea de abajo:
            // _uiState.value = UIState.Exito(listOf("¿Qué es un StateFlow?", "¿Cómo funciona Compose?"))
        }
    }
}