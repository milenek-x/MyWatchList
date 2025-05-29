package com.milenekx.mywatchlist.data.model

data class MixedMediaItem(
    val id: Int,
    val title: String?,
    val name: String?,
    val poster_path: String?,
    val overview: String?,
    val media_type: String,
    val vote_average: Double,
    val genre_ids: List<Int>,
    val year: String?,
    val homepage: String?
)

