package com.milenekx.mywatchlist.data.repository

import com.milenekx.mywatchlist.data.api.RetrofitInstance.api
import com.milenekx.mywatchlist.data.model.Country
import com.milenekx.mywatchlist.data.model.GenreResponse
import com.milenekx.mywatchlist.data.model.MovieResponse
import com.milenekx.mywatchlist.data.model.TVShowResponse
import retrofit2.Response

class Repository {
    private var apiKey = "898d7082467df8e6c1bb0e8146a5f281"

    suspend fun getPopularMixedContent() = api.getTrendingMixedContent(apiKey)

    suspend fun fetchLatestMovies(page: Int) = api.getLatestMovies(apiKey, page)

    suspend fun fetchTrendingMovies(page: Int) = api.getTrendingMovies(apiKey, page)

    suspend fun getPopularMovies(page: Int) = api.getTrendingMovies(apiKey, page)

    suspend fun fetchTrendingTVShows(page: Int) = api.getTrendingTVShows(apiKey, page)

    suspend fun fetchLatestTVShows(page: Int) = api.getLatestTVShows(apiKey, page)

    suspend fun getPopularTVShows(page: Int) = api.getLatestTVShows(apiKey, page)

    suspend fun getMovieGenres(): Response<GenreResponse> {
        return api.getMovieGenres(apiKey)
    }

    suspend fun getTvGenres(): Response<GenreResponse> {
        return api.getTVGenres(apiKey)
    }

    suspend fun getCountries(): Response<List<Country>> {
        return api.getCountries(apiKey)
    }

    suspend fun discoverMovies(
        genres: String?,
        year: Int?,
        countries: String?,
        page: Int = 1
    ): Response<MovieResponse> {

        return api.discoverMovies(
            apiKey = apiKey,
            page = page,
            withGenres = genres,
            primaryReleaseYear = year,
            withOriginCountry = countries
        )
    }

    suspend fun discoverTvShows(
        genres: String?,
        year: Int?,
        countries: String?,
        page: Int = 1
    ): Response<TVShowResponse> {

        return api.discoverTvShows(
            apiKey = apiKey,
            page = page,
            withGenres = genres,
            firstAirDateYear = year,
            withOriginCountry = countries
        )
    }


    suspend fun searchMovies(query: String, page: Int = 1): Response<MovieResponse> {
        return api.searchMovies(
            apiKey = apiKey,
            query = query,
            page = page
        )
    }

    suspend fun searchTVShows(query: String, page: Int = 1): Response<TVShowResponse> {
        return api.searchTVShows(
            apiKey = apiKey,
            query = query,
            page = page
        )
    }

    // NEW: Function to get Movie details for homepage
    suspend fun getMovieHomepage(movieId: String): String? {
        val response = api.getMovieDetails(movieId, apiKey)
        return if (response.isSuccessful) {
            response.body()?.homepage
        } else {
            null
        }
    }

    // NEW: Function to get TV Show details for homepage
    suspend fun getTvShowHomepage(tvId: String): String? {
        val response = api.getTVDetails(tvId, apiKey)
        return if (response.isSuccessful) {
            response.body()?.homepage
        } else {
            null
        }
    }

}
