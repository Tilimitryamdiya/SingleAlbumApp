package ru.netology.singlealbumapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.singlealbumapp.databinding.CardTrackBinding
import ru.netology.singlealbumapp.model.Track

class TrackAdapter(
    private val listener: OnInteractionListener
) : ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardTrackBinding.inflate(inflater, parent, false)
        return TrackViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }

}

class TrackViewHolder(
    private val binding: CardTrackBinding,
    private val listener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.track.text = track.file

        binding.likeButton.setOnClickListener {
            listener.onPlayPause(track)
        }
    }

}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}

interface OnInteractionListener {
    fun onPlayPause(track: Track)
}