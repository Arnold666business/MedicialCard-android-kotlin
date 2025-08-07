package com.example.medicialcard.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicialcard.R
import com.example.medicialcard.utils.Routes

@Composable
fun LoginScreen(navController: NavController){
    val viewModel: LoginViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    LaunchedEffect(state) {
        when (val current = state) {
            is LoginViewModel.LoginState.Success -> {
                navController.navigate(Routes.PATIENT) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        when(val current = state){
            is LoginViewModel.LoginState.Loading -> CircularProgressIndicator()
            is LoginViewModel.LoginState.Error -> Text(current.message, color = Color.Red)
            else -> {}
        }

        TextField(
            value = login,
            onValueChange = { login = it },
            label = { Text(stringResource(R.string.login_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_field)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )


        Button(
            onClick = {
                viewModel.login(login, password)

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.sing_in))
        }


    }

}