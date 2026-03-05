package com.example.architechturestartercode.data.movie

import android.app.Application
import com.example.architechturestartercode.data.movie.datasource.local.MoviesLocalDataSource
import com.example.architechturestartercode.data.movie.datasource.remote.MoviesRemoteDataSource
import com.example.architechturestartercode.data.movie.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class MoviesRepository(application: Application) {
    private val remoteDataSource = MoviesRemoteDataSource()
    private val localDataSource = MoviesLocalDataSource(application)

    fun getAllMovies(): Flow<Result<List<Movie>>> {
        return remoteDataSource.getAllMovies()
            .map { movies ->
                Result.success(movies)
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    suspend fun insertMovieToFav(movie: Movie) {
        localDataSource.insertMovie(movie)
    }

    suspend fun deleteMovieFromFav(movie: Movie) {
        localDataSource.deleteMovie(movie)
    }

    fun getAllFavMovies(): Flow<List<Movie>> {
        return localDataSource.getAllMovies()
    }
}

