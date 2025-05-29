package com.milenekx.mywatchlist.data.model

data class MixedMediaItem(
    val id: Int,
    val title: String?,            // only for movies
    val name: String?,             // only for TV shows
    val poster_path: String?,
    val overview: String?,
    val media_type: String,        // "movie" or "tv"
    val vote_average: Double,
    val genre_ids: List<Int>,
    val year: String?,
    val homepage: String?
)

