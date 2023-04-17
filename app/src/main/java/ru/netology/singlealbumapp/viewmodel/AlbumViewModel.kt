package ru.netology.singlealbumapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.singlealbumapp.api.AlbumApi
import ru.netology.singlealbumapp.model.Album
import ru.netology.singlealbumapp.model.Track
import ru.netology.singlealbumapp.state.PlayerModelState

class AlbumViewModel: ViewModel() {

    private val _data = MutableLiveData<Album>()
    val data: LiveData<Album>
        get() = _data

    private val _playerState = MutableLiveData<PlayerModelState>()
    val playerState: LiveData<PlayerModelState>
        get() = _playerState

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track>
        get() = _currentTrack

    init {
        loadAlbum()
    }

    fun loadAlbum() = viewModelScope.launch {
        try {
            _playerState.value = PlayerModelState(loading = true)
            val response = AlbumApi.retrofitService.loadAlbum()
            if (response.isSuccessful) {
                _data.value = response.body()
                _playerState.value = PlayerModelState()
            }
        } catch (e: Exception) {
            _playerState.value = PlayerModelState(error = true)
        }
    }

    fun play(playTrack: Track) {
        _currentTrack.value = playTrack
        _playerState.value = PlayerModelState(isPlaying = true)
        _data.value = data.value?.let { album ->
            album.copy(tracks = album.tracks.map { track ->
                if (playTrack.id == track.id) {
                    track.copy(isPlaying = true)
                } else {
                    track.copy(isPlaying = false)
                }
            })
        }
    }

    fun pause() {
        _playerState.value = PlayerModelState(isPlaying = false)
        _data.value = data.value?.let { album ->
            album.copy(tracks = album.tracks.map { track ->
                track.copy(isPlaying = false)
            })
        }
    }
}