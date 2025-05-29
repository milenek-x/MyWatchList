package com.milenekx.mywatchlist.data.repository

import com.milenekx.mywatchlist.data.api.RetrofitInstance.api
import com.milenekx.mywatchlist.data.model.Country
import com.milenekx.mywatchlist.data.model.GenreResponse
import com.milenekx.mywatchlist.data.model.MovieResponse
import com.milenekx.mywatchlist.data.model.TVShowResponse
import retrofit2.Response

class MovieRepository {
    private var API_KEY = "898d7082467df8e6c1bb0e8146a5f281"

    suspend fun getPopularMixedContent() = api.getTrendingMixedContent(API_KEY)

    suspend fun fetchLatestMovies(page: Int) = api.getLatestMovies(API_KEY, page)

    suspend fun fetchTrendingMovies(page: Int) = api.getTrendingMovies(API_KEY, page)

    suspend fun getPopularMovies(page: Int) = api.getTrendingMovies(API_KEY, page)

    suspend fun fetchTrendingTVShows(page: Int) = api.getTrendingTVShows(API_KEY, page)

    suspend fun fetchLatestTVShows(page: Int) = api.getLatestTVShows(API_KEY, page)

    suspend fun getPopularTVShows(page: Int) = api.getLatestTVShows(API_KEY, page)

    suspend fun getMovieGenres(): Response<GenreResponse> {
        return api.getMovieGenres(API_KEY)
    }

    suspend fun getTvGenres(): Response<GenreResponse> {
        return api.getTVGenres(API_KEY)
    }

    suspend fun getCountries(): Response<List<Country>> {
        return api.getCountries(API_KEY)
    }

    suspend fun discoverMovies(
        genres: String?,
        year: Int?,
        countries: String?,
        page: Int = 1
    ): Response<MovieResponse> {
        val genresString = genres?.toString()
        val countriesString = countries?.toString()

        return api.discoverMovies(
            apiKey = API_KEY,
            page = page,
            withGenres = genresString,
            primaryReleaseYear = year,
            withOriginCountry = countriesString
        )
    }

    suspend fun discoverTvShows(
        genres: String?,
        year: Int?,
        countries: String?,
        page: Int = 1
    ): Response<TVShowResponse> {
        val genresString = genres?.toString()
        val countriesString = countries?.toString()

        return api.discoverTvShows(
            apiKey = API_KEY,
            page = page,
            withGenres = genresString,
            firstAirDateYear = year,
            withOriginCountry = countriesString
        )
    }


    suspend fun searchMovies(query: String, page: Int = 1): Response<MovieResponse> {
        return api.searchMovies(
            apiKey = API_KEY,
            query = query,
            page = page
        )
    }

    suspend fun searchTVShows(query: String, page: Int = 1): Response<TVShowResponse> {
        return api.searchTVShows(
            apiKey = API_KEY,
            query = query,
            page = page
        )
    }

    // NEW: Function to get Movie details for homepage
    suspend fun getMovieHomepage(movieId: String): String? {
        val response = api.getMovieDetails(movieId, API_KEY)
        return if (response.isSuccessful) {
            response.body()?.homepage
        } else {
            // Log error or handle failure
            null
        }
    }

    // NEW: Function to get TV Show details for homepage
    suspend fun getTvShowHomepage(tvId: String): String? {
        val response = api.getTVDetails(tvId, API_KEY)
        return if (response.isSuccessful) {
            response.body()?.homepage
        } else {
            // Log error or handle failure
            null
        }
    }

}
