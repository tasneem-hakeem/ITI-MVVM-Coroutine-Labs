package com.example.architechturestartercode.presentation.favmovies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.MoviesRepository
import com.example.architechturestartercode.data.movie.model.Movie
import kotlinx.coroutines.launch

class FavMoviesViewModel(
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val _isDeletedSuccess = MutableLiveData<Boolean>()

    fun getFavMovies(): LiveData<List<Movie>> {
        return moviesRepository.getAllFavMovies()
    }

    fun deleteFavMovie(movie: Movie) {
        viewModelScope.launch {
            moviesRepository.deleteMovieFromFav(movie)
            _isDeletedSuccess.value = true
        }
    }
}

class FavMoviesViewModelFactory(private val repository: MoviesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavMoviesViewModel(
            repository,
        ) as T
    }
}