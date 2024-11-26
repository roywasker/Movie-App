package com.example.movie_app.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movie_app.Data.Room.AppDatabase


/**
 *class that creates an instance of DB
 */
class MovieViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieViewModel(database) as T
    }
}

