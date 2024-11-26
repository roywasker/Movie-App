package com.example.movie_app.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.Data.ApiService
import com.example.movie_app.Data.MovieApi
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.movie_app.Data.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MovieViewModel : ViewModel() {

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


    init {
        fetchMovies("Popular")
    }

    /**
     * A function that reset all the variable when move from category to other category
     */
    fun resetList(){
        currentPage=1
        _movies.value = emptyList()
    }

    /**
     * A function that fetches the movies from the API
     */
    fun fetchMovies(category: String) {

        loading.value = true
        viewModelScope.launch {
            try {

                when (category) {
                    "Popular" -> {

                        //Send a request to the API to get all popular movies
                        val response = movieApi.getPopularMovies(apiKey = ApiKey , page = currentPage)
                        _movies.value += response.results // add the response to the list
                        currentPage++
                    }
                    "Now Playing" -> {

                        //Send a request to the API to get all Now Play movies
                        val response = movieApi.getNowPlayingMovies(apiKey = ApiKey , page = currentPage)
                        _movies.value += response.results   // add the response to the list
                        currentPage++
                    }
                }
            } catch (e: Exception) {

                // add popup to say that we have error and try again
                Log.e("Movies", "Error: ${e.message}")
            }finally {
                loading.value = false
            }
        }
    }

    /**
     * A function to add movie to list of favorite movie
     */
    fun addToFavorites(movieId : Int) {
        val inFavorite = favoriteMovies.value.find { it.id == movieId } // Check if the movie is in the list.
        val movie = movies.value.find { it.id == movieId }
        if (inFavorite == null){
            if (movie != null) {
                _favoriteMovies.value += movie
            }
        }
    }

    /**
     * A function to delete movie from list of favorite movie
     */
    fun deleteFromFavorites(movieId : Int) {
        val updatedFavorites = favoriteMovies.value.filter { it.id != movieId } // find all movie without the movie to delete
        val deleteMovie = favoriteMovies.value.filter { it.id == movieId } // find the movie to delete
        _favoriteMovies.value = updatedFavorites
        _movies.value += deleteMovie
    }
}
