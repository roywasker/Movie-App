package com.example.movie_app.Data.Room

import com.example.movie_app.Data.Movie


/**
 * Function to convert MovieEntity object to Movie object
 */
fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        overview = this.overview,
        release_date = this.release_date,
        vote_average = this.vote_average,
        backdrop_path = this.backdrop_path
    )
}

/**
 * Function to convert Movie object to Movie objectEntity
 */
fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = this.id,
        title = this.title,
        overview = this.overview,
        release_date = this.release_date,
        vote_average = this.vote_average,
        backdrop_path = this.backdrop_path
    )
}