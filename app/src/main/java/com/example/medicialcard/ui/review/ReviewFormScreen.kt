package com.example.medicialcard.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.medicialcard.data.ReviewFormDto
import com.example.medicialcard.ui.register.RegisterViewModel
import com.example.medicialcard.utils.JwtManager
import com.example.medicialcard.utils.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewFormScreen(navController: NavController, doctorId: Long) {
    val viewModel: ReviewFormViewModel = viewModel(factory = ReviewFormViewModel.Factory(doctorId))
    val state by viewModel.reviewFormState.collectAsState()
    val doctorName by viewModel.doctorName.collectAsState()
    val patientId by viewModel.patientId.collectAsState()

    var reviewText by remember { mutableStateOf("") }
    var selectedRating by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        when (val current = state) {
            is ReviewFormViewModel.ReviewFormState.Success -> {
                navController.navigate(Routes.PATIENT) {
                    popUpTo(Routes.REVIEWS_FORM) { inclusive = true }
                }
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Оставить отзыв") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (val current = state) {
                is ReviewFormViewModel.ReviewFormState.Loading -> CircularProgressIndicator()
                is ReviewFormViewModel.ReviewFormState.Error -> Text(current.message, color = Color.Red)
                else -> {}
            }
            Text(
                text = "Для доктора $doctorName",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                label = { Text("Ваш отзыв") },
                placeholder = { Text("Опишите ваши впечатления...") },
                maxLines = 8,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Оценка:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                (1..5).forEach { rating ->
                    IconButton(
                        onClick = { selectedRating = rating },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Оценка $rating",
                            tint = if (rating <= selectedRating) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.sendReview(
                        ReviewFormDto(
                            doctorId = doctorId,
                            text = reviewText,
                            grade = selectedRating.toString(),
                            patientId = patientId
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = reviewText.isNotBlank() && selectedRating > 0,
                shape = RoundedCornerShape(12.dp)
            ) {

                Text("Отправить отзыв")
            }

        }
    }
}