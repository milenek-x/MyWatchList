package com.milenekx.mywatchlist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.MixedMediaItem

class PopularAdapter(
    private var mixedMediaItems: List<MixedMediaItem>,
    private val onDetailClick: (MixedMediaItem) -> Unit,
    private val onWatchClick: (MixedMediaItem) -> Unit,
) : RecyclerView.Adapter<PopularAdapter.PopularMovieViewHolder>() {

    val realItemCount: Int
        get() = mixedMediaItems.size

    inner class PopularMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.ivPoster)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvGenre: TextView = itemView.findViewById(R.id.tvGenre)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val btnDetail: Button = itemView.findViewById(R.id.btnDetail)
        val btnTrailer: Button = itemView.findViewById(R.id.btnWatch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular_movie, parent, false)
        return PopularMovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularMovieViewHolder, position: Int) {
        if (mixedMediaItems.isEmpty()) return

        val realPosition = position % mixedMediaItems.size
        val item = mixedMediaItems[realPosition]

        holder.tvTitle.text = item.title ?: item.name ?: "Unknown Title"
        holder.tvTitle.isSelected = true

        val genreNames = item.genre_ids.mapNotNull { genreId ->
            genreMap[genreId]
        }.joinToString(", ")
        holder.tvGenre.text = genreNames

        holder.tvType.text = when (item.media_type) {
            "movie" -> "Movie"
            "tv" -> "TV Series"
            else -> "Unknown"
        }

        val posterUrl = "https://image.tmdb.org/t/p/w500${item.poster_path}"
        Glide.with(holder.itemView.context).load(posterUrl).into(holder.ivPoster)

        holder.btnDetail.setOnClickListener { onDetailClick(item) }
        holder.btnTrailer.setOnClickListener { onWatchClick(item) }
    }


    override fun getItemCount(): Int = if (mixedMediaItems.isEmpty()) 0 else Int.MAX_VALUE

    fun updateData(newMovies: List<MixedMediaItem>) {
        mixedMediaItems = newMovies
        notifyDataSetChanged()
    }

    companion object {
        private val genreMap = mapOf(
            28 to "Action",
            12 to "Adventure",
            16 to "Animation",
            35 to "Comedy",
            80 to "Crime",
            99 to "Documentary",
            18 to "Drama",
            10751 to "Family",
            14 to "Fantasy",
            36 to "History",
            27 to "Horror",
            10402 to "Music",
            9648 to "Mystery",
            10749 to "Romance",
            878 to "Science Fiction",
            10770 to "TV Movie",
            53 to "Thriller",
            10752 to "War",
            37 to "Western"
        )
    }
}
