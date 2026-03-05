package com.example.architechturestartercode.presentation.allmovies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.MoviesRepository
import com.example.architechturestartercode.data.movie.model.Movie
import com.example.architechturestartercode.presentation.allmovies.state.MovieEvent
import com.example.architechturestartercode.presentation.allmovies.state.MovieUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AllMoviesViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MovieEvent>()
    val events = _events.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())

    init {
        observeMovies()
        getAllMovies()
    }

    fun getAllMovies() {
        viewModelScope.launch {
            repository.getAllMovies()
                .onStart { _uiState.value = MovieUiState.Loading }
                .collect { result ->
                    result.fold(
                        onSuccess = { movies ->
                            _allMovies.value = movies
                        },
                        onFailure = {
                            _uiState.value = MovieUiState.Error(
                                it.message ?: "Error"
                            )
                        }
                    )
                }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeMovies() {
        viewModelScope.launch {
            combine(
                _allMovies,
                _searchQuery.debounce(300)
            ) { movies, query ->
                if (query.isBlank()) {
                    movies
                } else {
                    movies.filter {
                        it.title.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filteredMovies ->
                _uiState.value = MovieUiState.Success(filteredMovies)
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

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
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
