package com.milenekx.mywatchlist.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.MixedMediaItem
import com.milenekx.mywatchlist.data.repository.MovieRepository
import com.milenekx.mywatchlist.ui.adapter.SearchGridAdapter
import com.milenekx.mywatchlist.util.navigateToItemDetails
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var searchBar: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchGridAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var btnBack: ImageButton
    private lateinit var btnClear: ImageButton

    private lateinit var mediaType: String

    private val repository = MovieRepository()
    private var searchJob: Job? = null

    private val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaType = arguments?.getString("mediaType") ?: "movie"

        searchBar = view.findViewById(R.id.etSearchBar)
        btnBack = view.findViewById(R.id.btnBack)
        btnClear = view.findViewById(R.id.btnClear)

        searchBar.hint = when (mediaType) {
            "tv" -> "Search TV Shows..."
            "movie" -> "Search Movies..."
            else -> "Search"
        }

        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)
        progressBar = view.findViewById(R.id.progressBarSearch)

        adapter = SearchGridAdapter(emptyList(), genreMap) { item ->
            navigateToItemDetails(item, findNavController())
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = adapter

        searchBar.addTextChangedListener { text ->
            searchJob?.cancel()
            val query = text?.toString()?.trim().orEmpty()

            if (query.length >= 2) {
                searchJob = lifecycleScope.launch {
                    progressBar.visibility = View.VISIBLE

                    Log.d("MEDIATYPE", mediaType)

                    val items: List<MixedMediaItem> = when (mediaType) {
                        "tv" -> {
                            val tvShows = repository.searchTVShows(query).body()?.results ?: emptyList()
                            tvShows.map {
                                MixedMediaItem(
                                    id = it.id,
                                    title = it.name,
                                    name = it.name,
                                    poster_path = it.poster_path,
                                    overview = it.overview,
                                    media_type = "tv",
                                    vote_average = 0.0,
                                    genre_ids = it.genre_ids,
                                    year = it.first_air_date.take(4),
                                    homepage = it.homepage  // Use first_air_date here
                                )
                            }
                        }
                        else -> {
                            val movies = repository.searchMovies(query).body()?.results ?: emptyList()
                            movies.map {
                                MixedMediaItem(
                                    id = it.id,
                                    title = it.title,
                                    name = it.title,
                                    poster_path = it.poster_path,
                                    overview = it.overview,
                                    media_type = "movie",
                                    vote_average = 0.0,
                                    genre_ids = it.genre_ids,
                                    year = it.release_date.take(4),
                                    homepage = it.homepage  // Use release_date here
                                )
                            }
                        }
                    }


                    adapter.updateData(items)

                    progressBar.visibility = View.GONE
                }
            } else {
                adapter.updateData(emptyList())
            }
        }

        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnClear.setOnClickListener {
            searchBar.text.clear()
        }
    }
}
