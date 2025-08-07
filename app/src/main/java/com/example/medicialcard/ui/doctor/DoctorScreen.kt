package com.example.medicialcard.ui.doctor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.medicialcard.data.DoctorProfile
import com.example.medicialcard.ui.DoctorImageResolver
import com.example.medicialcard.ui.components.ErrorState
import com.example.medicialcard.utils.Routes

@Composable
fun DoctorScreen(navController: NavController, doctorId: Long) {
    val viewModel: DoctorViewModel =
        viewModel(factory = DoctorViewModel.Factory(doctorId))
    val state by viewModel.doctorState.collectAsState()
    val notification by viewModel.notificationMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(notification) {
        notification?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotification()
        }
    }

    LaunchedEffect(state) {
        if (state is DoctorViewModel.DoctorState.Unauthorized) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.DOCTOR_DETAIL) { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (val currentState = state) {
            is DoctorViewModel.DoctorState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DoctorViewModel.DoctorState.Success -> {
                DoctorProfileContent(
                    doctor = currentState.doctor,
                    padding = padding,
                    onReviewsClick = { navController.navigate("reviews/$doctorId") },
                    onAppointmentClick = { viewModel.sendNotificationToRegistryAppointment() },
                    onReviewClick = { viewModel.tryTySendReview(navController) }
                )
            }
            is DoctorViewModel.DoctorState.Error -> {
                ErrorState(message = currentState.message)
            }
            else -> {}
        }
    }
}

@Composable
private fun DoctorProfileContent(
    doctor: DoctorProfile,
    padding: PaddingValues,
    onReviewsClick: () -> Unit,
    onAppointmentClick: () -> Unit,
    onReviewClick: () -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .padding(16.dp),
    ) {

        AsyncImage(
            model = DoctorImageResolver.getImageResource(doctor.doctor.doctorId),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "${doctor.doctor.firstName} ${doctor.doctor.lastName}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = doctor.categoryTitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        RatingBar(
            rating = doctor.doctor.rating,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onReviewsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Посмотреть отзывы")
            }

            Button(
                onClick = onAppointmentClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Записаться на прием", color = MaterialTheme.colorScheme.onSecondary)
            }

            OutlinedButton(
                onClick = onReviewClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Оставить отзыв")
            }
        }
    }
}

@Composable
private fun RatingBar(rating: Double, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.titleMedium
        )
    }
}