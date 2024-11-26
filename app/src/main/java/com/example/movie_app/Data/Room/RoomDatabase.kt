package com.example.movie_app.Data.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): MovieDAO
}