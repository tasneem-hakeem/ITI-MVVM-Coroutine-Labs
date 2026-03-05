package com.example.architechturestartercode.presentation.favmovies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.MoviesRepository
import com.example.architechturestartercode.data.movie.model.Movie
import com.example.architechturestartercode.presentation.favmovies.state.FavMovieEvent
import com.example.architechturestartercode.presentation.favmovies.state.FavMovieUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FavMoviesViewModel(
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavMovieUiState>(FavMovieUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FavMovieEvent>()
    val events = _events.asSharedFlow()

    init {
        getFavMovies()
    }

    fun getFavMovies() {
        viewModelScope.launch {
            moviesRepository.getAllFavMovies()
                .onStart {
                    _uiState.value = FavMovieUiState.Loading
                }
                .map {
                    FavMovieUiState.Success(it)
                }
                .catch {
                    _uiState.value = FavMovieUiState.Error(it.message ?: "Error")
                }
                .collect {
                    _uiState.value = it
                }
        }
    }

    fun deleteFavMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                moviesRepository.deleteMovieFromFav(movie)
                _events.emit(FavMovieEvent.ShowMessage("${movie.title} removed from favorites successfully"))
            } catch (e: Exception) {
                _events.emit(FavMovieEvent.ShowMessage("Error removing from favorites ${e.message}"))
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FavMoviesViewModelFactory(private val repository: MoviesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavMoviesViewModel(
            repository,
        ) as T
    }
}