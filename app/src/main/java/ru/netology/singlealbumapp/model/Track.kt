package ru.netology.singlealbumapp.model

data class Track(
    val id: Long,
    val file: String,
    val isPlaying: Boolean,
    val isSelected: Boolean
)