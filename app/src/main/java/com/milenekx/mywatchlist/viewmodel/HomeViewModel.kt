package com.milenekx.mywatchlist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milenekx.mywatchlist.data.model.MixedMediaItem
import com.milenekx.mywatchlist.data.model.Movie
import com.milenekx.mywatchlist.data.model.TVShow
import com.milenekx.mywatchlist.data.repository.MovieRepository
import kotlinx.coroutines.launch



class HomeViewModel(private val repository: MovieRepository) : ViewModel() {

    val popularMixedContent = MutableLiveData<List<MixedMediaItem>>()
    val trendingMovies = MutableLiveData<List<Movie>>()
    val trendingTVShows = MutableLiveData<List<TVShow>>()
    val latestMovies = MutableLiveData<List<Movie>>()
    val latestTvSeries = MutableLiveData<List<TVShow>>()
    val errorMessage = MutableLiveData<String>()

    fun fetchContent(trendingType: TrendingType) {
        viewModelScope.launch {
            try {
                val popularResponse = repository.getPopularMixedContent()
                if (popularResponse.isSuccessful) {
                    popularMixedContent.postValue(popularResponse.body()?.results)
                }

                when (trendingType) {
                    TrendingType.MOVIES -> {
                        val trendingMoviesResponse = repository.fetchTrendingMovies(1)
                        trendingMovies.postValue(trendingMoviesResponse.body()?.results)
                    }
                    TrendingType.TV_SHOWS -> {
                        val trendingTVShowsResponse = repository.fetchTrendingTVShows(1)
                        trendingTVShows.postValue(trendingTVShowsResponse.body()?.results)
                    }
                }

                val latestMoviesResponse = repository.fetchLatestMovies(1)
                latestMovies.postValue(latestMoviesResponse.body()?.results)

                val latestTvSeriesResponse = repository.fetchLatestTVShows(1)
                latestTvSeries.postValue(latestTvSeriesResponse.body()?.results)

            } catch (e: Exception) {
                errorMessage.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }

    // NEW: Function to get homepage from ViewModel
    suspend fun getHomepageUrl(mediaId: String, mediaType: String): String? {
        return try {
            if (mediaType == "movie") {
                repository.getMovieHomepage(mediaId)
            } else if (mediaType == "tv") {
                repository.getTvShowHomepage(mediaId)
            } else {
                null
            }
        } catch (e: Exception) {
            errorMessage.postValue("Error fetching homepage: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

