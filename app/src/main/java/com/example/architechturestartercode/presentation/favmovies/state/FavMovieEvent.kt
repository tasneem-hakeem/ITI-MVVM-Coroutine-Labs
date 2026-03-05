package com.example.architechturestartercode.presentation.favmovies.state

sealed class FavMovieEvent {
    data class ShowMessage(val message: String) : FavMovieEvent()
}