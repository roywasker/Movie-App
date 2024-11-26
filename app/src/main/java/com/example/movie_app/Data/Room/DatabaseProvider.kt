package com.example.movie_app.Data.Room

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    //INSTANCE of the DB
    private var INSTANCE: AppDatabase? = null

    /**
     * Function to get the DB
     */
    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "movie_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
