package com.example.medicialcard.ui.register

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.medicialcard.data.RegisterRequest
import java.time.LocalDate
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicialcard.R
import com.example.medicialcard.ui.components.DatePickerField
import com.example.medicialcard.ui.login.LoginViewModel
import com.example.medicialcard.utils.Routes
import java.time.LocalDateTime

@SuppressLint("NewApi")
@Composable
fun RegisterScreen(navController: NavController){
    val viewModel: RegisterViewModel = viewModel()
    val state by viewModel.registerState.collectAsState()

    var registerFields by remember { mutableStateOf(RegisterRequest("", "","","","","",
        "2000-01-01")) }


    LaunchedEffect(state) {
        when (val current = state) {
            is RegisterViewModel.RegisterState.Success -> {
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
            is RegisterViewModel.RegisterState.Loading -> CircularProgressIndicator()
            is RegisterViewModel.RegisterState.Error -> Text(current.message, color = Color.Red)
            else -> {}
        }

        TextField(
            value = registerFields.login,
            onValueChange = { registerFields = registerFields.copy(login = it) },
            label = { Text("логин") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = registerFields.password,
            onValueChange = { registerFields = registerFields.copy(password = it) },
            label = { Text(stringResource(R.string.password_field)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = registerFields.email,
            onValueChange = { registerFields = registerFields.copy(email = it) },
            label = { Text("почта") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = registerFields.phone,
            onValueChange = { registerFields = registerFields.copy(phone = it) },
            label = { Text("телефон") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = registerFields.lastName,
            onValueChange = { registerFields = registerFields.copy(lastName = it) },
            label = { Text("имя") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = registerFields.firstName,
            onValueChange = { registerFields = registerFields.copy(firstName = it)},
            label = { Text("фамилия") },
            modifier = Modifier.fillMaxWidth()
        )



        Button(
            onClick = {
                viewModel.reg(registerFields)

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }
    }

}