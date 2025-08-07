package com.example.medicialcard.ui.review.reviewsList

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.medicialcard.data.ReviewFormDto
import com.example.medicialcard.ui.components.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsListScreen(navController: NavController, doctorId: Long) {
    val viewModel: ReviewsListViewModel = viewModel(factory = ReviewsListViewModel.Factory(doctorId))
    val state by viewModel.reviewsState.collectAsState()
    val doctorName by viewModel.doctorName.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        if (state is ReviewsListViewModel.ReviewsState.Error) {
            val error = (state as ReviewsListViewModel.ReviewsState.Error).message
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Отзывы о докторе") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Star, contentDescription = "Назад")//star
                    }
                }
            )
        }
    ) { padding ->
        when (val currentState = state) {
            is ReviewsListViewModel.ReviewsState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            }
            is ReviewsListViewModel.ReviewsState.Success -> {
                ReviewsContent(
                    doctorName = doctorName,
                    reviews = currentState.reviews,
                    getPatientName = { viewModel.getPatientName(it) },
                    modifier = Modifier.padding(padding)
                )
            }
            is ReviewsListViewModel.ReviewsState.Error -> {
                ErrorState(message = (state as ReviewsListViewModel.ReviewsState.Error).message)
            }
            else -> {}
        }
    }
}

@Composable
private fun ReviewsContent(
    doctorName: String,
    reviews: List<ReviewFormDto>,
    getPatientName: (Long) -> String?,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        Text(
            text = doctorName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews) { review ->
                ReviewCard(
                    review = review,
                    patientName = getPatientName(review.patientId) ?: "Пациент"
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewFormDto, patientName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = patientName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                val rating = try { review.grade.toInt() } catch (e: Exception) { 0 }
                RatingStars(rating = rating)
            }

            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun RatingStars(rating: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(4.dp))
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}