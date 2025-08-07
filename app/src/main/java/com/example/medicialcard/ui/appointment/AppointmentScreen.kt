package com.example.medicialcard.ui.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.medicialcard.data.AppointmentDto
import com.example.medicialcard.ui.components.ErrorState
import com.example.medicialcard.utils.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(navController: NavController){
    val appointmentViewModel: AppointmentViewModel = viewModel()
    val state by appointmentViewModel.appointmentState.collectAsState()
    val patientName by appointmentViewModel.patientName.collectAsState()


    LaunchedEffect(state) {
        if (state is AppointmentViewModel.AppointmentState.Unauthorized) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.APPOINTMENTS) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Приёмы") },
                actions = {
                    patientName.let {
                        Text(
                            text = it,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val currentState = state) {
            is AppointmentViewModel.AppointmentState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            }
            is AppointmentViewModel.AppointmentState.Success -> {
                AppointmentsList(
                    appointments = currentState.appointments?: emptyList(),
                    getDoctorName = { appointmentViewModel.getDoctorName(it) },
                    modifier = Modifier.padding(padding)
                )
            }
            is AppointmentViewModel.AppointmentState.Error -> {
                ErrorState(message = currentState.message)
            }
            else -> {}
        }
    }
}

@Composable
private fun AppointmentsList(
    appointments: List<AppointmentDto>,
    getDoctorName: (Long) -> String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appointment = appointment,
                doctorName = getDoctorName(appointment.doctorId)
            )
        }
    }
}

@Composable
private fun AppointmentCard(appointment: AppointmentDto, doctorName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow(label = "Врач", value = doctorName)
            InfoRow(label = "Причина", value = appointment.reason)
            InfoRow(label = "Дата", value = appointment.date)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Статус:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                StatusChip(status = appointment.status)
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (color, text) = when (status.lowercase()) {
        "завершен" -> Color(0xFF4CAF50) to "Завершён"
        "отменен" -> Color(0xFFF44336) to "Отменён"
        else -> MaterialTheme.colorScheme.primary to status
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            textAlign = TextAlign.End
        )
    }
}