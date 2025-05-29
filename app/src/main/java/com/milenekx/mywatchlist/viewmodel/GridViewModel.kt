package com.milenekx.mywatchlist.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milenekx.mywatchlist.data.model.GridMediaItem
import com.milenekx.mywatchlist.data.model.Movie
import com.milenekx.mywatchlist.data.model.TVShow
import com.milenekx.mywatchlist.data.repository.Repository
import kotlinx.coroutines.launch

class GridViewModel(private val repository: Repository) : ViewModel() {

    val gridItems = MutableLiveData<List<GridMediaItem>>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    var currentFilterGenres: List<Int>? = null
    var currentFilterYear: Int? = null
    var currentFilterCountries: List<String>? = null
    private var currentMediaType: String = "movie"
    var currentCategoryType: String = "latest"


    fun fetchFilteredMovies(
        genres: List<Int>? = null,
        year: Int? = null,
        countries: List<String>? = null
    ) {
        isLoading.postValue(true)

        currentMediaType = "movie"
        currentCategoryType = "filtered"
        currentFilterGenres = genres
        currentFilterYear = year
        currentFilterCountries = countries

        viewModelScope.launch {
            try {
                val genresString = genres?.joinToString(separator = ",")

                val countriesString = countries?.joinToString(separator = ",")

                val allFilteredMovies = mutableListOf<Movie>()

                for (page in 1..5) {
                    val response = repository.discoverMovies(
                        page = page,
                        genres = genresString,
                        year = year,
                        countries = countriesString
                    )
                    if (response.isSuccessful) {
                        response.body()?.results?.let {
                            allFilteredMovies.addAll(it)
                        }
                    } else {
                        Log.e("GridViewModel", "Failed to load filtered movies for page $page: ${
                            response.errorBody()?.string()}")
                        errorMessage.postValue("Failed to load filtered movies.")
                        break // Stop fetching if one page fails
                    }
                }
                gridItems.postValue(allFilteredMovies.map { GridMediaItem.MovieItem(it) })

            } catch (e: Exception) {
                errorMessage.postValue("Error fetching filtered movies: ${e.localizedMessage}")
                Log.e("GridViewModel", "Exception fetching filtered movies", e)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun fetchFilteredTvShows(
        genres: List<Int>? = null,
        year: Int? = null,
        countries: List<String>? = null
    ) {
        isLoading.postValue(true)

        currentMediaType = "tv"
        currentCategoryType = "filtered"
        currentFilterGenres = genres
        currentFilterYear = year
        currentFilterCountries = countries

        viewModelScope.launch {
            try {
                val genresString = genres?.joinToString(separator = ",")
                val countriesString = countries?.joinToString(separator = ",")

                val allFilteredTvShows = mutableListOf<TVShow>()

                for (page in 1..5) {
                    val response = repository.discoverTvShows(
                        page = page,
                        genres = genresString,
                        year = year,
                        countries = countriesString
                    )
                    if (response.isSuccessful) {
                        response.body()?.results?.let {
                            allFilteredTvShows.addAll(it)
                        }
                    } else {
                        Log.e("GridViewModel", "Failed to load filtered TV shows for page $page: ${
                            response.errorBody()?.string()}")
                        errorMessage.postValue("Failed to load filtered TV shows.")
                        break
                    }
                }
                gridItems.postValue(allFilteredTvShows.map { GridMediaItem.TVShowItem(it) })

            } catch (e: Exception) {
                errorMessage.postValue("Error fetching filtered TV shows: ${e.localizedMessage}")
                Log.e("GridViewModel", "Exception fetching filtered TV shows", e)
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun fetchGridItems(
        type: String,
        mediaType: String,
        filterGenres: List<Int>? = null,
        filterYear: Int? = null,
        filterCountries: List<String>? = null
    ) {
        isLoading.postValue(true)

        currentMediaType = mediaType
        currentCategoryType = type
        currentFilterGenres = filterGenres
        currentFilterYear = filterYear
        currentFilterCountries = filterCountries

        viewModelScope.launch {
            try {
                val items: List<GridMediaItem> = when (mediaType.lowercase()) {
                    "movie" -> {
                        when (type.lowercase()) {
                            "trending" -> {
                                val allTrendingMovies = mutableListOf<Movie>()
                                for (page in 1..30) {
                                    val response = repository.fetchTrendingMovies(page)
                                    if (response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allTrendingMovies.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load trending movies for page $page: ${
                                            response.errorBody()?.string()}")
                                    }
                                }
                                allTrendingMovies.map { GridMediaItem.MovieItem(it) }
                            }
                            "latest" -> {
                                val allLatestMovies = mutableListOf<Movie>()
                                for (page in 1..30) {
                                    val response = repository.fetchLatestMovies(page)
                                    if (response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allLatestMovies.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load latest movies for page $page: ${
                                            response.errorBody()?.string()}")
                                    }
                                }
                                allLatestMovies.map { GridMediaItem.MovieItem(it) }
                            }
                            "popular" -> {
                                val allPopularMovies = mutableListOf<Movie>()
                                for (page in 1..30) {
                                    val response = repository.getPopularMovies(page)
                                    if (response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allPopularMovies.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load popular movies for page $page: ${
                                            response.errorBody()?.string()}")
                                    }
                                }
                                allPopularMovies.map { GridMediaItem.MovieItem(it) }
                            }
                            else -> emptyList()
                        }
                    }
                    "tv" -> {
                        when (type.lowercase()) {
                            "trending" -> {
                                val allTrendingTvShows = mutableListOf<TVShow>()
                                for (page in 1..30) {
                                    val response = repository.fetchTrendingTVShows(page)
                                    if (response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allTrendingTvShows.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load trending TV shows for page $page: ${
                                            response.errorBody()?.string()}")
                                    }
                                }
                                allTrendingTvShows.map { GridMediaItem.TVShowItem(it) }
                            }
                            "latest" -> {
                                val allLatestTvShows = mutableListOf<TVShow>()
                                for (page in 1..30) {
                                    val response = repository.fetchLatestTVShows(page)
                                    if (response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allLatestTvShows.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load latest TV shows for page $page: ${
                                            response.errorBody()?.string()}")
                                    }
                                }
                                allLatestTvShows.map { GridMediaItem.TVShowItem(it) }
                            }
                            "popular" -> {
                                val allPopularTvShows = mutableListOf<TVShow>()
                                for (page in 1..30) {
                                    val response = repository.getPopularTVShows(page)
                                    if (response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allPopularTvShows.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load popular TV shows for page $page: ${
                                            response.errorBody()?.string()}")
                                    }
                                }
                                allPopularTvShows.map { GridMediaItem.TVShowItem(it) }
                            }
                            else -> emptyList()
                        }
                    }
                    else -> emptyList()
                }

                gridItems.postValue(items)
            } catch (e: Exception) {
                errorMessage.postValue("Failed to load grid items: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


}
