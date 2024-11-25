package com.example.movie_app.Data

data class MovieResponse(
    val results: List<Movie>
)

//Data class that represents a movie and all the information about it
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String, //delete
    val backdrop_path: String,
    val release_date: String,
    val vote_average: Double,
    val popularity: Double
    )