package com.example.architechturestartercode.presentation.favmovies.state

import com.example.architechturestartercode.data.movie.model.Movie

sealed class FavMovieUiState {
    object Loading : FavMovieUiState()
    data class Success(val movies: List<Movie>) : FavMovieUiState()
    data class Error(val message: String) : FavMovieUiState()
}