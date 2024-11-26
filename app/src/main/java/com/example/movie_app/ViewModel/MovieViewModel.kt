package com.example.movie_app.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.Data.ApiService
import com.example.movie_app.Data.MovieApi
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.movie_app.Data.Movie
import com.example.movie_app.Data.Room.AppDatabase
import com.example.movie_app.Data.Room.toEntity
import com.example.movie_app.Data.Room.toMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.SocketTimeoutException

class MovieViewModel(private val database: AppDatabase) : ViewModel() {

    //Creating an object used to send requests to the API
    private val movieApi = ApiService.createApi<MovieApi>()

    private val ApiKey ="ef23208db8087ca29fb3db2e258f8766"

    // List of movie form the API
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    var loading = mutableStateOf(false)

    // Current page that we get from the API
    private var currentPage by mutableIntStateOf(1)

    //List of favorite movie
    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies

    // Error message
    var errorMessage = mutableStateOf<String?>(null)

    init {
        fetchMovies("Popular")
        fetchFavoriteMovies()
    }

    /**
     * A function that reset all the variable when move from category to other category
     */
    fun resetList(){
        currentPage=1
        _movies.value = emptyList()
        errorMessage.value = null
    }

    /**
     * A function that fetches the movies from the API
     */
    fun fetchMovies(category: String) {

        loading.value = true
        viewModelScope.launch {
            errorMessage.value = null // reset the error message
            try {

                when (category) {
                    "Popular" -> {

                        //Send a request to the API to get all popular movies
                        val response = movieApi.getPopularMovies(apiKey = ApiKey , page = currentPage)

                        if (response.isSuccessful) {
                            _movies.value += response.body()?.results ?: emptyList() // add the response to the list
                            currentPage++
                        } else{
                            errorMessage.value = "Failed to fetch movies: ${response.message()}"
                        }
                    }
                    "Now Playing" -> {

                        //Send a request to the API to get all Now Play movies
                        val response = movieApi.getNowPlayingMovies(apiKey = ApiKey , page = currentPage)

                        if (response.isSuccessful) {
                            _movies.value += response.body()?.results ?: emptyList()   // add the response to the list
                            currentPage++
                        }else{
                            errorMessage.value = "Failed to fetch movies: ${response.message()}"
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                errorMessage.value = "Request timed out. Please try again."
            } catch (e: Exception) {
                errorMessage.value = "An error occurred. Please try again."
            }finally {
                loading.value = false
            }
        }
    }

    /**
     * A function to add movie to list of favorite movie and update DB
     */
    fun addToFavorite(movie: Movie) {
        viewModelScope.launch {
            database.favoriteMovieDao().insertMovie(movie.toEntity())
            fetchFavoriteMovies()
        }
    }

    /**
     * Function to remove movie from favorite and update DB
     */
    fun removeFromFavorite(movie: Movie) {
        viewModelScope.launch {
            database.favoriteMovieDao().deleteMovie(movie.toEntity())
            fetchFavoriteMovies()
        }
        _movies.value += movie
    }

    /**
     * Function fetch all the favorite movie from DB
     */
    private fun fetchFavoriteMovies() {
        viewModelScope.launch {
            val favoriteMoviesList = database.favoriteMovieDao().getAllFavoriteMovies()
            _favoriteMovies.value = favoriteMoviesList.map { it.toMovie() } // Convert MovieEntity to Movie
        }
    }
}
