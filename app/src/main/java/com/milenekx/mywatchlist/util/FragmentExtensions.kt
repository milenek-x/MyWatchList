package com.milenekx.mywatchlist.util

import androidx.navigation.NavController
import android.os.Bundle
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.GridMediaItem
import com.milenekx.mywatchlist.data.model.MixedMediaItem

fun navigateToItemDetails(item: GridMediaItem, navController: NavController) {
    val mediaId: String
    val mediaType: String
    val title: String?
    val overview: String?
    val posterPath: String?

    when (item) {
        is GridMediaItem.MovieItem -> {
            mediaId = item.movie.id.toString()
            mediaType = "movie"
            title = item.movie.title
            overview = item.movie.overview
            posterPath = item.movie.posterPath
        }
        is GridMediaItem.TVShowItem -> {
            mediaId = item.tvShow.id.toString()
            mediaType = "tv"
            title = item.tvShow.name
            overview = item.tvShow.overview
            posterPath = item.tvShow.posterPath
        }
    }

    if (title != null && posterPath != null) {
        val bundle = Bundle().apply {
            putString("mediaId", mediaId)
            putString("mediaType", mediaType)
            putString("title", title)
            putString("overview", overview)
            putString("posterPath", posterPath)
        }
        navController.navigate(R.id.itemDetailsFragment, bundle)
    } else {
        println("Error: Cannot navigate to details. Missing title or poster path for item: $item")
    }
}

fun navigateToItemDetails(movie: com.milenekx.mywatchlist.data.model.Movie, navController: NavController) {
    navigateToItemDetails(GridMediaItem.MovieItem(movie), navController)
}

fun navigateToItemDetails(tvShow: com.milenekx.mywatchlist.data.model.TVShow, navController: NavController) {
    navigateToItemDetails(GridMediaItem.TVShowItem(tvShow), navController)
}

fun navigateToItemDetails(mixedItem: MixedMediaItem, navController: NavController) {
    val mediaId = mixedItem.id.toString()
    val mediaType = mixedItem.media_type
    val title = if (mixedItem.media_type == "movie") mixedItem.title else mixedItem.name
    val overview = mixedItem.overview
    val posterPath = mixedItem.poster_path

    if (title != null && posterPath != null) {
        val bundle = Bundle().apply {
            putString("mediaId", mediaId)
            putString("mediaType", mediaType)
            putString("title", title)
            putString("overview", overview)
            putString("posterPath", posterPath)
        }
        navController.navigate(R.id.itemDetailsFragment, bundle)
    } else {
        println("Error: Cannot navigate to details. Missing title/name or poster path for mixed item: $mixedItem")
    }
}
