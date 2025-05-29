package com.milenekx.mywatchlist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.TVShow

class HorizontalTVAdapter(
    private var tvShows: List<TVShow>,
    private val onItemClick: (TVShow) -> Unit  // Changed from Movie to TVShow
) : RecyclerView.Adapter<HorizontalTVAdapter.HorizontalTVViewHolder>() {

    inner class HorizontalTVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.ivPoster)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalTVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horizontal_movie, parent, false)
        return HorizontalTVViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalTVViewHolder, position: Int) {
        val tvShow = tvShows[position]
        holder.tvTitle.text = tvShow.name
        holder.tvTitle.isSelected = true
        val posterUrl = "https://image.tmdb.org/t/p/w500${tvShow.posterPath}"
        Glide.with(holder.itemView.context).load(posterUrl).into(holder.ivPoster)
        holder.itemView.setOnClickListener { onItemClick(tvShow) }
    }

    override fun getItemCount() = tvShows.size

    fun updateData(newTVShows: List<TVShow>) {
        tvShows = newTVShows
        notifyDataSetChanged()
    }
}
