package ru.netology.singlealbumapp.state

data class PlayerModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val isPlaying: Boolean = false
)
