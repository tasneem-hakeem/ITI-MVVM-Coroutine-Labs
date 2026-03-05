package com.example.architechturestartercode.presentation.allmovies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.MoviesRepository
import com.example.architechturestartercode.data.movie.model.Movie
import com.example.architechturestartercode.presentation.allmovies.state.MovieEvent
import com.example.architechturestartercode.presentation.allmovies.state.MovieUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AllMoviesViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MovieEvent>()
    val events = _events.asSharedFlow()

    init {
        getAllMovies()
    }

    fun getAllMovies() {
        viewModelScope.launch {
            repository.getAllMovies()
                .onStart {
                    _uiState.value = MovieUiState.Loading
                }
                .map { result ->
                    result.fold(
                        onSuccess = { MovieUiState.Success(it) },
                        onFailure = {
                            MovieUiState.Error(
                                it.message ?: "Error"
                            )
                        }
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun addToFav(movie: Movie) {
        viewModelScope.launch {
            try {
                repository.insertMovieToFav(movie)
                _events.emit(MovieEvent.ShowMessage("${movie.title} added to favorites successfully"))
            } catch (e: Exception) {
                _events.emit(MovieEvent.ShowMessage("Error adding to favorites ${e.message}"))
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AllMoviesViewModelFactory(private val repository: MoviesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AllMoviesViewModel(
            repository,
        ) as T
    }
}
