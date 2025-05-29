package com.milenekx.mywatchlist.data.model

import com.google.gson.annotations.SerializedName

data class TVShow(
    val id: Int,
    val name: String?,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("first_air_date")
    val firstAirDate: String,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    val homepage: String?
)