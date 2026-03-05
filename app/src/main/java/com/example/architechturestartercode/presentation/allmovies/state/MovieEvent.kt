package com.example.architechturestartercode.presentation.allmovies.state

sealed class MovieEvent {
    data class ShowMessage(val message: String) : MovieEvent()
}