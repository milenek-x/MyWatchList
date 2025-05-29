package com.milenekx.mywatchlist.util

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import android.os.Bundle // Import Bundle
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.GridMediaItem
import com.milenekx.mywatchlist.data.model.MixedMediaItem

/**
 * Navigates to the ItemDetailsFragment with the details of a given media item.
 * This function is designed to work from any fragment that has access to a NavController.
 *
 * @param item The GridMediaItem (MovieItem or TVShowItem) whose details are to be displayed.
 * @param navController The NavController instance used for navigation. This should be obtained
 * from the calling Fragment or Activity (e.g., using findNavController()).
 */
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
            posterPath = item.movie.poster_path
        }
        is GridMediaItem.TVShowItem -> {
            mediaId = item.tvShow.id.toString()
            mediaType = "tv"
            title = item.tvShow.name
            overview = item.tvShow.overview
            posterPath = item.tvShow.poster_path
        }
    }

    // Ensure title and posterPath are not null before creating the action
    if (title != null && posterPath != null) {
        val bundle = Bundle().apply {
            putString("mediaId", mediaId)
            putString("mediaType", mediaType)
            putString("title", title)
            putString("overview", overview)
            putString("posterPath", posterPath)
        }
        // Navigate directly to the itemDetailsFragment using its ID
        navController.navigate(R.id.itemDetailsFragment, bundle)
    } else {
        // Handle cases where essential data (title or posterPath) might be missing
        // You might want to log this or show a Toast message to the user.
        println("Error: Cannot navigate to details. Missing title or poster path for item: $item")
    }
}

// Overload for Movie objects
fun navigateToItemDetails(movie: com.milenekx.mywatchlist.data.model.Movie, navController: NavController) {
    navigateToItemDetails(GridMediaItem.MovieItem(movie), navController)
}

// Overload for TVShow objects
fun navigateToItemDetails(tvShow: com.milenekx.mywatchlist.data.model.TVShow, navController: NavController) {
    navigateToItemDetails(GridMediaItem.TVShowItem(tvShow), navController)
}

// Overload for MixedMediaItem objects
fun navigateToItemDetails(mixedItem: MixedMediaItem, navController: NavController) {
    val mediaId = mixedItem.id.toString()
    val mediaType = mixedItem.media_type
    val title = if (mixedItem.media_type == "movie") mixedItem.title else mixedItem.name // Use title for movie, name for TV
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
