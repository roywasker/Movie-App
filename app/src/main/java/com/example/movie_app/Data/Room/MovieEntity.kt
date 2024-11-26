package com.example.movie_app.Data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey


//define the details of the movie
@Entity(tableName = "favorite_movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val overview: String,
    val backdrop_path: String,
    val release_date: String,
    val vote_average: Double,
)
