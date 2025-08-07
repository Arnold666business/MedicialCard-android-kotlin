package com.example.medicialcard.ui.doctorsInCategory

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.util.Logger
import com.example.medicialcard.data.DoctorDto
import com.example.medicialcard.ui.components.ErrorState
import com.example.medicialcard.utils.Routes
import com.example.medicialcard.R
import com.example.medicialcard.ui.DoctorImageResolver
import com.example.medicialcard.utils.Endpoints

@Composable
fun DoctorsListScreen(navController: NavController, categoryId: Long) {
    val viewModel: DoctorsListViewModel =
        viewModel(factory = DoctorsListViewModel.Factory(categoryId))
    val state by viewModel.doctorsListState.collectAsState()
    val title by viewModel.categoryTitle.collectAsState()


    when (val currentState = state) {
        is DoctorsListViewModel.DoctorListState.Loading -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        }

        is DoctorsListViewModel.DoctorListState.Success -> {
            DoctorsContent(title = title,
                doctors = currentState.doctorsList,
                filterActive = currentState.isActiveFilter,
                onFilterClick = { viewModel.toggleRatingFilter() },
                onDoctorClick = { id -> navController.navigate("doctor/$id") })
        }

        is DoctorsListViewModel.DoctorListState.Error -> {
            ErrorState(message = currentState.message)
        }

        else -> {}
    }
}

@Composable
private fun DoctorsContent(
    title: String,
    doctors: List<DoctorDto>,
    filterActive: Boolean,
    onFilterClick: () -> Unit,
    onDoctorClick: (Long) -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(bottom = 40.dp   ),

        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title, style = MaterialTheme.typography.titleLarge
                )
                FilterButton(active = filterActive, onClick = onFilterClick)
            }
        }
        items(doctors) { doctor ->
            DoctorCard(doctor = doctor, onClick = { onDoctorClick(doctor.doctorId) })
        }
    }

}

@Composable
private fun FilterButton(active: Boolean, onClick: () -> Unit) {
    val containerColor = if (active) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (active) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(containerColor)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Фильтр по рейтингу",
            tint = contentColor
        )
    }
}

@Composable
private fun DoctorCard(doctor: DoctorDto, onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = DoctorImageResolver.getImageResource(doctor.doctorId),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = "${doctor.firstName} ${doctor.lastName}",
                    style = MaterialTheme.typography.bodyMedium
                )

                RatingBar(rating = doctor.rating)
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
private fun RatingBar(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Рейтинг",
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(16.dp)
        )
    }
}

