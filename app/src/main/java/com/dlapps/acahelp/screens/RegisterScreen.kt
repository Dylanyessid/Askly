package com.dlapps.acahelp.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.dlapps.acahelp.data.local.TokenManager
import com.dlapps.acahelp.repositories.AuthRepository
import com.dlapps.acahelp.ui.components.CustomTextField
import com.dlapps.acahelp.ui.theme.BluePrimary
import com.dlapps.acahelp.utils.mapErrorCodeToMessage

@Composable
fun RegisterScreen(navController: NavController, ) {
    val context = LocalContext.current
    val viewModel: RegisterScreenViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val tokenManager = TokenManager(context)
                val authRepository = AuthRepository(tokenManager)
                RegisterScreenViewModel(authRepository)
            }
        }
    )
    val state by viewModel.registerState.observeAsState(RegisterState.Idle)
    val scrollState = rememberScrollState()
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (state) {
            is RegisterState.Loading -> CircularProgressIndicator()
            is RegisterState.Error -> Text( mapErrorCodeToMessage((state as  RegisterState.Error).code), color = Color.Red)
            is RegisterState.Success -> {
                // Opción A: Mostrar mensaje
                Text("¡Registro exitoso!", color = Color.Green)

                // Opción B: Navegar automáticamente al Login
                /*LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true } // Limpia el historial
                    }
                }*/
            }
            else -> {}
        }

        RegisterForm(viewModel)

        Button(
            onClick = {
                viewModel.register()
                //viewModel.register(name, lastName, email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
            Text(
                text = "Regístrate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, BluePrimary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = BluePrimary
            )
        ) {
            Text(
                text = "Iniciar Sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
fun RegisterForm(viewModel: RegisterScreenViewModel){

    var emailWasFocused by remember { mutableStateOf(false) }
    Column {

        CustomTextField(
            value = viewModel.nameState.value,
            onValueChange = { viewModel.onNameChange(it) },
            placeholder = "Ingrese sus nombres"
        )

        CustomTextField(
            value = viewModel.lastNameState.value,
            onValueChange = { viewModel.onLastNameChange(it) },
            placeholder = "Ingrese sus apellidos"
        )

        CustomTextField(
            value = viewModel.emailState.value,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "Email",
            isError = viewModel.emailState.error != null && viewModel.emailState.isTouched,
            errorMessage = viewModel.emailState.error,
            onFocusChanged = {
                if (it.isFocused) emailWasFocused = true
                if (!it.isFocused && emailWasFocused) viewModel.onEmailBlur()
            }
        )

        CustomTextField(
            value = viewModel.passwordState.value,
            onValueChange = { viewModel.onPasswordChange(it) },
            placeholder = "Ingrese su contraseña",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}