package com.example.architechturestartercode.data.movie.datasource.remote

import com.example.architechturestartercode.data.movie.model.Movie
import com.example.architechturestartercode.data.network.Network

class MoviesRemoteDataSource {
    private val moviesService: MoviesService = Network.moviesService

    suspend fun getAllMovies(): Result<List<Movie>> {
        val response = moviesService.getMovies()
        if (response.isSuccessful) {
            val movies = response.body()?.results ?: emptyList()
            return Result.success(movies)
        } else {
            val exception = Exception("Failed to fetch movies ${response.message()}")
            return Result.failure(exception)
        }
    }
}

