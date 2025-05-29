package com.milenekx.mywatchlist.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milenekx.mywatchlist.data.model.GridMediaItem
import com.milenekx.mywatchlist.data.model.Movie
import com.milenekx.mywatchlist.data.model.TVShow
import com.milenekx.mywatchlist.data.repository.MovieRepository
import kotlinx.coroutines.launch

class GridViewModel(private val repository: MovieRepository) : ViewModel() {

    val gridItems = MutableLiveData<List<GridMediaItem>>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    // Hold the current filter state
    // You might want to define a data class for FilterCriteria { val genres: List<Int>?, val year: Int?, val countries: List<String>? }
    var currentFilterGenres: List<Int>? = null
    var currentFilterYear: Int? = null
    var currentFilterCountries: List<String>? = null
    var currentMediaType: String = "movie" // Keep track of current media type
    var currentCategoryType: String = "latest" // Keep track of current category type


    // --- NEW FUNCTION FOR FILTERING ---
    fun fetchFilteredMovies(
        genres: List<Int>? = null,
        year: Int? = null,
        countries: List<String>? = null
    ) {
        isLoading.postValue(true)

        // Update current filter state
        currentMediaType = "movie" // When applying specific filters, it's usually for movies or TV
        currentCategoryType = "filtered" // Indicate that items are now filtered
        currentFilterGenres = genres
        currentFilterYear = year
        currentFilterCountries = countries

        viewModelScope.launch {
            try {
                // Convert genre IDs to comma-separated string if not null
                val genresString = genres?.joinToString(separator = ",")
                // Convert country codes to comma-separated string if not null
                val countriesString = countries?.joinToString(separator = ",")

                val allFilteredMovies = mutableListOf<Movie>()
                // Fetch first few pages for filtered results, e.g., 5 pages
                for (page in 1..5) { // You can adjust the number of pages
                    val response = repository.discoverMovies(
                        page = page,
                        genres = genresString,
                        year = year,
                        countries = countriesString
                    )
                    if (response != null && response.isSuccessful) {
                        response.body()?.results?.let {
                            allFilteredMovies.addAll(it)
                        }
                    } else {
                        Log.e("GridViewModel", "Failed to load filtered movies for page $page: ${response?.errorBody()?.string()}")
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

        // Update current filter state
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
                // Fetch first few pages for filtered results
                for (page in 1..5) {
                    val response = repository.discoverTvShows(
                        page = page,
                        genres = genresString,
                        year = year,
                        countries = countriesString
                    )
                    if (response != null && response.isSuccessful) {
                        response.body()?.results?.let {
                            allFilteredTvShows.addAll(it)
                        }
                    } else {
                        Log.e("GridViewModel", "Failed to load filtered TV shows for page $page: ${response?.errorBody()?.string()}")
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
        isLoading.postValue(true) // Set loading to true when starting fetch

        // Update current filter state
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
                                val allTrendingMovies = mutableListOf<Movie>() // Assuming 'Movie' is your data class for a single movie
                                for (page in 1..30) {
                                    val response = repository.fetchTrendingMovies(page) // Pass page
                                    if (response != null && response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allTrendingMovies.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load trending movies for page $page: ${response?.errorBody()?.string()}")
                                    }
                                }
                                allTrendingMovies.map { GridMediaItem.MovieItem(it) }
                            }
                            "latest" -> {
                                val allLatestMovies = mutableListOf<Movie>()
                                for (page in 1..30) {
                                    val response = repository.fetchLatestMovies(page)
                                    if (response != null && response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allLatestMovies.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load latest movies for page $page: ${response?.errorBody()?.string()}")
                                    }
                                }
                                allLatestMovies.map { GridMediaItem.MovieItem(it) }
                            }
                            "popular" -> {
                                val allPopularMovies = mutableListOf<Movie>()
                                for (page in 1..30) {
                                    val response = repository.getPopularMovies(page) // Pass page
                                    if (response != null && response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allPopularMovies.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load popular movies for page $page: ${response?.errorBody()?.string()}")
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
                                    val response = repository.fetchTrendingTVShows(page) // Pass page
                                    if (response != null && response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allTrendingTvShows.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load trending TV shows for page $page: ${response?.errorBody()?.string()}")
                                    }
                                }
                                allTrendingTvShows.map { GridMediaItem.TVShowItem(it) }
                            }
                            "latest" -> {
                                val allLatestTvShows = mutableListOf<TVShow>()
                                for (page in 1..30) {
                                    val response = repository.fetchLatestTVShows(page) // Pass page
                                    if (response != null && response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allLatestTvShows.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load latest TV shows for page $page: ${response?.errorBody()?.string()}")
                                    }
                                }
                                allLatestTvShows.map { GridMediaItem.TVShowItem(it) }
                            }
                            "popular" -> {
                                val allPopularTvShows = mutableListOf<TVShow>()
                                for (page in 1..30) {
                                    val response = repository.getPopularTVShows(page) // Pass page
                                    if (response != null && response.isSuccessful) {
                                        response.body()?.results?.let {
                                            allPopularTvShows.addAll(it)
                                        }
                                    } else {
                                        Log.e("FetchGridItems", "Failed to load popular TV shows for page $page: ${response?.errorBody()?.string()}")
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
                isLoading.postValue(false) // Set loading to false after completion (success or failure)
            }
        }
    }


}
