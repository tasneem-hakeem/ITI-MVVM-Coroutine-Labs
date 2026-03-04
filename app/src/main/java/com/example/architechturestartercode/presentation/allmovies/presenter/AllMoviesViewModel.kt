package com.example.architechturestartercode.presentation.allmovies.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.MoviesRepository
import com.example.architechturestartercode.data.movie.model.Movie
import kotlinx.coroutines.launch

class AllMoviesViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _allMovies = MutableLiveData<List<Movie>>()
    val allMovies: LiveData<List<Movie>>
        get() = _allMovies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _isAddToFavSuccess = MutableLiveData<Boolean>()

    init {
        getAllMovies()
    }

    fun getAllMovies() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getAllMovies()
            result.onSuccess {
                _allMovies.value = it
                _isLoading.value = false
            }.onFailure {
                _errorMessage.value = it.message
                _isLoading.value = false
            }
        }
    }

    fun addToFav(movie: Movie) {
        viewModelScope.launch {
            repository.insertMovieToFav(movie)
            _isAddToFavSuccess.value = true
        }
    }
}

class AllMoviesViewModelFactory(private val repository: MoviesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AllMoviesViewModel(
            repository,
        ) as T
    }
}
