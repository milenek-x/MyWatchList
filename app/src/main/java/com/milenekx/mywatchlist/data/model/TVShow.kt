package com.milenekx.mywatchlist.data.model

data class TVShow(
    val id: Int,
    val name: String?,
    val overview: String,
    val poster_path: String?,
    val first_air_date: String,
    val genre_ids: List<Int>,
    val homepage: String?
)
