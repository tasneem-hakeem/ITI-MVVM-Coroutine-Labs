package com.example.architechturestartercode.presentation.allmovies.state

import com.example.architechturestartercode.data.movie.model.Movie

sealed class MovieUiState {
    object Loading : MovieUiState()
    data class Success(val movies: List<Movie>) : MovieUiState()
    data class Error(val message: String) : MovieUiState()
}