package com.example.movie_app.Screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.movie_app.Data.Movie
import com.example.movie_app.ViewModel.MovieViewModel
import com.example.movie_app.route.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(navController: NavHostController, movieId: Int , viewModel: MovieViewModel ) {

    // Get movie and favorite movie list
    val movies = viewModel.movies.collectAsState(initial = emptyList())
    val favoriteMovies = viewModel.favoriteMovies.collectAsState(initial = emptyList())

    // check if current movie is in favorite
    val inFavorite = remember(favoriteMovies.value) {
        favoriteMovies.value.find { it.id == movieId } != null
    }

    // Get instance of the movie
    val movie = if (!inFavorite){
        movies.value.find { it.id == movieId }
    }else{
        favoriteMovies.value.find { it.id == movieId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray
                ),
                title = {
                    if (movie != null) {
                        Text(
                            text = movie.title,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }
                },

                //set back arrow icon to go back screen
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetList()
                        viewModel.fetchMovies("Popular")
                        navController.navigate(Routes.homeScreen)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .padding(start = 6.dp, end = 8.dp)
                                .size(40.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // present movie details
            MovieDetails(movie, viewModel,inFavorite)
        }
    }
}


/**
 * present movie details by movie instance
 */
@Composable
fun MovieDetails(
    movie: Movie?,
    viewModel: MovieViewModel,
    inFavorite: Boolean,
) {
    if (movie == null) return

    // Get the image of poster from the API
    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://image.tmdb.org/t/p/w500" + movie.backdrop_path)
            .size(Size.ORIGINAL)
            .build()
    ).state

    // Present the poster of the movie
    imageState.painter?.let {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .height(250.dp)
                .clip(RoundedCornerShape(22.dp)),
            painter = it,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop
        )
    }

    // Present the name of the movie
    Text(
        text = movie.title,
        modifier = Modifier.padding(8.dp),
        fontSize = 18.sp
    )

    // Present the Release Date of the movie
    Text(
        text = "Release Date: ${movie.release_date}",
        modifier = Modifier.padding(8.dp),
        fontSize = 18.sp
    )

    // Present the rating of the movie
    Text(
        text = "Rating: ${movie.vote_average}",
        modifier = Modifier.padding(8.dp),
        fontSize = 18.sp
    )

    // Present the Overview the movie
    Text(
        text = "Overview:",
        modifier = Modifier.padding(8.dp),
        fontSize = 18.sp
    )

    BasicText(
        text = movie.overview,
        modifier = Modifier.padding(8.dp)
    )

    // button to add the movie to favorite
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick ={
            if (inFavorite){
                viewModel.deleteFromFavorites(movie.id)
            }else{
                viewModel.addToFavorites(movie.id)
            }},
            modifier = Modifier.padding(horizontal = 4.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor =  Color.White ,
                containerColor = Color.DarkGray
            )){
            Text(text = if (!inFavorite) "add to favorite" else "remove from favorite" ,
                fontSize = 18.sp
            )
        }
    }
}