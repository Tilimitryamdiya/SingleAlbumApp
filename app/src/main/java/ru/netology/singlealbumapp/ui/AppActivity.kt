package ru.netology.singlealbumapp.ui

import android.media.AudioAttributes
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import ru.netology.singlealbumapp.BuildConfig
import ru.netology.singlealbumapp.R
import ru.netology.singlealbumapp.adapter.OnInteractionListener
import ru.netology.singlealbumapp.adapter.TrackAdapter
import ru.netology.singlealbumapp.databinding.ActivityAppBinding
import ru.netology.singlealbumapp.model.Track
import ru.netology.singlealbumapp.viewmodel.AlbumViewModel

class AppActivity : AppCompatActivity() {
    private val mediaObserver = MediaLifecycleObserver()
    lateinit var binding: ActivityAppBinding
    private val viewModel: AlbumViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(mediaObserver)

        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlayPause(track: Track) {
                playPause(track)
            }
        })

        binding.list.adapter = adapter

        binding.headerPlayButton.setOnClickListener {
            viewModel.currentTrack.value?.let {
                playPause(it)
            } ?: viewModel.data.value?.tracks?.get(0)?.let { playPause(it) }
        }

        viewModel.data.observe(this) {
            adapter.submitList(it?.tracks)
            with(binding) {
                albumTitle.text = it.title
                artistTitle.text = it.artist
                albumYear.text = it.published
                musicGenre.text = it.genre
            }
        }

        viewModel.playerState.observe(this) { state ->
            binding.headerPlayButton.isChecked = state.isPlaying
            binding.progress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadAlbum() }
                    .show()
            }


        }

        binding.seekbar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) mediaObserver.player?.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            }
        )

        mediaObserver.player?.setOnCompletionListener {
            playNextTrack()
        }
    }

    private fun playPause(track: Track) {

        if (track.id != viewModel.currentTrack.value?.id) {
            mediaObserver.reset()
            mediaObserver.apply {
                player?.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                player?.setDataSource(BuildConfig.BASE_URL + track.file)
            }.play()
            viewModel.play(track)
            initialiseSeekBar()
        } else {
            if (mediaObserver.player?.isPlaying == true) {
                mediaObserver.pause()
                viewModel.pause()
            } else {
                mediaObserver.resume()
                viewModel.play(track)
            }
        }

    }


    private fun initialiseSeekBar() = with(binding) {
        seekbar.max = mediaObserver.player?.duration ?: 0

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekbar.progress = mediaObserver.player?.currentPosition!!
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    seekbar.progress = 0
                }
            }
        }, 0)
    }

    private fun playNextTrack() {
        viewModel.data.value?.let {
            if (viewModel.currentTrack.value?.id?.toInt() == it.tracks.size) {
                playPause(it.tracks[0])
            } else {
                playPause(it.tracks[viewModel.currentTrack.value?.id?.toInt() ?: 0])
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }
}
