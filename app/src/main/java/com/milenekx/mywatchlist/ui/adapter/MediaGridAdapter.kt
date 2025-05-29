package com.milenekx.mywatchlist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.GridMediaItem

class MediaGridAdapter(
    private var items: List<GridMediaItem>,
    private val onItemClick: (GridMediaItem) -> Unit
) : RecyclerView.Adapter<MediaGridAdapter.MediaGridViewHolder>() {

    inner class MediaGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.ivPoster)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaGridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return MediaGridViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaGridViewHolder, position: Int) {
        val item = items[position]

        val title: String
        val posterPath: String?

        when (item) {
            is GridMediaItem.MovieItem -> {
                title = item.movie.title
                posterPath = item.movie.poster_path
            }
            is GridMediaItem.TVShowItem -> {
                title = item.tvShow.name.toString()
                posterPath = item.tvShow.poster_path
            }
        }

        holder.tvTitle.text = title
        val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
        Glide.with(holder.itemView.context).load(posterUrl).into(holder.ivPoster)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<GridMediaItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
