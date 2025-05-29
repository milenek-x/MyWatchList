package com.milenekx.mywatchlist.data.model


data class ItemDetailsResponse(
    val id: Int,
    val title: String?,                // for movies
    val name: String?,                 // for TV
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val genres: List<Genre>?,
    val homepage: String?,
    val original_language: String,
    val original_title: String?,       // for movies
    val original_name: String?,        // for TV
    val release_date: String?,         // for movies
    val first_air_date: String?,       // for TV
    val runtime: Int?,                 // for movies
    val episode_run_time: List<Int>?,  // for TV
    val status: String?,
    val vote_average: Double,
    val vote_count: Int,
    val tagline: String?,
    val production_companies: List<ProductionCompany>?,
    val production_countries: List<ProductionCountry>?,
    val spoken_languages: List<SpokenLanguage>?
)
