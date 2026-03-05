package com.example.architechturestartercode.data.movie.datasource.remote

import com.example.architechturestartercode.data.movie.model.Movie
import com.example.architechturestartercode.data.network.Network
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MoviesRemoteDataSource {
    private val moviesService: MoviesService = Network.moviesService

    fun getAllMovies(): Flow<List<Movie>> = flow {
        val response = moviesService.getMovies()
        if (response.isSuccessful) {
            val movies = response.body()?.results ?: emptyList()
            emit(movies)
        } else {
            throw Exception("Failed to fetch movies ${response.message()}")
        }
    }
}

