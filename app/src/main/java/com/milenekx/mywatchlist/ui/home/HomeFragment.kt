package com.milenekx.mywatchlist.ui.home

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.MixedMediaItem
import com.milenekx.mywatchlist.data.repository.Repository
import com.milenekx.mywatchlist.ui.adapter.HorizontalMovieAdapter
import com.milenekx.mywatchlist.ui.adapter.HorizontalTVAdapter
import com.milenekx.mywatchlist.ui.adapter.PopularAdapter
import com.milenekx.mywatchlist.util.navigateToItemDetails
import com.milenekx.mywatchlist.util.openWebPage
import com.milenekx.mywatchlist.viewmodel.HomeViewModel
import com.milenekx.mywatchlist.viewmodel.HomeViewModelFactory
import com.milenekx.mywatchlist.viewmodel.TrendingType
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel

    private lateinit var popularAdapter: PopularAdapter
    private lateinit var trendingAdapter: HorizontalMovieAdapter
    private lateinit var trendingTvAdapter: HorizontalTVAdapter
    private lateinit var latestMoviesAdapter: HorizontalMovieAdapter
    private lateinit var latestTvAdapter: HorizontalTVAdapter

    private lateinit var rvPopular: RecyclerView
    private lateinit var rvTrending: RecyclerView
    private lateinit var rvLatestMovies: RecyclerView
    private lateinit var rvLatestTv: RecyclerView
    private lateinit var rgTrendingToggle: RadioGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPopular = view.findViewById(R.id.rvPopular)
        rvTrending = view.findViewById(R.id.rvTrending)
        rvLatestMovies = view.findViewById(R.id.rvLatestMovies)
        rvLatestTv = view.findViewById(R.id.rvLatestTv)
        rgTrendingToggle = view.findViewById(R.id.rgTrendingToggle)

        val repository = Repository()
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        popularAdapter = PopularAdapter(
            emptyList(),
            onDetailClick = { item ->
                navigateToItemDetails(item, findNavController()) },
            onWatchClick = { mixedMediaItem: MixedMediaItem ->
                lifecycleScope.launch {
                    val homepageUrl = viewModel.getHomepageUrl(mixedMediaItem.id.toString(), mixedMediaItem.media_type)
                    openWebPage(requireContext(), homepageUrl)
                }
            }
        )
        rvPopular.adapter = popularAdapter
        rvPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvPopular)

        val itemCount = popularAdapter.realItemCount
        if (itemCount > 0) {
            val middle = Int.MAX_VALUE / 2
            val offset = middle % itemCount
            rvPopular.scrollToPosition(middle - offset)
        }

        trendingAdapter = HorizontalMovieAdapter(emptyList()) { movie ->
            navigateToItemDetails(movie, findNavController())
        }

        trendingTvAdapter = HorizontalTVAdapter(emptyList()) { tvShow ->
            navigateToItemDetails(tvShow, findNavController())
        }

        rvTrending.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvTrending.adapter = trendingAdapter // default to movies

        latestMoviesAdapter = HorizontalMovieAdapter(emptyList()) { movie ->
            navigateToItemDetails(movie, findNavController())
        }
        rvLatestMovies.adapter = latestMoviesAdapter
        rvLatestMovies.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        latestTvAdapter = HorizontalTVAdapter(emptyList()) { tvShow ->
            navigateToItemDetails(tvShow, findNavController())
        }
        rvLatestTv.adapter = latestTvAdapter
        rvLatestTv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModel.popularMixedContent.observe(viewLifecycleOwner) { movies ->
            popularAdapter.updateData(movies ?: emptyList())
        }

        viewModel.trendingMovies.observe(viewLifecycleOwner) { movies ->
            trendingAdapter.updateData(movies ?: emptyList())
            rvTrending.adapter = trendingAdapter
        }

        viewModel.trendingTVShows.observe(viewLifecycleOwner) { tvShows ->
            trendingTvAdapter.updateData(tvShows ?: emptyList())
            rvTrending.adapter = trendingTvAdapter
        }

        viewModel.latestMovies.observe(viewLifecycleOwner) { movies ->
            latestMoviesAdapter.updateData(movies ?: emptyList())
        }

        viewModel.latestTvSeries.observe(viewLifecycleOwner) { tvShows ->
            latestTvAdapter.updateData(tvShows ?: emptyList())
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        rgTrendingToggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbTrendingMovies -> {
                    viewModel.fetchContent(TrendingType.MOVIES)
                }
                R.id.rbTrendingTv -> {
                    viewModel.fetchContent(TrendingType.TV_SHOWS)
                }
            }
        }

        viewModel.fetchContent(TrendingType.MOVIES)
    }
}
