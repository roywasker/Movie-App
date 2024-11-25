package com.example.movie_app.Screen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.movie_app.Data.Movie
import coil.request.ImageRequest
import coil.size.Size
import com.example.movie_app.ViewModel.MovieViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel: MovieViewModel = viewModel() // Get ViewModel instance

    var selectedCategory by remember { mutableStateOf("Popular") } // current category of movie

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray
                ),
                title = {
                    Text( //title of the top bar
                        text = selectedCategory,
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                },

                //set back arrow icon to go back screen
                navigationIcon = {
                    IconButton(onClick = {}) {
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
            // Category Select
            CategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    viewModel.resetList()
                    viewModel.fetchMovies(category)
                }
            )

            // show all the movie by the category
            MovieList(viewModel, selectedCategory)
        }
    }
}

/**
 * Present movie in card with all his data
 */
@Composable
fun MovieItem(movie: Movie) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Spacer(modifier = Modifier.height(8.dp))

            // present the movie name
            Text(
                text = movie.title,
                style = MaterialTheme.typography.body1,
                color = Color.Black,
                modifier = Modifier.padding(4.dp)
            )

            // Present the movie release date
            Text(
                text = movie.release_date,
                style = MaterialTheme.typography.body2,
                color = Color.Gray,
                modifier = Modifier.padding(4.dp)
            )

            // Present the movie rating
            Text(
                text = "Rating : "+movie.vote_average.toString(),
                style = MaterialTheme.typography.body2,
                color = Color.Gray,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

/**
 * Function to present all the movie in colum by 2 card in each raw
 */
@Composable
fun MovieList(viewModel: MovieViewModel, selectedCategory: String) {

    //current movie list
    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.loading

    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(8.dp)
    ) {
        items(movies.size) { index ->
            MovieItem(movie = movies[index])
        }

        //If we still wait to data form API show loading icon
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // If we in end of the colum get more movie by send new request to API
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == movies.size - 1 && !isLoading) {
                    viewModel.fetchMovies(selectedCategory)
                }
            }
    }
}

/**
 * Function to present filter category button
 */
@Composable
fun CategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly)
    {

        // List of Category
        val categories = listOf("Popular", "Now Playing", "Favorites")

        categories.forEach { category ->
            Button(
                onClick = { onCategorySelected(category) },
                modifier = Modifier.padding(horizontal = 4.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor =  Color.White ,
                    containerColor = if (category == selectedCategory) Color.LightGray else Color.DarkGray
                )
            ) {
                Text(text = category)
            }
        }
    }
}