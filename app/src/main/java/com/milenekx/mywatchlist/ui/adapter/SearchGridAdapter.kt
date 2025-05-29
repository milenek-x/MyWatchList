package com.milenekx.mywatchlist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.MixedMediaItem

class SearchGridAdapter(
    private var mediaList: List<MixedMediaItem>,
    private val genreMap: Map<Int, String>,
    private val onItemClick: (MixedMediaItem) -> Unit
) : RecyclerView.Adapter<SearchGridAdapter.SearchViewHolder>() {

    fun updateData(newMedia: List<MixedMediaItem>) {
        mediaList = newMedia
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_grid, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(mediaList[position])
    }

    override fun getItemCount(): Int = mediaList.size

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.imgPoster)
        private val title: TextView = itemView.findViewById(R.id.txtTitle)
        private val year : TextView = itemView.findViewById(R.id.txtYear)
        private val genres : TextView = itemView.findViewById(R.id.txtGenres)

        fun bind(mediaItem: MixedMediaItem) {
            title.text = mediaItem.title ?: mediaItem.title ?: "Untitled"

            year.text = mediaItem.year

            val genreNames = mediaItem.genre_ids.mapNotNull { genreMap[it] }
            genres.text = genreNames.joinToString(", ")


            val posterUrl = "https://image.tmdb.org/t/p/w500${mediaItem.poster_path}"
            Glide.with(itemView)
                .load(posterUrl)
                .placeholder(R.drawable.rounded_corner)
                .into(poster)

            itemView.setOnClickListener {
                onItemClick(mediaItem)
            }
        }
    }
}
