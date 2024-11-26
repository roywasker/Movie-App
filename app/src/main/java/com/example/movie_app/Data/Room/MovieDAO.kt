package com.example.movie_app.Data.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


// Interface that defines the functions of the database
@Dao
interface MovieDAO {

    @Insert
    suspend fun insertMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("SELECT * FROM favorite_movies")
    suspend fun getAllFavoriteMovies(): List<MovieEntity>
}