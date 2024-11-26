package com.example.movie_app.Data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//An interface for sending requests to the API to get the movies by category
interface MovieApi {

    // Send request to popular movie
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    // Send request to now playing movie
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>
}