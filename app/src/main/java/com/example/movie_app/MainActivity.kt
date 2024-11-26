package com.example.movie_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movie_app.Data.Room.DatabaseProvider
import com.example.movie_app.Screen.HomeScreen
import com.example.movie_app.Screen.MovieScreen
import com.example.movie_app.ViewModel.MovieViewModel
import com.example.movie_app.ViewModel.MovieViewModelFactory
import com.example.movie_app.route.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            //initialization DB
            val db = DatabaseProvider.getDatabase(LocalContext.current)

            //initialization Navigation
            val navController = rememberNavController()

            //initialization the view model
            val viewModel: MovieViewModel = viewModel(factory = MovieViewModelFactory(db))

            NavHost(navController = navController, startDestination = "HomeScreen", builder = {
                composable(Routes.homeScreen,){ // move to home screen
                    HomeScreen(navController,viewModel)
                }
                composable(Routes.movieScreenWithId, // move to movie screen
                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                ){ backStackEntry ->
                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: -1 // get the movie ID
                    MovieScreen(navController , movieId ,viewModel)
                }
            })
        }
    }
}