package com.milenekx.mywatchlist.data.api

import com.milenekx.mywatchlist.data.model.Country
import com.milenekx.mywatchlist.data.model.GenreResponse
import com.milenekx.mywatchlist.data.model.ItemDetailsResponse
import com.milenekx.mywatchlist.data.model.MovieResponse
import com.milenekx.mywatchlist.data.model.TVShowResponse
import com.milenekx.mywatchlist.data.model.MixedMediaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiService {

    @GET("trending/all/day")
    suspend fun getTrendingMixedContent(
        @Query("api_key") apiKey: String
    ): Response<MixedMediaResponse>

    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    @GET("trending/tv/day")
    suspend fun getTrendingTVShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TVShowResponse>

    @GET("movie/now_playing")
    suspend fun getLatestMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    @GET("tv/popular")
    suspend fun getLatestTVShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TVShowResponse>

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String
    ): Response<GenreResponse>

    @GET("genre/tv/list")
    suspend fun getTVGenres(
        @Query("api_key") apiKey: String
    ): Response<GenreResponse>

    @GET("configuration/countries")
    suspend fun getCountries(
        @Query("api_key") apiKey: String
    ): Response<List<Country>>

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("with_genres") withGenres: String? = null,
        @Query("primary_release_year") primaryReleaseYear: Int? = null,
        @Query("with_origin_country") withOriginCountry: String? = null
    ): Response<MovieResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    @GET("discover/tv")
    suspend fun discoverTvShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("with_genres") withGenres: String? = null,
        @Query("first_air_date_year") firstAirDateYear: Int? = null,
        @Query("with_origin_country") withOriginCountry: String? = null
    ): Response<TVShowResponse>

    @GET("search/tv")
    suspend fun searchTVShows(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<TVShowResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String
    ): Response<ItemDetailsResponse>

    @GET("tv/{tv_id}")
    suspend fun getTVDetails(
        @Path("tv_id") tvId: String,
        @Query("api_key") apiKey: String
    ): Response<ItemDetailsResponse>

}
