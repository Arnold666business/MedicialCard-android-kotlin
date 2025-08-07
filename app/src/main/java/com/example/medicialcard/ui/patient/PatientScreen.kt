package com.example.medicialcard.ui.patient

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicialcard.data.PatientDto
import com.example.medicialcard.utils.Routes
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.medicialcard.ui.components.ErrorState

@Composable
fun PatientScreen(navController: NavController) {
    val viewModel: PatientViewModel = viewModel()
    val patientDataState by viewModel.patientDataState.collectAsState()

    LaunchedEffect(patientDataState) {
        if (patientDataState is PatientViewModel.PatientState.Unauthorized) {
            navController.navigate(Routes.START) {
                popUpTo(Routes.PATIENT) { inclusive = true }
            }
        }
    }

    when (val currentState = patientDataState) {
        is PatientViewModel.PatientState.Loading ->
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

        is PatientViewModel.PatientState.Success ->
            PatientProfile(
                patient = currentState.patient,
                onDoctorsClick = { navController.navigate(Routes.CATEGORY) },
                onAppointmentsClick = { navController.navigate(Routes.APPOINTMENTS) },
                onLogout = {viewModel.logOut()}
            )

        is PatientViewModel.PatientState.Error ->
            ErrorState(message = currentState.message)
        else -> {}
    }
}

@SuppressLint("NewApi")
@Composable
private fun PatientProfile(
    patient: PatientDto,
    onDoctorsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "${patient.firstName} ${patient.lastName}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, top = 40.dp)
        )


        ProfileSection("Основная информация") {
            InfoItem("ID пациента", patient.id.toString())
            InfoItem("Дата регистрации", patient.dateOfRegistration.format(dateFormatter))
        }


        ProfileSection("Контактная информация") {
            InfoItem("Email", patient.email)
            InfoItem("Телефон", patient.phone)
            InfoItem("Логин", patient.login)
        }

        Column(Modifier.padding(top = 24.dp)) {
            Button(
                onClick = onDoctorsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Мои врачи")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onAppointmentsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Мои приёмы")
            }
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Выход")
            }
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Divider(Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(value, fontWeight = FontWeight.Medium)
    }
}

