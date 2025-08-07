package com.example.medicialcard

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medicialcard.api.AuthApi
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.ui.appointment.AppointmentScreen
import com.example.medicialcard.ui.category.CategoriesScreen
import com.example.medicialcard.ui.doctor.DoctorScreen
import com.example.medicialcard.ui.doctorsInCategory.DoctorsListScreen
import com.example.medicialcard.ui.login.LoginScreen
import com.example.medicialcard.ui.patient.PatientScreen
import com.example.medicialcard.ui.register.RegisterScreen
import com.example.medicialcard.ui.review.ReviewFormScreen
import com.example.medicialcard.ui.review.reviewsList.ReviewsListScreen
import com.example.medicialcard.ui.start.StartScreen
import com.example.medicialcard.ui.theme.MedicialCardTheme
import com.example.medicialcard.utils.JwtManager
import com.example.medicialcard.utils.Routes
import okhttp3.Route


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicialCardTheme {
                NavGraph()
            }
        }
    }
}

@Composable
fun NavGraph() {
    val viewModel: MainViewModel = viewModel()
    val navController = rememberNavController()
    val startDestination = viewModel.startDestination.collectAsState().value


    if (startDestination != null) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(route = Routes.START) {
                StartScreen(navController)
            }
            composable(route = Routes.LOGIN) {
                LoginScreen(navController)
            }
            composable(route = Routes.REGISTER) {
                RegisterScreen(navController)
            }
            composable(route = Routes.PATIENT) {
                PatientScreen(navController)
            }
            composable(route = Routes.APPOINTMENTS) {
                AppointmentScreen(navController)
            }
            composable(route = Routes.CATEGORY) {
                CategoriesScreen(navController)
            }
            composable(route = Routes.DOCTORS_LIST,
                arguments = listOf(navArgument("categoryId") {type = NavType.StringType})
            ){
                backStackEntry -> DoctorsListScreen(
                    navController,
                backStackEntry.arguments?.getString("categoryId")?.toLong() ?: 0L
                )
            }
            composable(route = Routes.DOCTOR_DETAIL,
                arguments = listOf(navArgument("doctorId") {type = NavType.StringType})
                ) {
                backStackEntry -> DoctorScreen(navController,
                    backStackEntry.arguments?.getString("doctorId")?.toLong() ?: 0L
                    )
            }
            composable(route = Routes.REVIEWS_FORM,
                arguments = listOf(navArgument("doctorId") {type = NavType.StringType})
                ) {
                backStackEntry ->
                ReviewFormScreen(navController, backStackEntry.arguments?.getString("doctorId")?.toLong() ?: 0L)
            }
            composable(route = Routes.REVIEWS,
                arguments = listOf(navArgument("doctorId") {type = NavType.StringType})
            ){
                backStackEntry ->
                ReviewsListScreen(navController, backStackEntry.arguments?.getString("doctorId")?.toLong() ?: 0L)
            }
        }
    } else {
        CircularProgressIndicator()
    }
}






