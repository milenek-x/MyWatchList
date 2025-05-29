package com.milenekx.mywatchlist.data.model

sealed class GridMediaItem {
    data class MovieItem(val movie: Movie) : GridMediaItem()
    data class TVShowItem(val tvShow: TVShow) : GridMediaItem()
}
