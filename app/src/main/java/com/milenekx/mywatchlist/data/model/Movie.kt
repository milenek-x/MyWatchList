package com.milenekx.mywatchlist.data.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String,
    val genre_ids: List<Int>,
    val homepage: String?,
)

