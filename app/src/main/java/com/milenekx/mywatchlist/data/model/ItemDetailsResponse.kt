package com.milenekx.mywatchlist.data.model

import com.google.gson.annotations.SerializedName

data class ItemDetailsResponse(
    val id: Int,
    val title: String?,
    val name: String?,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    val genres: List<Genre>?,
    val homepage: String?,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String?,
    @SerializedName("original_name")
    val originalName: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("first_air_date")
    val firstAirDate: String?,
    val runtime: Int?,
    @SerializedName("episode_run_time")
    val episodeRunTime: List<Int>?,
    val status: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    val tagline: String?,
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>?,
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry>?,
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>?
)