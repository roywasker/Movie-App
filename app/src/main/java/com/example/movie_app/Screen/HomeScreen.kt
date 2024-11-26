package com.example.movie_app.Screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
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
import androidx.compose.material.ExperimentalMaterialApi
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
import coil.compose.rememberAsyncImagePainter
import com.example.movie_app.Data.Movie
import coil.request.ImageRequest
import coil.size.Size
import com.example.movie_app.ViewModel.MovieViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: MovieViewModel) {
    var selectedCategory by remember { mutableStateOf("Popular") } // current category of movie

    //current context
    val context = LocalContext.current
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
                    IconButton(onClick = {
                        if (context is Activity) { // Exit from app
                            context.finish()
                        }
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
            MovieList(viewModel, selectedCategory , navController)
        }
    }
}

/**
 * Present movie in card with all his data
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieItem(movie: Movie, navController: NavHostController,isSelected: Boolean = false) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .focusable()
            .background(if (isSelected) Color.Black else Color.White), // background color for current card
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        onClick ={navController.navigate("movieScreen/${movie.id}")} // Go to movie screen on click
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
            TextComp(movie.title,MaterialTheme.typography.body1 ,Color.Black )

            // Present the movie release date
            TextComp(movie.release_date,MaterialTheme.typography.body2 ,Color.Gray )

            // Present the movie rating
            TextComp("Rating : "+movie.vote_average.toString(),MaterialTheme.typography.body2 ,Color.Gray )
        }
    }
}

/**
 * Function to display Text by String color and style
 */
@Composable
fun TextComp(text: String, style: TextStyle, color: Color) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = Modifier.padding(4.dp)
    )
}

/**
 * Function to present all the movie in colum by 2 card in each raw
 */
@Composable
fun MovieList(viewModel: MovieViewModel, selectedCategory: String, navController: NavHostController) {

    //current movie list
    val movies by if (selectedCategory == "Favorites") {
        viewModel.favoriteMovies.collectAsState()
    }else{
        viewModel.movies.collectAsState()
    }
    val isLoading by viewModel.loading

    val listState = rememberLazyGridState()

    // current index of movie form lists
    var selectedIndex by remember { mutableStateOf(0) }

    // Coroutine scope for scrolling
    val coroutineScope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->  //Support for keyboard actions
                when {
                    event.key == Key.DirectionDown && event.type == KeyEventType.KeyDown -> { // click down arrow
                        if (selectedIndex < movies.size - 2) {
                            selectedIndex += 2
                            coroutineScope.launch { // allow scroll
                                listState.animateScrollToItem(selectedIndex)
                            }
                        }
                        true
                    }
                    event.key == Key.DirectionUp && event.type == KeyEventType.KeyDown -> { // click up arrow
                        if (selectedIndex >= 2) {
                            selectedIndex -= 2
                            coroutineScope.launch {// allow scroll
                                listState.animateScrollToItem(selectedIndex)
                            }
                        }
                        true
                    }
                    event.key == Key.DirectionRight && event.type == KeyEventType.KeyDown -> {  // click right arrow
                        if (selectedIndex % 2 == 0 && selectedIndex < movies.size - 1) {
                            selectedIndex++
                            coroutineScope.launch {// allow scroll
                                listState.animateScrollToItem(selectedIndex)
                            }
                        }
                        true
                    }
                    event.key == Key.DirectionLeft && event.type == KeyEventType.KeyDown -> { // click left arrow
                        if (selectedIndex % 2 == 1) {
                            selectedIndex--
                            coroutineScope.launch {// allow scroll
                                listState.animateScrollToItem(selectedIndex)
                            }
                        }
                        true
                    }
                    event.key == Key.Enter && event.type == KeyEventType.KeyDown -> {  // click enter arrow
                        navController.navigate("movieScreen/${movies[selectedIndex].id}") // go to movie details screen
                        true
                    }
                    else -> false
                }
            },
        contentPadding = PaddingValues(8.dp)
    ) {
        items(movies.size) { index ->
            MovieItem(movie = movies[index], navController, index == selectedIndex)
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

    // List of Category
    val categories = listOf("Popular", "Now Playing", "Favorites")

    // current category index form categories list
    var selectedIndex by remember { mutableStateOf(categories.indexOf(selectedCategory)) }

    Row(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .onPreviewKeyEvent { event -> // Support for keyboard actions
            when {
                event.key == Key.DirectionRight && event.type == KeyEventType.KeyDown -> { // click right arrow
                    selectedIndex = (selectedIndex + 1) % categories.size
                    true
                }
                event.key == Key.DirectionLeft && event.type == KeyEventType.KeyDown -> {// click left arrow
                    selectedIndex = (selectedIndex - 1 + categories.size) % categories.size
                    true
                }
                event.key == Key.Enter && event.type == KeyEventType.KeyDown -> {// click enter arrow
                    onCategorySelected(categories[selectedIndex])
                    true
                }
                else -> false
            }
        },
        horizontalArrangement = Arrangement.SpaceEvenly)
    {

        categories.forEachIndexed  { index , category ->
            Button(
                onClick = { onCategorySelected(category)
                            selectedIndex=index
                          },
                modifier = Modifier.padding(horizontal = 4.dp)
                    .focusable(),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor =  Color.White ,
                    containerColor = if (index == selectedIndex) Color.LightGray else Color.DarkGray
                )

            ) {
                Text(text = category)
            }
        }
    }
}