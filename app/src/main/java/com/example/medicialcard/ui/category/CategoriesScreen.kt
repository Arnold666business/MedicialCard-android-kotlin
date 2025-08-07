package com.example.medicialcard.ui.category

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicialcard.data.CategoryDto
import com.example.medicialcard.ui.components.ErrorState
import com.example.medicialcard.utils.Routes
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CategoriesScreen(navController: NavController) {
    val categoryModelView: CategoryModelView = viewModel()
    val state by categoryModelView.categoriesState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is CategoryModelView.CategoriesState.Unauthorized -> {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.CATEGORY) { inclusive = true }
                }
            }

            else -> {}
        }
    }

    when (val currentState = state) {
        is CategoryModelView.CategoriesState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is CategoryModelView.CategoriesState.Success -> {
            CategoriesList(
                categories = currentState.categories,
                onCategoryClick = { id ->
                    navController.navigate("doctors/$id")
                }
            )
        }

        is CategoryModelView.CategoriesState.Error -> {
            ErrorState(
                message = currentState.message
            )
        }

        else -> {}
    }
}

@Composable
private fun CategoriesList(
    categories: List<CategoryDto>,
    onCategoryClick: (Long) -> Unit
) {


    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 55.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        item {
            Text(
                text = "Категории врачей",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(categories) { category ->
            CategoryItem(
                category = category,
                onClick = { onCategoryClick(category.categoryId) }
            )
        }
    }

}

@Composable
private fun CategoryItem(category: CategoryDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Перейти",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}